package com.aware.sensor.accelerometer.db.room

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE

/**
 * Room accelerometer device DAO
 *
 * @author  sercant
 * @date 02/03/2018
 */
@Dao interface AccelerometerDeviceDao {

    @Query("select * from accelerometerDevice")
    fun getAll(): List<DeviceRoomEntity>

    @Query("select * from accelerometerDevice where id = :arg0")
    fun findById(id: Long): DeviceRoomEntity

    @Insert(onConflict = REPLACE)
    fun insert(data: DeviceRoomEntity)

    @Insert(onConflict = REPLACE)
    fun insertAll(data: Array<DeviceRoomEntity>)

    @Update(onConflict = REPLACE)
    fun update(data: DeviceRoomEntity)

    @Delete
    fun delete(data: DeviceRoomEntity)

    @Query("DELETE FROM accelerometerDevice")
    fun deleteAll()
}