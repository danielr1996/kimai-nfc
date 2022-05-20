package de.danielr1996.kimainfc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

/**
 * Activity bound to an nfc intent filter, needed because Android doesnt allow a service to be started directly by an intent filter
 */
class ServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, KimaiService::class.java)
        startService(intent)
        finish()
    }
}