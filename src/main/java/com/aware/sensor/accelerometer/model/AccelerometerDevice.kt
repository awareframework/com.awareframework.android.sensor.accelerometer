package com.aware.sensor.accelerometer.model

import android.hardware.Sensor

/**
 * Holds the information about
 * the accelerometer sensor of the device
 *
 * @author  sercant
 * @date 17/02/2018
 */
open class AccelerometerDevice(
        var deviceId: String,
        var timestamp: Long,
        var maxRange: Float,
        var minDelay: Float,
        var name: String,
        var power: Float, // in Ma
        var resolution: Float,
        var type: String,
        var vendor: String,
        var version: String
) {
    constructor() : this(
            "",
            0,
            0f,
            0f,
            "",
            0f,
            0f,
            "",
            "",
            ""
    )

    constructor(device_id: String, timestamp: Long, sensor: Sensor) : this(
            device_id,
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