package ru.mirea.mvolobueva.cryptoloader

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import ru.mirea.mvolobueva.cryptoloader.databinding.ActivityMainBinding
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<String> {

    private lateinit var binding: ActivityMainBinding
    private val TAG = this.javaClass.simpleName
    private val LoaderID = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEncrypt.setOnClickListener {
            val phrase = binding.editTextPhrase.text.toString()

            if (phrase.isNotEmpty()) {
                val key = generateKey()

                val shiper = encryptMsg(phrase, key)

                val bundle = Bundle()
                bundle.putByteArray(MyLoader.ARG_WORD, shiper)
                bundle.putByteArray("key", key.encoded)

                LoaderManager.getInstance(this).restartLoader(LoaderID, bundle, this)
            } else {
                Toast.makeText(this, "Введите фразу!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateKey(): SecretKey {
        return try {
            val sr = SecureRandom.getInstance("SHA1PRNG")
            sr.setSeed("any data used as random seed".toByteArray())
            val kg = KeyGenerator.getInstance("AES")
            kg.init(256, sr)
            SecretKeySpec(kg.generateKey().encoded, "AES")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun encryptMsg(message: String, secret: SecretKey): ByteArray {
        return try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secret)
            cipher.doFinal(message.toByteArray())
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<String> {
        if (id == LoaderID) {
            Toast.makeText(this, "onCreateLoader: $id", Toast.LENGTH_SHORT).show()
            return MyLoader(this, args)
        }
        throw IllegalArgumentException("Invalid loader id")
    }

    override fun onLoadFinished(loader: Loader<String>, data: String?) {
        if (loader.id == LoaderID) {
            Log.d(TAG, "onLoadFinished: $data")
            Toast.makeText(this, "Расшифровано: $data", Toast.LENGTH_LONG).show()
        }
    }

    override fun onLoaderReset(loader: Loader<String>) {
        Log.d(TAG, "onLoaderReset")
    }
}