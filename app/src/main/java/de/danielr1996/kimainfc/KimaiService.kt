package de.danielr1996.kimainfc

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.*
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class KimaiService : Service() {
    private lateinit var url: String;
    private lateinit var username: String;
    private lateinit var apipassword: String;
    private lateinit var project: String;
    private lateinit var activity: String;
    private lateinit var q: RequestQueue
    private lateinit var preferences: SharedPreferences

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        preferences = getSharedPreferences(getString(R.string.sharedPrefKey), Context.MODE_PRIVATE)
        url=preferences.getString("url","").toString()
        username=preferences.getString("username","").toString()
        apipassword=preferences.getString("password","").toString()
        project = preferences.getString("project","").toString()
        activity = preferences.getString("activity","").toString()
        val cache = DiskBasedCache(cacheDir, 1024*1024)
        val network = BasicNetwork(HurlStack())
        q = RequestQueue(cache,network).apply { start() }
    }

    fun clockin(): Request<JSONObject> {
        val now = getNow()
        val payload = JSONObject()
        payload.put("project", project.toInt())
        payload.put("activity", activity.toInt())
        return object : JsonObjectRequest(Method.POST, "$url/api/timesheets", payload,
            {
                Toast.makeText(applicationContext, "Eingestempelt um $now", Toast.LENGTH_SHORT).show();
            },
            { error->error(error)}) {
            override fun getHeaders(): Map<String, String> { return getAuthHeaders() }
        }
    }

    fun clockout(id: Int): Request<JSONObject> {
        val now = getNow()
        val payload = JSONObject()
        return object: JsonObjectRequest(Method.PATCH, "$url/api/timesheets/$id/stop", payload,
            {
                Toast.makeText(applicationContext, "Ausgestempelt um $now", Toast.LENGTH_SHORT).show();
            } ,
            {error->error(error)}){
            override fun getHeaders(): Map<String, String> {return getAuthHeaders()}
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val activeTimeSheetRequest = object: JsonArrayRequest(
            Method.GET, "$url/api/timesheets/active", null,
            {activeTimeSheets ->
                Log.i("kimai/KimaiService","discovered ${activeTimeSheets.length()} timesheets")
                if(activeTimeSheets.length() >0){
                    val currentTimeSheetId = activeTimeSheets.getJSONObject(0).getInt("id");
                    q.add(clockout(currentTimeSheetId))
                }else{
                    q.add(clockin())
                }
            } ,
            {error ->error(error)}){
            override fun getHeaders(): Map<String, String> {return getAuthHeaders()}
        }
        q.add(activeTimeSheetRequest)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getNow(): String {
        val date = LocalDateTime.now();
        val formatter = DateTimeFormatter.ofPattern("HH : mm")
        return formatter.format(date);
    }

    fun getAuthHeaders() : Map<String, String>{
        val params = HashMap<String, String>()
        params["X-AUTH-USER"] = username
        params["X-AUTH-TOKEN"] = apipassword
        return params
    }

    fun error(error: VolleyError){
    }
}