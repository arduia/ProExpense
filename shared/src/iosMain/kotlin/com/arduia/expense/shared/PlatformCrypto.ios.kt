package com.arduia.expense.shared

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.kCCPBKDF2
import platform.CoreCrypto.kCCPRFHmacAlgSHA256
import platform.Security.SecRandomCopyBytes
import platform.Security.errSecSuccess
import platform.Security.kSecRandomDefault

private const val BITS_PER_BYTE = 8

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
actual fun platformSecureRandomBytes(size: Int): ByteArray {
    val out = ByteArray(size)
    val status =
        out.usePinned { pinned ->
            SecRandomCopyBytes(kSecRandomDefault, size.convert(), pinned.addressOf(0))
        }
    check(status == errSecSuccess) { "SecRandomCopyBytes failed with status $status" }
    return out
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
actual fun platformPbkdf2Sha256Hex(
    password: String,
    salt: ByteArray,
    iterations: Int,
    keyLengthBits: Int,
): String {
    // cinterop maps `const char *password` to String?, so the password is passed by value while
    // passwordLen must still be its UTF-8 byte length (not its Char count).
    val passwordLength = password.encodeToByteArray().size
    val derived = ByteArray(keyLengthBits / BITS_PER_BYTE)
    val status =
        salt.usePinned { saltPinned ->
            derived.usePinned { derivedPinned ->
                CCKeyDerivationPBKDF(
                    algorithm = kCCPBKDF2,
                    password = password,
                    passwordLen = passwordLength.convert(),
                    salt = saltPinned.addressOf(0).reinterpret(),
                    saltLen = salt.size.convert(),
                    prf = kCCPRFHmacAlgSHA256,
                    rounds = iterations.convert(),
                    derivedKey = derivedPinned.addressOf(0).reinterpret(),
                    derivedKeyLen = derived.size.convert(),
                )
            }
        }
    check(status == 0) { "CCKeyDerivationPBKDF failed with status $status" }
    return derived.toLowerHex()
}
