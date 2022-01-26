package de.danielr1996.kimainfc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class ServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("kimai/ServiceActivity","started ServiceActivity")
        val intent = Intent(this, KimaiService::class.java)
        startService(intent)
        finish()
    }
}