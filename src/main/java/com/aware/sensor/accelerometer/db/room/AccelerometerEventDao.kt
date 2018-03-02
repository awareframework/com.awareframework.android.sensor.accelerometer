package com.aware.sensor.accelerometer.db.room

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE

/**
 * Room accelerometer DAO
 *
 * @author  sercant
 * @date 22/02/2018
 */
@Dao interface AccelerometerEventDao {

    @Query("select * from accelerometerEvent")
    fun getAll(): List<EventRoomEntity>

    @Query("select * from accelerometerEvent where id = :arg0")
    fun findById(id: Long): EventRoomEntity

    @Insert(onConflict = REPLACE)
    fun insert(data: EventRoomEntity)

    @Insert(onConflict = REPLACE)
    fun insertAll(data: Array<EventRoomEntity>)

    @Update(onConflict = REPLACE)
    fun update(data: EventRoomEntity)

    @Delete
    fun delete(data: EventRoomEntity)

    @Query("DELETE FROM accelerometerEvent")
    fun deleteAll()
}