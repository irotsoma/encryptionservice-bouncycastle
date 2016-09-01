/*
 * Copyright (C) 2016  Irotsoma, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.irotsoma.cloudbackenc.encryptionservice.bouncycastle

import com.irotsoma.cloudbackenc.common.logger
import com.irotsoma.cloudbackenc.encryptionservice.EncryptionServiceKeyAlgorithms
import com.irotsoma.cloudbackenc.encryptionservice.EncryptionServiceKeyService
import com.irotsoma.cloudbackenc.encryptionservice.EncryptionServicePBKDFAlgorithms
import java.security.KeyPair
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Created by irotsoma on 8/25/2016.
 *
 * Bouncy Castle implementation of encryption key generation services
 */
class BouncyCastleKeyService: EncryptionServiceKeyService {
    val DEFAULT_PBKDF_ITERATIONS = 64000


    override fun generatePasswordBasedKey(password:String, salt: ByteArray): SecretKey? {
        return generatePasswordBasedKey(password, salt, EncryptionServicePBKDFAlgorithms.PBKDF2WithHmacSHA1, 128, DEFAULT_PBKDF_ITERATIONS)
    }

    override fun generatePasswordBasedKey(password:String, salt: ByteArray, algorithm: EncryptionServicePBKDFAlgorithms, keySize: Int): SecretKey? {
        return generatePasswordBasedKey(password, salt, algorithm, keySize, DEFAULT_PBKDF_ITERATIONS)
    }

    override fun generatePasswordBasedKey(password:String, salt: ByteArray, algorithm: EncryptionServicePBKDFAlgorithms, keySize: Int, iterations: Int): SecretKey? {
        val keySpec = PBEKeySpec(password.toCharArray(), salt, iterations, 128)
        val keyFactory = SecretKeyFactory.getInstance(algorithm.value, "BC")
        return keyFactory.generateSecret(keySpec)
    }


    companion object { val LOG by logger() }
    override fun generateSymmetricKey(): SecretKey? {
        return generateSymmetricKey(EncryptionServiceKeyAlgorithms.AES, 128, SecureRandom.getInstanceStrong())
    }
    override fun generateSymmetricKey(algorithm: EncryptionServiceKeyAlgorithms, keySize: Int): SecretKey? {
        return generateSymmetricKey(algorithm, keySize, SecureRandom.getInstanceStrong())
    }
    override fun generateSymmetricKey(algorithm: EncryptionServiceKeyAlgorithms, keySize: Int, secureRandom: SecureRandom): SecretKey? {
        try {
            val keyGen = KeyGenerator.getInstance(algorithm.value, "BC")
            keyGen.init(keySize, secureRandom)
            return keyGen.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            LOG.error("Unsupported algorithm: $algorithm, size: $keySize", e)
            return null
        }
    }





    override fun generateAsymmetricKeys(): KeyPair {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateAsymmetricKeys(algorithm: EncryptionServiceKeyAlgorithms, keySize: Int): KeyPair {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun generateAsymmetricKeys(algorithm: EncryptionServiceKeyAlgorithms, keySize: Int, secureRandom: SecureRandom): KeyPair {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}