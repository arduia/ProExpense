package com.arduia.expense.feature.auth

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class AndroidPinKeyDeriver : PinKeyDeriver {
  override fun deriveKey(pin: String, salt: ByteArray): ByteArray {
    val spec = PBEKeySpec(
      pin.toCharArray(),
      salt,
      PinKeyDeriver.PBKDF2_ITERATIONS,
      PinKeyDeriver.KEY_LENGTH_BITS,
    )
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    return factory.generateSecret(spec).encoded
  }

  override fun generateSalt(length: Int): ByteArray {
    val salt = ByteArray(length)
    SecureRandom().nextBytes(salt)
    return salt
  }
}
