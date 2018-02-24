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

    companion object {
        val CONFIG_EXTRA_KEY = "accelerometer_config"
    }

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
    private var config: Accelerometer.AccelerometerConfig = Accelerometer.defaultConfig
    private var sensorObserver: AWARESensorObserver? = null


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

        if (config.wakeLockEnabled) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
            wakeLock!!.acquire()
        }

        sensorHandler = Handler(sensorThread.looper)


        // TODO (sercant): data label stuff. Maybe not needed anymore?
//        val filter = IntentFilter()
//        filter.addAction(ACTION_AWARE_ACCELEROMETER_LABEL)
//        registerReceiver(dataLabeler, filter)

        if (config.debug) Log.d(TAG, "Accelerometer service created!")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        // TODO (sercant): check permissions!!

        if (intent != null) {
            config = intent.extras.getParcelable(AccelerometerSensor.CONFIG_EXTRA_KEY)
        }

        if (mAccelerometer == null) {
            if (config.debug) Log.w(TAG, "This device does not have an accelerometer!")
            stopSelf()
        } else {
            saveAccelerometerDevice(mAccelerometer)

            mSensorManager!!.registerListener(this, mAccelerometer, config.frequency, sensorHandler)
            LAST_SAVE = System.currentTimeMillis()

            if (config.debug) Log.d(TAG, "Accelerometer service active: ${config.frequency} ms")
        }

        return Service.START_STICKY
    }

    private fun saveAccelerometerDevice(acc: Sensor?) {
        if (acc == null) return

        val device = AccelerometerDevice()
        device.device_id = config.deviceID
        device.timestamp = System.currentTimeMillis()
        device.double_sensor_maximum_range = acc.maximumRange
        device.double_sensor_minimum_delay = acc.minDelay.toFloat()
        device.sensor_name = acc.name
        device.double_sensor_power_ma = acc.power
        device.double_sensor_resolution = acc.resolution
        device.sensor_type = acc.type.toString()
        device.sensor_vendor = acc.vendor
        device.sensor_version = acc.version.toString()

        Engine.getDefaultEngine(applicationContext).saveDeviceAsync(device)

        if (config.debug) Log.d(TAG, "Accelerometer device:" + device.toString())
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //We log current accuracy on the sensor changed event
    }

    override fun onSensorChanged(event: SensorEvent) {
        val currentTime = System.currentTimeMillis()

        if (config.enforceFrequency && currentTime < LAST_TS + config.frequency / 1000)
            return

        if (LAST_VALUES != null && config.threshold > 0 && Math.abs(event.values[0] - LAST_VALUES!![0]) < config.threshold
                && Math.abs(event.values[1] - LAST_VALUES!![1]) < config.threshold
                && Math.abs(event.values[2] - LAST_VALUES!![2]) < config.threshold) {
            return
        }

        LAST_VALUES = arrayOf(event.values[0], event.values[1], event.values[2])

        val data = AccelerometerEvent()
        data.timestamp = currentTime
        data.eventTimestamp = event.timestamp
        data.device_id = config.deviceID
        data.double_values_0 = event.values[0]
        data.double_values_1 = event.values[1]
        data.double_values_2 = event.values[2]
        data.accuracy = event.accuracy
        data.label = config.label

        if (sensorObserver != null) {
            sensorObserver!!.onAccelerometerChanged(data)
        }

        dataBuffer.add(data)
        LAST_TS = currentTime

        if (dataBuffer.size < config.bufferSize && currentTime < LAST_SAVE + config.bufferTimeout) {
            return
        }

        val dataBuffer = dataBuffer.toTypedArray()
        this.dataBuffer.clear()

        try {
            if (!config.debugDbSlow) {
                Engine.getDefaultEngine(applicationContext).bulkInsertAsync(dataBuffer)
            }
        } catch (e: Exception) {
            if (config.debug) Log.d(TAG, e.message)
        }

        LAST_SAVE = currentTime
    }

    override fun onDestroy() {
        super.onDestroy()

        sensorHandler!!.removeCallbacksAndMessages(null)
        mSensorManager!!.unregisterListener(this, mAccelerometer)
        sensorThread!!.quit()
        if (config.wakeLockEnabled) {
            wakeLock!!.release()
        }

        // TODO (sercant): data label stuff. Maybe not needed anymore?
//        unregisterReceiver(dataLabeler)

        if (config.debug) Log.d(TAG, "Accelerometer service terminated...")
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}