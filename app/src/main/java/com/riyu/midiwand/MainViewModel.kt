package com.riyu.midiwand

import android.content.Context

class MainViewModel(context: Context) {
    val accelerometerSensor = AccelerometerSensor(context)
    val magnetometerSensor = MagnetometerSensor(context)
    val accelerometerReading = FloatArray(3)
    val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    init {
        accelerometerSensor.startListening()
        magnetometerSensor.startListening()

        accelerometerSensor.setOnSensorValuesChangedListener { values ->
            values.toFloatArray().copyInto(accelerometerReading, 0, 0, 3)
        }
        magnetometerSensor.setOnSensorValuesChangedListener {values ->
            values.toFloatArray().copyInto(magnetometerReading, 0, 0, 3)
        }
    }
}