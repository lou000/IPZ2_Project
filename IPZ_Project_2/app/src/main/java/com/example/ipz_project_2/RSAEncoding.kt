import java.io.ByteArrayOutputStream
import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import java.security.spec.X509EncodedKeySpec
import java.io.IOException
import java.security.*
import java.util.*
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec

/*
 * RSA Key Size: 4096
 * Cipher Type: RSA/ECB/OAEPWithSHA-256AndMGF1Padding
 */
class RSAEncoding {
    var privateKey: PrivateKey
    var publicKey: PublicKey

    companion object {
        @Throws(Exception::class)
        fun encryptMessage(plainText: String, publickey: String): String {
            val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publickey))
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.toByteArray()))
        }

        @Throws(Exception::class)
        fun decryptMessage(encryptedText: String?, privatekey: String): String {
            val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privatekey))
            return String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)))
        }





        // convert String publickey to Key object
        @Throws(GeneralSecurityException::class, IOException::class)
        fun loadPublicKey(stored: String): Key {
            val data: ByteArray = Base64.getDecoder().decode(stored.toByteArray())
            val spec = X509EncodedKeySpec(data)
            val fact = KeyFactory.getInstance("RSA")
            return fact.generatePublic(spec)
        }

        // Convert String private key to privateKey object
        @Throws(GeneralSecurityException::class)
        fun loadPrivateKey(key64: String): PrivateKey {
            val clear: ByteArray = Base64.getDecoder().decode(key64.toByteArray())
            val keySpec = PKCS8EncodedKeySpec(clear)
            val fact = KeyFactory.getInstance("RSA")
            val priv = fact.generatePrivate(keySpec)
            Arrays.fill(clear, 0.toByte())
            return priv
        }
    }

    init {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(4096)
        val pair = keyGen.generateKeyPair()
        privateKey = pair.private
        publicKey = pair.public
    }
} 