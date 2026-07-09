package com.arduia.expense.storage

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSMutableData
import platform.Foundation.appendBytes
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecRandomCopyBytes
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecRandomDefault
import platform.Security.kSecReturnData
import platform.Security.kSecValueData
import platform.darwin.OSStatus

/**
 * Keychain-backed mirror of AndroidDatabaseKeyManager's first-launch bootstrap: generates a random
 * SQLCipher passphrase via SecRandomCopyBytes and stores it as a generic-password Keychain item.
 * PIN-derived rekey/biometric gating stay deferred, exactly as on Android (design §5.2).
 *
 * CFDictionary construction and the CFBridgingRetain/Release lifecycle follow the pattern used by
 * russhwolf/multiplatform-settings' KeychainSettings (Apache 2.0) — building the query with
 * `CFDictionaryCreate` rather than casting a Kotlin Map to NSDictionary, which has reported crashes
 * in Kotlin/Native's cinterop bridge (KT-40625).
 *
 * Runtime-unverified: this environment can only klib-compile iOS code (no macOS/simulator), so the
 * Keychain read/write round-trip has not been exercised on a device/simulator — verify before
 * relying on it in the iosApp phase.
 */
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
class IosDatabaseKeyManager(
    private val service: String = "com.arduia.expense.database",
    private val account: String = "proexpense_db_key",
) : DatabaseKeyManager {
    override fun getOrCreateDatabaseKey(): ByteArray {
        readKey()?.let { return it }
        val key = ByteArray(KEY_SIZE_BYTES)
        val status =
            key.usePinned { pinned ->
                SecRandomCopyBytes(kSecRandomDefault, KEY_SIZE_BYTES.convert(), pinned.addressOf(0))
            }
        check(status == 0) { "SecRandomCopyBytes failed with status $status" }
        persistKey(key)
        return key
    }

    private fun readKey(): ByteArray? =
        cfRetain(service, account) { cfService, cfAccount ->
            val result = alloc<CFTypeRefVar>()
            val status =
                keychainOperation(
                    cfService,
                    kSecAttrAccount to cfAccount,
                    kSecReturnData to kCFBooleanTrue,
                    kSecMatchLimit to kSecMatchLimitOne,
                ) { SecItemCopyMatching(it, result.ptr) }
            if (status == errSecItemNotFound) return@cfRetain null
            check(status == errSecSuccess) { "SecItemCopyMatching failed with status $status" }
            (CFBridgingRelease(result.value) as? NSData)?.toByteArray()
        }

    private fun persistKey(key: ByteArray) {
        cfRetain(service, account, key.toNSData()) { cfService, cfAccount, cfValue ->
            val status =
                keychainOperation(
                    cfService,
                    kSecAttrAccount to cfAccount,
                    kSecValueData to cfValue,
                ) { SecItemAdd(it, null) }
            check(status == errSecSuccess) { "SecItemAdd failed with status $status" }
        }
    }

    private inline fun MemScope.keychainOperation(
        cfService: CFTypeRef?,
        vararg input: Pair<CFStringRef?, CFTypeRef?>,
        operation: (CFDictionaryRef?) -> OSStatus,
    ): OSStatus {
        val query =
            cfDictionaryOf(
                mapOf(kSecClass to kSecClassGenericPassword, kSecAttrService to cfService, *input),
            )
        val status = operation(query)
        CFBridgingRelease(query)
        return status
    }

    private companion object {
        const val KEY_SIZE_BYTES = 32
    }
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
private fun MemScope.cfDictionaryOf(map: Map<CFStringRef?, CFTypeRef?>): CFDictionaryRef? {
    val keys = allocArrayOf(*map.keys.toTypedArray())
    val values = allocArrayOf(*map.values.toTypedArray())
    return CFDictionaryCreate(
        kCFAllocatorDefault,
        keys.reinterpret(),
        values.reinterpret(),
        map.size.convert(),
        null,
        null,
    )
}

@OptIn(ExperimentalForeignApi::class)
private inline fun <T> cfRetain(
    value1: Any?,
    value2: Any?,
    block: MemScope.(CFTypeRef?, CFTypeRef?) -> T,
): T =
    memScoped {
        val cfValue1 = CFBridgingRetain(value1)
        val cfValue2 = CFBridgingRetain(value2)
        try {
            block(cfValue1, cfValue2)
        } finally {
            CFBridgingRelease(cfValue1)
            CFBridgingRelease(cfValue2)
        }
    }

@OptIn(ExperimentalForeignApi::class)
private inline fun <T> cfRetain(
    value1: Any?,
    value2: Any?,
    value3: Any?,
    block: MemScope.(CFTypeRef?, CFTypeRef?, CFTypeRef?) -> T,
): T =
    memScoped {
        val cfValue1 = CFBridgingRetain(value1)
        val cfValue2 = CFBridgingRetain(value2)
        val cfValue3 = CFBridgingRetain(value3)
        try {
            block(cfValue1, cfValue2, cfValue3)
        } finally {
            CFBridgingRelease(cfValue1)
            CFBridgingRelease(cfValue2)
            CFBridgingRelease(cfValue3)
        }
    }

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
private fun ByteArray.toNSData(): NSData {
    val data = NSMutableData()
    if (isEmpty()) return data
    usePinned { pinned -> data.appendBytes(pinned.addressOf(0), size.convert()) }
    return data
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val data: CPointer<ByteVar> = bytes!!.reinterpret()
    return ByteArray(length.toInt()) { index -> data[index] }
}
