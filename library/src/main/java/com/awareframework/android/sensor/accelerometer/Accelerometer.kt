package com.awareframework.android.sensor.accelerometer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.awareframework.android.core.db.Engine
import com.awareframework.android.core.model.SensorConfig
import com.awareframework.android.core.model.SensorObserver


/**
 * Main interaction class with the sensor
 *
 * @author  sercant
 * @date 19/02/2018
 */
class Accelerometer private constructor(
        private var context: Context,
        config: AccelerometerConfig = defaultConfig
) : BroadcastReceiver() {

    companion object {
        const val ACTION_AWARE_ACCELEROMETER_START = "ACTION_AWARE_ACCELEROMETER_START"
        const val ACTION_AWARE_ACCELEROMETER_STOP = "ACTION_AWARE_ACCELEROMETER_STOP"

        const val ACTION_AWARE_ACCELEROMETER = "ACTION_AWARE_ACCELEROMETER"

        const val ACTION_AWARE_ACCELEROMETER_LABEL = "ACTION_AWARE_ACCELEROMETER_LABEL"
        const val EXTRA_LABEL = "label"

        const val DATA_TYPE = "DATA_ACCELEROMETER"

        val defaultConfig: AccelerometerConfig = AccelerometerConfig()
    }

    internal val TAG = "com.aware.accelerometer"

    var config: AccelerometerConfig = config
        private set

    data class AccelerometerConfig(
            /**
             * Accelerometer frequency in hertz per second: e.g.,
             * 0 - fastest
             * 1 - sample per second
             * 5 - sample per second
             * 20 - sample per second
             */
            var frequency: Int = 5,

            /**
             * Period to save data in minutes. (optional)
             */
            var period: Float = 1f,

            // TODO (sercant): enable if needed after meeting.
            /**
             * Accelerometer threshold (float).  Do not record consecutive points if
             * change in value of all axes is less than this.
             */
            var threshold: Float = 0f,

            var sensorObserver: SensorObserver? = null,
            var wakeLockEnabled: Boolean = true
    ) : SensorConfig(dbName = "aware_accelerometer.db")

    class Builder(private val context: Context) {

        private val config: AccelerometerConfig = AccelerometerConfig()

        /**
         * @param frequency sample count per second in hertz. (*fastest• default = 0)
         */
        fun setFrequency(frequency: Int) = apply { config.frequency = frequency }
        fun setThreshold(threshold: Float) = apply { config.threshold = threshold }

        /**
         * @param sensorObserver callback for live data updates.
         */
        fun setSensorObserver(sensorObserver: SensorObserver) = apply { config.sensorObserver = sensorObserver }

        /**
         * @param deviceId id of the device that will be associated with the events and the sensor. (default = "")
         */
        fun setDeviceId(deviceId: String) = apply { config.deviceId = deviceId }

        /**
         * @param label collected data will be labeled accordingly. (default = "")
         */
        fun setLabel(label: String) = apply { config.label = label }

        /**
         * @param debug enable/disable logging to Logcat. (default = false)
         */
        fun setDebug(debug: Boolean) = apply { config.debug = debug }
        //        fun setWakeLock(wakeLock: Boolean) = apply { config.wakeLockEnabled = wakeLock }

        /**
         * @param period period of database saves in minutes. (default = 1.0)
         */
        fun setPeriod(period: Float) = apply { config.period = period }

        /**
         * @param key encryption key for the database. (default = no encryption)
         */
        fun setDbKey(key: String) = apply { config.dbKey = key }

        /**
         * @param type which db engine to use for saving data. (default = NONE)
         */
        fun setDbType(type: Engine.DatabaseType) = apply { config.dbType = type }

        /**
         * Returns the accelerometer with the built configuration.
         */
        fun build(): Accelerometer = Accelerometer(context, config)
    }

    init {
        // TODO WARN (sercant): this will leak!! What should we do if we want to start and stop using
        // TODO WARN (sercant): receivers?
        setLabelReceiver(true)
    }

    fun start() {
        start(config)
    }

    fun start(config: AccelerometerConfig) {
        this.config = config
        AccelerometerSensor.CONFIG = config

//        if (config.wakeLockEnabled and (ContextCompat.checkSelfPermission(context, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED)) {
//            Log.e(TAG, "Permission for the WAKE_LOCK is not granted!")
//        } else {
        val intent = Intent(context, AccelerometerSensor::class.java)
        context.startService(intent)
//        }
    }

    fun stop() {
        context.stopService(Intent(context, AccelerometerSensor::class.java))
    }

    private fun setLabelReceiver(on: Boolean) {
        if (on) {
            val filter = IntentFilter()
            filter.addAction(ACTION_AWARE_ACCELEROMETER_LABEL)

            context.registerReceiver(this, filter)
        } else {
            context.unregisterReceiver(this)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_AWARE_ACCELEROMETER_START -> this.start()

            ACTION_AWARE_ACCELEROMETER_STOP -> this.stop()

            ACTION_AWARE_ACCELEROMETER_LABEL -> config.label = intent.getStringExtra(EXTRA_LABEL)
        }
    }
}