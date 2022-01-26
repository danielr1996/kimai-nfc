package de.danielr1996.kimainfc

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {
    private lateinit var etUrl: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etActivity: EditText
    private lateinit var etProject: EditText
    private lateinit var btnSave: Button
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        etUrl = findViewById(R.id.etUrl)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etActivity = findViewById(R.id.etActivity)
        etProject = findViewById(R.id.etProject)
        btnSave = findViewById(R.id.btnSave)
        preferences = getSharedPreferences(getString(R.string.sharedPrefKey), Context.MODE_PRIVATE)
        btnSave.setOnClickListener { onSave() }
        etUrl.setText(preferences.getString("url","").toString())
        etUsername.setText(preferences.getString("username","").toString())
        etPassword.setText(preferences.getString("password","").toString())
        etActivity.setText(preferences.getString("activity","").toString())
        etProject.setText(preferences.getString("project","").toString())
    }

    fun onSave(){
        with (preferences.edit()){
            putString("url",etUrl.text.toString())
            putString("username",etUsername.text.toString())
            putString("password",etPassword.text.toString())
            putString("activity",etActivity.text.toString())
            putString("project",etProject.text.toString())
            apply()
            Toast.makeText(applicationContext, "Einstellungen gespeichert", Toast.LENGTH_SHORT).show();
        }
    }
}