package com.arduia.expense.feature.importexport

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

actual object HmacSha256 {
    actual fun sign(key: String, data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key.encodeToByteArray(), "HmacSHA256"))
        val bytes = mac.doFinal(data.encodeToByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    actual fun verify(key: String, data: String, signature: String): Boolean =
        sign(key, data).equals(signature, ignoreCase = true)
}
