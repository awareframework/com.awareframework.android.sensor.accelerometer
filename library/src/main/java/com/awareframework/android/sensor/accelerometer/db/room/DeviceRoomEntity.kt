package com.awareframework.android.sensor.accelerometer.db.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.awareframework.android.sensor.accelerometer.model.AccelerometerDevice

/**
 * Room accelerometer device entity
 *
 * @author  sercant
 * @date 02/03/2018
 */
@Entity(tableName = "accelerometerDevice")
class DeviceRoomEntity : AccelerometerDevice {

    @PrimaryKey var id: Long? = null

    constructor() : super()

    constructor(id: Long? = null, device: AccelerometerDevice) : super(
        device.deviceId,
        device.timestamp,
        device.maxRange,
        device.minDelay,
        device.name,
        device.power, // in Ma
        device.resolution,
        device.type,
        device.vendor,
        device.version
    ) {
        this.id = id
    }
}