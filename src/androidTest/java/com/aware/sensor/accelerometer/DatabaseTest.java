package com.aware.sensor.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.aware.sensor.accelerometer.db.Engine;
import com.aware.sensor.accelerometer.model.AccelerometerDevice;
import com.aware.sensor.accelerometer.model.AccelerometerEvent;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    static final String ENCRYPTION_KEY = "custom_key";

    @Test
    public void initDatabases() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Engine engine = new Engine.Builder(appContext)
                .setDatabaseType(Engine.DatabaseType.NONE)
                .build();

        assertNull(engine);

        engine = new Engine.Builder(appContext)
                .setDatabaseName("test.db")
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .build();

        assertNotNull(engine);

        engine = new Engine.Builder(appContext)
                .setDatabaseName("test_encrypted.db")
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .setEncryptionKey(ENCRYPTION_KEY)
                .build();

        assertNotNull(engine);
    }

    @Test
    public void testInsertAllEvents() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        // Create some default events
        ArrayList<AccelerometerEvent> events = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            events.add(new AccelerometerEvent());
        }

        final AccelerometerEvent[] data_buffer = new AccelerometerEvent[events.size()];
        events.toArray(data_buffer);

        Engine engine = new Engine.Builder(appContext)
                .setDatabaseName("test.db")
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .build();
        engine.clearData().join();
        engine.bulkInsertAsync(data_buffer).join();
        List<AccelerometerEvent> data = engine.getAll(AccelerometerEvent.class);
        assertEquals(events.size(), data.size());
        engine.destroy();

        Engine encryptedEngine = new Engine.Builder(appContext)
                .setDatabaseName("test_encrypted.db")
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .setEncryptionKey(ENCRYPTION_KEY)
                .build();
        encryptedEngine.clearData().join();
        encryptedEngine.bulkInsertAsync(data_buffer).join();
        List<AccelerometerEvent> data2 = encryptedEngine.getAll(AccelerometerEvent.class);
        assertEquals(events.size(), data2.size());
        encryptedEngine.destroy();
    }

    @Test
    public void testUpdateDevice() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();


        SensorManager mSensorManager = (SensorManager) appContext.getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        AccelerometerDevice device = new AccelerometerDevice("", System.currentTimeMillis(), mAccelerometer);

        Engine engine = new Engine.Builder(appContext)
                .setDatabaseName("test.db")
                .setDatabaseType(Engine.DatabaseType.ROOM)
                .build();
        engine.clearData().join();
        engine.saveDeviceAsync(device).join();

        List<AccelerometerDevice> savedDevice = engine.getAll(AccelerometerDevice.class);
        assertEquals(1, savedDevice.size());
        // TODO (sercant): we should also check if the entries are same here.

        String uuid = UUID.randomUUID().toString();
        device.setDeviceId(uuid);
        engine.saveDeviceAsync(device).join();

        savedDevice = engine.getAll(AccelerometerDevice.class);
        assertEquals(1, savedDevice.size());
        assertEquals(device.getDeviceId(), savedDevice.get(0).getDeviceId());
    }
}
