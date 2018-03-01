package com.aware.sensor.accelerometer.db.room

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE

/**
 * Room accelerometer DAO
 *
 * @author  sercant
 * @date 22/02/2018
 */
@Dao interface AccelerometerDataDao {

    @Query("select * from accelerometerData")
    fun getAll(): List<AccelerometerData>

    @Query("select * from accelerometerData where id = :arg0")
    fun findById(id: Long): AccelerometerData

    @Insert(onConflict = REPLACE)
    fun insert(data: AccelerometerData)

    @Insert(onConflict = REPLACE)
    fun insertAll(data: Array<AccelerometerData>)

    @Update(onConflict = REPLACE)
    fun update(data: AccelerometerData)

    @Delete
    fun delete(data: AccelerometerData)

    @Query("DELETE FROM accelerometerData")
    fun deleteAll()
}