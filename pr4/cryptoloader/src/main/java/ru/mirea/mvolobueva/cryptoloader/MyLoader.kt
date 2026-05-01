package ru.mirea.mvolobueva.cryptoloader

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.loader.content.AsyncTaskLoader
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class MyLoader(context: Context, args: Bundle?) : AsyncTaskLoader<String>(context) {

    private var firstName: String? = null

    companion object {
        const val ARG_WORD = "word"
    }

    init {
        if (args != null) {
            val cryptText = args.getByteArray(ARG_WORD)
            val key = args.getByteArray("key")

            if (key != null && cryptText != null) {
                val originalKey: SecretKey = SecretKeySpec(key, 0, key.size, "AES")
                firstName = decryptMsg(cryptText, originalKey)
            }
        }
    }

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }

    override fun loadInBackground(): String? {
        SystemClock.sleep(5000)
        return firstName
    }

    private fun decryptMsg(cipherText: ByteArray, secret: SecretKey): String {
        return try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secret)
            String(cipher.doFinal(cipherText))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}