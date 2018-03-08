package com.awareframework.android.sensor.accelerometer.db

import android.content.Context
import com.awareframework.android.core.db.Engine

/**
 * Abstract class for specifying the builder that is going to
 * be used in the accelerometer module of database engine
 *
 * @author  sercant
 * @date 06/03/2018
 */
abstract class DbEngine(context: Context, encryptionKey: String?, dbName: String) : Engine(context, encryptionKey, dbName) {

    class Builder(context: Context) : Engine.Builder(context) {
        override fun build(): Engine? {
            return when (type) {
                DatabaseType.ROOM -> RoomEngine(context, encryptionKey, dbName)
                DatabaseType.NONE -> null
            }
        }
    }
}