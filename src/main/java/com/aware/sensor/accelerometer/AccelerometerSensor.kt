package com.aware.sensor.accelerometer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.aware.sensor.accelerometer.db.Engine
import com.aware.sensor.accelerometer.model.AccelerometerDevice
import com.aware.sensor.accelerometer.model.AccelerometerEvent

/**
 * Implementation of Aware accelerometer in kotlin as a standalone service.
 * Utilizes db.Engine to support different kinds of databases.
 *
 * @author  sercant
 * @date 17/02/2018
 */
class AccelerometerSensor : Service(), SensorEventListener {

    val TAG = "com.aware.sensor.aclm"

//    open class Builder {
//        constructor() {
//
//        }
//    }

    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null

    private var sensorThread: HandlerThread? = null
    private var sensorHandler: Handler? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private var LAST_VALUES: Array<Float>? = null
    private var LAST_TS: Long = 0
    private var LAST_SAVE: Long = 0

    private val dataBuffer = ArrayList<AccelerometerEvent>()

    // Parameters of the sensor
    /**
     * Accelerometer frequency in microseconds: e.g.,
     * 0 - fastest
     * 20000 - game
     * 60000 - UI
     * 200000 - normal (default)
     */
    private var frequency = 200000

    /**
     * Accelerometer threshold (float).  Do not record consecutive points if
     * change in value of all axes is less than this.
     */
    private var threshold = 0.0

    /**
     * Discard sensor events that come in more often than frequency
     */
    private var enforceFrequency = false
    private var debugDbSlow = false
    private var sensorObserver: AWARESensorObserver? = null
    private var deviceID = ""
    private var label = ""
    private var debug = false
    private var wakeLockEnabled = false
    private var bufferSize = 250
    private var bufferTimeout = 30000
    //////////////////////////

    // TODO (sercant): data label stuff. Maybe not needed anymore?
//        const val ACTION_AWARE_ACCELEROMETER = "ACTION_AWARE_ACCELEROMETER"
//        const val ACTION_AWARE_ACCELEROMETER_LABEL = "ACTION_AWARE_ACCELEROMETER_LABEL"
//        const val EXTRA_LABEL = "label"
//
//        private val dataLabeler = DataLabel()


    interface AWARESensorObserver {
        fun onAccelerometerChanged(data: AccelerometerEvent)
    }

    override fun onCreate() {
        super.onCreate()

//        AUTHORITY = getAuthority()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorThread = HandlerThread(TAG)

        val sensorThread = sensorThread!!
        sensorThread.start()

        if (wakeLockEnabled) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
            wakeLock!!.acquire()
        }

        sensorHandler = Handler(sensorThread.looper)


        // TODO (sercant): data label stuff. Maybe not needed anymore?
//        val filter = IntentFilter()
//        filter.addAction(ACTION_AWARE_ACCELEROMETER_LABEL)
//        registerReceiver(dataLabeler, filter)

        if (debug) Log.d(TAG, "Accelerometer service created!")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // TODO (sercant): check permissions!!

        if (mAccelerometer == null) {
            if (debug) Log.w(TAG, "This device does not have an accelerometer!")
            stopSelf()
        } else {
            saveAccelerometerDevice(mAccelerometer)

            mSensorManager!!.registerListener(this, mAccelerometer, frequency, sensorHandler)
            LAST_SAVE = System.currentTimeMillis()

            if (debug) Log.d(TAG, "Accelerometer service active: $frequency ms")
        }

        return Service.START_STICKY
    }

    private fun saveAccelerometerDevice(acc: Sensor?) {
        if (acc == null) return

        val device = AccelerometerDevice()
        device.device_id = deviceID
        device.timestamp = System.currentTimeMillis()
        device.double_sensor_maximum_range = acc.maximumRange
        device.double_sensor_minimum_delay = acc.minDelay.toFloat()
        device.sensor_name = acc.name
        device.double_sensor_power_ma = acc.power
        device.double_sensor_resolution = acc.resolution
        device.sensor_type = acc.type.toString()
        device.sensor_vendor = acc.vendor
        device.sensor_version = acc.version.toString()

        Engine.getDefaultEngine().saveDeviceAsync(device)

        if (debug) Log.d(TAG, "Accelerometer device:" + device.toString())
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //We log current accuracy on the sensor changed event
    }

    override fun onSensorChanged(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()

        if (enforceFrequency && currentTime < LAST_TS + frequency / 1000)
            return

        if (LAST_VALUES != null && threshold > 0 && Math.abs(event.values[0] - LAST_VALUES!![0]) < threshold
                && Math.abs(event.values[1] - LAST_VALUES!![1]) < threshold
                && Math.abs(event.values[2] - LAST_VALUES!![2]) < threshold) {
            return
        }

        LAST_VALUES = arrayOf(event.values[0], event.values[1], event.values[2])

        val data = AccelerometerEvent()
        data.timestamp = currentTime
        data.eventTimestamp = event.timestamp
        data.device_id = deviceID
        data.double_values_0 = event.values[0]
        data.double_values_1 = event.values[1]
        data.double_values_2 = event.values[2]
        data.accuracy = event.accuracy
        data.label = label

        if (sensorObserver != null) {
            sensorObserver!!.onAccelerometerChanged(data)
        }

        dataBuffer.add(data)
        LAST_TS = currentTime

        if (dataBuffer.size < bufferSize && currentTime < LAST_SAVE + bufferTimeout) {
            return
        }

        val dataBuffer = dataBuffer.toTypedArray()
        this.dataBuffer.clear()

        try {
            if (!debugDbSlow) {
                Engine.getDefaultEngine().bulkInsertAsync(dataBuffer)
            }
        } catch (e: Exception) {
            if (debug) Log.d(TAG, e.message)
        }

        LAST_SAVE = currentTime
    }

    override fun onDestroy() {
        super.onDestroy()

        sensorHandler!!.removeCallbacksAndMessages(null)
        mSensorManager!!.unregisterListener(this, mAccelerometer)
        sensorThread!!.quit()
        if (wakeLockEnabled) {
            wakeLock!!.release()
        }

        // TODO (sercant): data label stuff. Maybe not needed anymore?
//        unregisterReceiver(dataLabeler)

        if (debug) Log.d(TAG, "Accelerometer service terminated...")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}