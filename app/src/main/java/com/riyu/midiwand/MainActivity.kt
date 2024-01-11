package com.riyu.midiwand

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.riyu.midiwand.domain.usecase.PitchUseCase
import com.riyu.midiwand.ui.theme.MIDIWandTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var pitchUseCase: PitchUseCase
    private lateinit var midiController: MidiController
    private lateinit var handler: Handler
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pitchUseCase = PitchUseCase(applicationContext)
        midiController = MidiController(applicationContext)
        midiController.initialize()

        setContent {
            MIDIWandTheme {
                MidiWandApp(pitchUseCase = pitchUseCase, midiController = midiController)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MIDIWandTheme {
            MidiWandApp(pitchUseCase, midiController)
        }
    }

    @Composable
    fun MidiWandApp(
        pitchUseCase: PitchUseCase,
        midiController: MidiController,
        modifier: Modifier = Modifier
    ) {

        var sliderVal by remember {
            mutableStateOf(0F)
        }

        Column(
        ) {
            PitchDisplay(pitchUseCase)
        }

    }

    @Composable
    fun PitchDisplay(pitchUseCase: PitchUseCase) {
        var pitch by remember { mutableStateOf(0f) }

        LaunchedEffect(pitchUseCase) {
            pitchUseCase.observePitch().collect { newPitch ->
                pitch = newPitch
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val degrees = Math.toDegrees(-pitch.toDouble())
            val displayValue = if (degrees > 0) {
                ((degrees / 90) * 127).toInt()
            } else 0
            Text(text = "Value $displayValue")
        }
    }

    private fun startSendingNoteOn() {
        // Schedule a Runnable to send the MIDI note-on event every 500 milliseconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Send MIDI note-on event
                sendMidiNoteOn()

                // Schedule the next run after the desired interval (e.g., 500 milliseconds)
                handler.postDelayed(this, 500)
            }
        }, 500)
    }

    private fun sendMidiNoteOn() {
        // Implement logic to send MIDI note-on event
        // You should use your MidiController or MIDI API for this purpose
        // Example: midiController.sendNoteOn(channel, note, velocity)
        midiController.sendNoteOn(0, 44, 127)
    }
}




