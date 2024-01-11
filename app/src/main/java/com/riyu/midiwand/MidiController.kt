package com.riyu.midiwand

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.media.midi.MidiOutputPort
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MidiController(private val context: Context) {

    private val midiManager: MidiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager
    private lateinit var midiDevice: MidiDevice
    private lateinit var inputPort: MidiInputPort
    private lateinit var outputPort: MidiOutputPort

    fun initialize() {
        // Perform MIDI initialization, such as finding and connecting to a MIDI device
        val midiDeviceInfo = findYourMidiDevice()
        if (midiDeviceInfo != null) {
            connectToMidiDevice(midiDeviceInfo)
        }
    }

    fun sendPitchAsMidiController(pitchValue: Int) {
        // MIDI controller message format: B0 nn vv
        // Send MIDI pitch value using the outputPort
        // ...
    }

    private fun findYourMidiDevice(): MidiDeviceInfo? {
        val deviceInfoList = midiManager.devices

        // Iterate through the devices and find the one that matches your criteria
        for (deviceInfo in deviceInfoList) {
            val deviceName = deviceInfo.properties.getString(MidiDeviceInfo.PROPERTY_NAME)

            // Example: Check if the device name contains a specific keyword
            if (deviceName != null && deviceName.contains("Android USB Peripheral Port")) {
                println("Found USB Peripheral Port")
                println(deviceInfo.outputPortCount)
                return deviceInfo
            }
        }
        println("Did not find USB Peripheral Port")
        // Return null if no matching device is found
        return null
    }

    private fun connectToMidiDevice(deviceInfo: MidiDeviceInfo) {
        midiManager.openDevice(deviceInfo, {
            if (it != null) {
                midiDevice = it
                inputPort = it.openInputPort(0) // Open the first output port (adjust as needed)
                outputPort = it.openOutputPort(0)
            }

            sendNoteOn(0, 60, 90)
        }, Handler(Looper.getMainLooper()))
    }

    fun sendNoteOn(channel: Int, note: Int, velocity: Int) {
        // Check if the output port is initialized
        if (::inputPort.isInitialized) {
            // MIDI note-on event format: 0x90 + channel, note, velocity
            val statusByte = 0x90 or (channel and 0x0F) // MIDI note-on status byte
            val midiMessage = byteArrayOf(statusByte.toByte(), note.toByte(), velocity.toByte())
            inputPort.send(midiMessage, 0, midiMessage.size)
        }
    }
}