package com.awareframework.android.sensor.accelerometer.model

import android.hardware.Sensor
import com.awareframework.android.core.model.AwareObject

/**
 * Holds the information about
 * the accelerometer sensor of the device
 *
 * @author  sercant
 * @date 17/02/2018
 */
open class AccelerometerDevice(
        deviceId: String = "",
        timestamp: Long = 0L,
        var maxRange: Float = 0f,
        var minDelay: Float = 0f,
        var name: String = "",
        var power: Float = 0f, // in Ma
        var resolution: Float = 0f,
        var type: String = "",
        var vendor: String = "",
        var version: String = ""
) : AwareObject(
        timestamp = timestamp,
        deviceId = deviceId
) {

    companion object {
        const val TABLE_NAME = "accelerometerDevice"
    }

    constructor(deviceId: String, timestamp: Long, sensor: Sensor) : this(
            deviceId,
            timestamp,
            sensor.maximumRange,
            sensor.minDelay.toFloat(),
            sensor.name,
            sensor.power,
            sensor.resolution,
            sensor.type.toString(),
            sensor.vendor,
            sensor.version.toString()
    )
}