package com.riyu.midiwand

import android.app.Application

object SensorModule {

    fun provideAccelerometerSensor(app: Application): MeasurableSensor {
        return AccelerometerSensor(app)
    }

    fun provideMagnetometerSensor(app: Application): MeasurableSensor {
        return MagnetometerSensor(app)
    }
}