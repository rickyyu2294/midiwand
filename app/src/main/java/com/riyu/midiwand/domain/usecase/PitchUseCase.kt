package com.riyu.midiwand.domain.usecase

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PitchUseCase(private val context: Context) {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val rotationMatrix = FloatArray(9)
    private val accelerometerValues = FloatArray(3)
    private val magnetometerValues = FloatArray(3)
    private val orientationAngles = FloatArray(3)

    fun observePitch(): Flow<Float> = callbackFlow {
        val sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> System.arraycopy(
                        event.values, 0,
                        accelerometerValues, 0, 3
                    )
                    Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(
                        event.values, 0,
                        magnetometerValues, 0, 3
                    )
                }

                SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerValues,
                    magnetometerValues
                )

                SensorManager.getOrientation(rotationMatrix, orientationAngles)

                val pitch = orientationAngles[1]
                trySend(pitch)
            }
        }

        if (accelerometer != null && magnetometer != null) {
            sensorManager.registerListener(
                sensorEventListener,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
            sensorManager.registerListener(
                sensorEventListener,
                magnetometer,
                SensorManager.SENSOR_DELAY_UI
            )

            awaitClose {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }
    }
}
