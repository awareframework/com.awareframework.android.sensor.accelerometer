package com.awareframework.example_accelerometer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.awareframework.android.core.db.Engine
import com.awareframework.android.core.manager.DbSyncManager
import com.awareframework.android.sensor.accelerometer.Accelerometer

class MainActivity : AppCompatActivity() {

    lateinit var accelerometer: Accelerometer
    lateinit var syncManager: DbSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accelerometer = Accelerometer.Builder(this)
                .setDebug(true)
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .setDatabaseHost("http://10.0.2.2:3000/insert")
                .build()
        syncManager = DbSyncManager.Builder(this)
                .setDebug(true)
                .setBatteryChargingOnly(false)
                .setSyncInterval(0.2f)
                .setWifiOnly(false)
                .build()

        accelerometer.start()

//        syncManager.start()

        val handler = Handler()
        handler.postDelayed({
            accelerometer.sync(true)
        }, 10000)
    }

    override fun onDestroy() {
        super.onDestroy()
        accelerometer.stop()
        syncManager.stop()
    }
}
