package com.riyu.midiwand.domain.usecase

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.asin

class PitchUseCase(private val context: Context) {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val rotationMatrix = FloatArray(9)
    private val orientationValues = FloatArray(3)

    fun observePitch(): Flow<Float> = callbackFlow {
        val sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not needed for this example
            }

            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> System.arraycopy(
                        event.values, 0,
                        orientationValues, 0, orientationValues.size
                    )
                    Sensor.TYPE_MAGNETIC_FIELD -> System.arraycopy(
                        event.values, 0,
                        orientationValues, 3, orientationValues.size - 3
                    )
                }

                SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    orientationValues,
                    orientationValues.copyOfRange(3, 6)
                )

                val pitch = calculatePitch()
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

    private fun calculatePitch(): Float {
        // Calculate pitch using rotationMatrix and orientationValues
        // You can customize this calculation based on your requirements.
        // For example, you might want to use Math.atan2 or other trigonometric functions.
        // Ensure to return the pitch value in degrees.
        // Example:
        return Math.toDegrees(asin(rotationMatrix[1].toDouble())).toFloat()
    }
}
