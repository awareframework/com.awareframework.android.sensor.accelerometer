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
open class AccelerometerDevice : AwareObject {

    var maxRange: Float = 0f
    var minDelay: Float = 0f
    var name: String = ""
    var power: Float = 0f // in Ma
    var resolution: Float = 0f
    var type: String = ""
    var vendor: String = ""
    var version: String = ""

    constructor() : super()
    constructor(
            deviceId: String,
            timestamp: Long,
            maxRange: Float,
            minDelay: Float,
            name: String,
            power: Float, // in Ma
            resolution: Float,
            type: String,
            vendor: String,
            version: String) : super() {
        this.deviceId = deviceId
        this.timestamp = timestamp
        this.maxRange = maxRange
        this.minDelay = minDelay
        this.name = name
        this.power = power
        this.resolution = resolution
        this.type = type
        this.vendor = vendor
        this.version = version
    }

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