package com.aware.sensor.accelerometer

import android.content.Context
import android.content.Intent
import com.aware.sensor.accelerometer.model.AccelerometerEvent

/**
 * Main interaction class with the sensor
 *
 * @author  sercant
 * @date 19/02/2018
 */
class Accelerometer private constructor(
        private var context: Context,
        config: AccelerometerConfig = defaultConfig

) {
    var config: AccelerometerConfig = config
        private set

    interface SensorObserver {
        fun onAccelerometerChanged(data: AccelerometerEvent)
    }

    data class AccelerometerConfig(
            /**
             * Accelerometer frequency in microseconds: e.g.,
             * 0 - fastest
             * 20000 - game
             * 60000 - UI
             * 200000 - normal (default)
             */
            var frequency: Int = 200000,

            /**
             * Accelerometer threshold (float).  Do not record consecutive points if
             * change in value of all axes is less than this.
             */
            var threshold: Float = 0f,

            /**
             * Discard sensor events that come in more often than frequency
             */
            var enforceFrequency: Boolean = false,
            var debugDbSlow: Boolean = false,
            var sensorObserver: SensorObserver? = null,
            var deviceID: String = "",
            var label: String = "",
            var debug: Boolean = false,
            var wakeLockEnabled: Boolean = false,
            var bufferSize: Int = 250,
            var bufferTimeout: Int = 30000
    )

    class Builder(private val context: Context) {

        private val config: AccelerometerConfig = AccelerometerConfig()

        fun setFrequency(frequency: Int) = apply { config.frequency = frequency }
        fun setThreshold(threshold: Float) = apply { config.threshold = threshold }
        fun setEnforceFrequency(enforceFrequency: Boolean) = apply { config.enforceFrequency = enforceFrequency }
        fun setDebugDBSlow(debugDBSlow: Boolean) = apply { config.debugDbSlow = debugDBSlow }
        fun setSensorObserver(sensorObserver: SensorObserver) = apply { config.sensorObserver = sensorObserver }
        fun setDeviceID(deviceID: String) = apply { config.deviceID = deviceID }

        fun setDataLabel(dataLabel: String) = apply { config.label = dataLabel }
        fun setDebug(debug: Boolean) = apply { config.debug = debug }
        fun setWakeLock(wakeLock: Boolean) = apply { config.wakeLockEnabled = wakeLock }
        fun setBufferSize(bufferSize: Int) = apply { config.bufferSize = bufferSize }
        fun setBufferTimeout(bufferTimeout: Int) = apply { config.bufferTimeout = bufferTimeout }

        fun build(): Accelerometer = Accelerometer(context, config)
    }

    companion object {
        val defaultConfig: AccelerometerConfig = AccelerometerConfig()
    }

    fun start() {
        AccelerometerSensor.CONFIG = config
        val intent = Intent(context, AccelerometerSensor::class.java)
        context.startService(intent)
    }

    fun stop() {
        context.stopService(Intent(context, AccelerometerSensor::class.java))
    }
}