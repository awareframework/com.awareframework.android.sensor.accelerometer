package com.aware.sensor.accelerometer

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable

/**
 * Main interaction class with the sensor
 *
 * @author  sercant
 * @date 19/02/2018
 */
class Accelerometer private constructor(
        private var context: Context,
        private var config: AccelerometerConfig = defaultConfig
) {

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
            // TODO (sercant): implement observer in the Accelerometer class to
            // support multiple observers
            //    var sensorObserver: AccelerometerSensor.AWARESensorObserver? = null,
            var deviceID: String = "",
            var label: String = "",
            var debug: Boolean = false,
            var wakeLockEnabled: Boolean = false,
            var bufferSize: Int = 250,
            var bufferTimeout: Int = 30000
    ) : Parcelable {

        private constructor(parcel: Parcel) : this() {
            frequency = parcel.readInt()
            threshold = parcel.readFloat()
            enforceFrequency = parcel.readByte() > 0
            debugDbSlow = parcel.readByte() > 0
            deviceID = parcel.readString()
            label = parcel.readString()
            debug = parcel.readByte() > 0
            wakeLockEnabled = parcel.readByte() > 0
            bufferSize = parcel.readInt()
            bufferTimeout = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(frequency)
            parcel.writeFloat(threshold)
            parcel.writeByte(if (enforceFrequency) 1 else 0)
            parcel.writeByte(if (debugDbSlow) 1 else 0)
            parcel.writeString(deviceID)
            parcel.writeString(label)
            parcel.writeByte(if (debug) 1 else 0)
            parcel.writeByte(if (wakeLockEnabled) 1 else 0)
            parcel.writeInt(bufferSize)
            parcel.writeInt(bufferTimeout)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<AccelerometerConfig> {
            override fun createFromParcel(parcel: Parcel): AccelerometerConfig {
                return AccelerometerConfig(parcel)
            }

            override fun newArray(size: Int): Array<AccelerometerConfig?> {
                return arrayOfNulls(size)
            }
        }

    }

    class Builder(private val context: Context) {

        private val config: AccelerometerConfig = AccelerometerConfig()

        fun setFrequency(frequency: Int) = apply { config.frequency = frequency }
        fun setThreshold(threshold: Float) = apply { config.threshold = threshold }
        fun setEnforceFrequency(enforceFrequency: Boolean) = apply { config.enforceFrequency = enforceFrequency }
        fun setDebugDBSlow(debugDBSlow: Boolean) = apply { config.debugDbSlow = debugDBSlow }
        //        fun setSensorObserver(sensorObserver: AccelerometerSensor.AWARESensorObserver) = apply { config.sensorObserver = sensorObserver }
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
        val intent = Intent(context, AccelerometerSensor::class.java)
        intent.putExtra(AccelerometerSensor.CONFIG_EXTRA_KEY, config)
        context.startService(intent)
    }

    fun stop() {
        context.stopService(Intent(context, AccelerometerSensor::class.java))
    }
}