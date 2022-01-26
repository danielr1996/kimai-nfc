package de.danielr1996.kimainfc

import android.app.Service
import android.content.Intent
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
    lateinit var url: String;
    lateinit var username: String;
    lateinit var apipassword: String;
    lateinit var project: Integer;
    lateinit var activity: Integer;
    lateinit var q: RequestQueue

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i("kimai/KimaiService","onCreate")
        url="https://your-kimai.instance"
        username="user@example.com"
        apipassword="password"
        project = Integer(1)
        activity = Integer(1)
        val cache = DiskBasedCache(cacheDir, 1024*1024)
        val network = BasicNetwork(HurlStack())
        q = RequestQueue(cache,network).apply { start() }
    }

    fun clockin(): Request<JSONObject> {
        val now = getNow()
        val payload = JSONObject()
        payload.put("begin", now)
        payload.put("project", project.toInt())
        payload.put("activity", activity.toInt())
        return object : JsonObjectRequest(Method.POST, "$url/api/timesheets", payload,
            {
                Log.i("kimai/KimaiService", "clocked in at $now")
                Toast.makeText(applicationContext, "Eingestempelt um $now", Toast.LENGTH_SHORT).show();
            },
            { error->error(error)}) {
            override fun getHeaders(): Map<String, String> { return getAuthHeaders() }
        }
    }

    fun clockout(id: Int): Request<JSONObject> {
        val now = getNow()
        val payload = JSONObject()
        payload.put("end",now)
        return object: JsonObjectRequest(Method.PATCH, "$url/api/timesheets/$id", payload,
            {
                Log.i("kimai/KimaiService", "clocked out at $now")
                Toast.makeText(applicationContext, "Ausgestempelt um $now", Toast.LENGTH_SHORT).show();
            } ,
            {error->error(error)}){
            override fun getHeaders(): Map<String, String> {return getAuthHeaders()}
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("kimai/KimaiService","onStartCommand")
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
        Log.i("kimai/KimaiService","onDestroy")
        super.onDestroy()
    }

    fun getNow(): String {
        val date = LocalDateTime.now();
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return formatter.format(date);
    }

    fun getAuthHeaders() : Map<String, String>{
        val params = HashMap<String, String>()
        params["X-AUTH-USER"] = username
        params["X-AUTH-TOKEN"] = apipassword
        return params
    }

    fun error(error: VolleyError){
        Log.i("kimai/KimaiService",error.networkResponse.statusCode.toString())
        Log.i("kimai/KimaiService",String(error.networkResponse.data))
    }
}