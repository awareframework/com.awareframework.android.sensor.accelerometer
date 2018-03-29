package com.awareframework.android.sensor.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.awareframework.android.core.db.Engine
import com.awareframework.android.core.db.room.AwareDataEntity
import com.awareframework.android.sensor.accelerometer.model.AccelerometerDevice
import com.awareframework.android.sensor.accelerometer.model.AccelerometerEvent
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Class decription
 *
 * @author  sercant
 * @date 23/03/2018
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private var engine: Engine? = null
    private var encryptedEngine: Engine? = null

    @Before
    @Throws(Exception::class)
    fun init() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        engine = Engine.Builder(appContext)
                .setType(Engine.DatabaseType.NONE)
                .build()

        assertNull(engine)

        engine = Engine.Builder(appContext)
                .setPath("test.db")
                .setType(Engine.DatabaseType.ROOM)
                .build()

        assertNotNull(engine)

        encryptedEngine = Engine.Builder(appContext)
                .setPath("test_encrypted.db")
                .setType(Engine.DatabaseType.ROOM)
                .setEncryptionKey(ENCRYPTION_KEY)
                .build()

        assertNotNull(engine)
    }

    @Test
    @Throws(Exception::class)
    fun testDeleteData() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        val engine = engine!!

        // Create some default events
        val events = ArrayList<AccelerometerEvent>()
        for (i in 0..99) {
            events.add(AccelerometerEvent())
        }

        val data_buffer: Array<AccelerometerEvent> = events.toTypedArray()
        engine.removeAll().join()
        engine.save(data_buffer, AccelerometerEvent.TABLE_NAME).join()

        val data = engine.get(AccelerometerEvent.TABLE_NAME, 10)
        assertEquals(10, data!!.size.toLong())

        engine.remove(data).join()

        val afterRemoveData = engine.get(AccelerometerEvent.TABLE_NAME, 100)
        assertEquals(90, afterRemoveData!!.size.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun testInsertAllEvents() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        // Create some default events
        val events = ArrayList<AccelerometerEvent>()
        for (i in 0..99) {
            events.add(AccelerometerEvent())
        }

        val data_buffer: Array<AccelerometerEvent> = events.toTypedArray()

        engine!!.removeAll().join()
        engine!!.save(data_buffer, AccelerometerEvent.TABLE_NAME).join()
        val data = engine!!.getAll(AccelerometerEvent.TABLE_NAME)
        assertEquals(events.size.toLong(), data!!.size.toLong())

        encryptedEngine!!.removeAll().join()
        encryptedEngine!!.save(data_buffer, AccelerometerEvent.TABLE_NAME).join()
        val data2 = encryptedEngine!!.getAll(AccelerometerEvent.TABLE_NAME)
        assertEquals(events.size.toLong(), data2!!.size.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateDevice() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        val mSensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val device = AccelerometerDevice("", System.currentTimeMillis(), mAccelerometer)

        engine!!.removeAll().join()
        engine!!.save(device, AccelerometerDevice.TABLE_NAME, 0L).join()

        var savedDevice = engine!!.getAll(AccelerometerDevice.TABLE_NAME)
        assertEquals(1, savedDevice!!.size.toLong())

        // TODO (sercant): we should also check if the entries are same here.
        val uuid = UUID.randomUUID().toString()
        device.deviceId = uuid
        engine!!.save(device, AccelerometerDevice.TABLE_NAME, 0L).join()

        savedDevice = engine!!.getAll(AccelerometerDevice.TABLE_NAME)
        assertEquals(1, savedDevice!!.size.toLong())
        assertEquals(device.deviceId, savedDevice[0].deviceId)
    }

    @After
    fun tearDown() {
        engine!!.close()
        encryptedEngine!!.close()
    }

    companion object {

        private val ENCRYPTION_KEY = "custom_key"
    }
}
