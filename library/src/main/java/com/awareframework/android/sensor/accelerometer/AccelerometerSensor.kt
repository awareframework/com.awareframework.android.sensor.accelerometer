package com.awareframework.android.sensor.accelerometer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.awareframework.android.core.AwareSensor
import com.awareframework.android.core.db.Engine
import com.awareframework.android.core.db.model.DbSyncConfig
import com.awareframework.android.sensor.accelerometer.model.AccelerometerDevice
import com.awareframework.android.sensor.accelerometer.model.AccelerometerEvent
import java.util.TimeZone
import kotlin.collections.ArrayList

private fun logd(msg: String) { if (AccelerometerSensor.CONFIG.debug) Log.d(AccelerometerSensor.TAG, msg) }
private fun logw(msg: String) { Log.w(AccelerometerSensor.TAG, msg) }

/**
 * Implementation of Aware accelerometer in kotlin as a standalone service.
 * Utilizes db.Engine to support different kinds of databases.
 *
 * @author  sercant
 * @date 17/02/2018
 */
class AccelerometerSensor : AwareSensor(), SensorEventListener {

    companion object {
        const val TAG = "com.aware.sensor.aclm"
        const val WAKELOCK_TAG = "awareframework:accelerometer"

        internal var CONFIG: Accelerometer.AccelerometerConfig = Accelerometer.defaultConfig

        internal fun startService(context: Context) {
            val intent = Intent(context, AccelerometerSensor::class.java)
            context.startService(intent)
        }

        internal fun stopService(context: Context) {
            context.stopService(Intent(context, AccelerometerSensor::class.java))
        }
    }

    private lateinit var mSensorManager: SensorManager
    private var mAccelerometer: Sensor? = null

    private lateinit var sensorThread: HandlerThread
    private lateinit var sensorHandler: Handler
    private var wakeLock: PowerManager.WakeLock? = null

    private lateinit var accelerometerSpecificReceiver: AccelerometerSpecificReceiver

    private var lastValues: Array<Float> = arrayOf(0f, 0f, 0f)
    private var lastTimestamp: Long = 0
    private var lastSavedAt: Long = 0

    private val dataBuffer = ArrayList<AccelerometerEvent>()

    override fun onCreate() {
        super.onCreate()

        dbEngine = Engine.Builder(applicationContext)
                .setPath(CONFIG.dbPath)
                .setType(CONFIG.dbType)
                .setEncryptionKey(CONFIG.dbEncryptionKey)
                .setHost(CONFIG.dbHost)
                .build()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorThread = HandlerThread(TAG)
        sensorThread.start()

        if (CONFIG.wakeLockEnabled) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)
            wakeLock!!.acquire()
        }

        sensorHandler = Handler(sensorThread.looper)

        accelerometerSpecificReceiver = AccelerometerSpecificReceiver(this)
        applicationContext.registerReceiver(accelerometerSpecificReceiver, accelerometerSpecificReceiver.getIntentFilter())

        logd("Accelerometer service created.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (mAccelerometer == null) {
            logw("This device does not have an accelerometer!")
            stopSelf()
        } else {
            saveAccelerometerDevice(mAccelerometer)

            val samplingPeriodUs = if(CONFIG.interval > 0) 1000000 / CONFIG.interval else 0
            mSensorManager.registerListener(this, mAccelerometer, samplingPeriodUs, sensorHandler)
            lastSavedAt = System.currentTimeMillis()

            logd("Accelerometer service active: ${CONFIG.interval} hz")
        }

        return Service.START_STICKY
    }

    private fun saveAccelerometerDevice(acc: Sensor?) {
        if (acc == null) return

        val device = AccelerometerDevice(CONFIG.deviceId, System.currentTimeMillis(), acc)

        // Save with id = 0L so there is only one entry on the table.
        dbEngine?.save(device, AccelerometerDevice.TABLE_NAME, 0L)

        logd("Saved accelerometer device: \n" + device.toString())
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // We log current accuracy on the sensor changed event
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.timestamp - lastTimestamp < CONFIG.interval / 1000) {
            // skip this event
            return
        }
        lastTimestamp = event.timestamp

        if (lastValues != null && CONFIG.threshold > 0
                && Math.abs(event.values[0] - lastValues!![0]) < CONFIG.threshold
                && Math.abs(event.values[1] - lastValues!![1]) < CONFIG.threshold
                && Math.abs(event.values[2] - lastValues!![2]) < CONFIG.threshold) {
            return
        }
        lastValues.forEachIndexed { index, fl ->
            lastValues[index] = event.values[index]
        }

        val currentTime = System.currentTimeMillis()
        val data = AccelerometerEvent().apply {
            timestamp = currentTime
            eventTimestamp = event.timestamp
            timezone = TimeZone.getDefault().rawOffset
            deviceId = CONFIG.deviceId
            x = event.values[0]
            y = event.values[1]
            z = event.values[2]
            accuracy = event.accuracy
            label = CONFIG.label
        }

        CONFIG.sensorObserver?.onDataChanged("", data, null)

        dataBuffer.add(data)

        if (currentTime - lastSavedAt < CONFIG.period * 60000) { // convert minute to ms
            // not ready to save yet
            return
        }
        lastSavedAt = currentTime

        val dataBuffer = this.dataBuffer.toTypedArray()
        this.dataBuffer.clear()

        try {
            logd("Saving buffer to database.")
            dbEngine?.save(dataBuffer, AccelerometerEvent.TABLE_NAME)

            //TODO (sercant): enable later
//            val accelerometerData = Intent(Accelerometer.ACTION_AWARE_ACCELEROMETER)
//            sendBroadcast(accelerometerData)
        } catch (e: Exception) {
            e.message ?: logw(e.message!!)
            e.printStackTrace()
        }
    }

    override fun onSync(intent: Intent?) {
        dbEngine?.startSync(AccelerometerEvent.TABLE_NAME)
        dbEngine?.startSync(AccelerometerDevice.TABLE_NAME, DbSyncConfig(removeAfterSync = false))
    }

    override fun onDestroy() {
        super.onDestroy()

        sensorHandler.removeCallbacksAndMessages(null)
        mSensorManager.unregisterListener(this, mAccelerometer)
        sensorThread.quit()
        if (CONFIG.wakeLockEnabled) {
            wakeLock?.release()
        }
        dbEngine?.close()
        dbEngine = null

        applicationContext.unregisterReceiver(accelerometerSpecificReceiver)

        logd("Accelerometer service terminated...")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    class AccelerometerBroadcastReceiver : AwareSensor.SensorBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context ?: return

            logd("Sensor broadcast received. action: " + intent?.action)

            when (intent?.action) {
                AwareSensor.SensorBroadcastReceiver.SENSOR_START_ENABLED -> {
                    logd("Sensor enabled: " + CONFIG.enabled)

                    if (CONFIG.enabled) {
                        startService(context)
                    }
                }

                Accelerometer.ACTION_AWARE_ACCELEROMETER_STOP,
                AwareSensor.SensorBroadcastReceiver.SENSOR_STOP_ALL -> {
                    logd("Stopping sensor.")
                    stopService(context)
                }

                Accelerometer.ACTION_AWARE_ACCELEROMETER_START -> {
                    startService(context)
                }
            }
        }
    }

    class AccelerometerSpecificReceiver(val sensor: AccelerometerSensor) : AwareSensor.SensorBroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context ?: return

            logd("Accelerometer broadcast received. action: " + intent?.action)

            when (intent?.action) {

                Accelerometer.ACTION_AWARE_ACCELEROMETER_LABEL -> {
                    AccelerometerSensor.CONFIG.label = intent.getStringExtra(Accelerometer.ACTION_AWARE_ACCELEROMETER_LABEL)
                }

                Accelerometer.ACTION_AWARE_ACCELEROMETER_SYNC -> {
                    sensor.onSync(intent)
                }
            }
        }

        fun getIntentFilter() : IntentFilter {
            val intentFilter = IntentFilter()
//            intentFilter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER_START)
//            intentFilter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER_STOP)
            intentFilter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER_LABEL)
            intentFilter.addAction(Accelerometer.ACTION_AWARE_ACCELEROMETER_SYNC)

            return intentFilter
        }
    }
}