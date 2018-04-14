package com.awareframework.example_accelerometer

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.awareframework.android.core.db.Engine
import com.awareframework.android.core.manager.DbSyncManager
import com.awareframework.android.sensor.accelerometer.Accelerometer
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var accelerometer: Accelerometer
    lateinit var syncManager: DbSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)
        } else {
            startSensors()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> startSensors()
        }
    }

    fun startSensors() {
        val prefs = getSharedPreferences("deviceId", Context.MODE_PRIVATE)
        val deviceId = prefs.getString("deviceId", UUID.randomUUID().toString())
        prefs.edit().putString("deviceId", deviceId).apply()

        accelerometer = Accelerometer.Builder(this)
                .setDebug(true)
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .setDatabasePath("database-name")
                .setDatabaseHost("https://node.awareframework.com/insert")
                .setDeviceId(deviceId)
                .build()
        syncManager = DbSyncManager.Builder(this)
                .setDebug(true)
                .setBatteryChargingOnly(false)
                .setSyncInterval(0.2f)
                .setWifiOnly(false)
                .build()

        accelerometer.start()

        syncManager.start()

//        val handler = Handler()
//        handler.postDelayed({
//            accelerometer.sync(true)
//        }, 10000)
    }

    override fun onDestroy() {
        super.onDestroy()
        accelerometer.stop()
        syncManager.stop()
    }
}
