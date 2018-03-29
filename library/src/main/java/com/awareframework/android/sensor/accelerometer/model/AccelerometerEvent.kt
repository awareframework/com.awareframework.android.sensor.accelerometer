package com.awareframework.android.sensor.accelerometer.model

import com.awareframework.android.core.model.AwareObject

/**
 * Holds the data of the sensor events
 *
 * @author  sercant
 * @date 17/02/2018
 */
open class AccelerometerEvent(
        var x: Float = 0f,
        var y: Float = 0f,
        var z: Float = 0f,
        var eventTimestamp: Long = 0L,
        var accuracy: Int = 0,
        jsonVersion : Int = 1
) : AwareObject(jsonVersion = jsonVersion) {

    companion object {
        const val TABLE_NAME = "accelerometerEvent"
    }

}