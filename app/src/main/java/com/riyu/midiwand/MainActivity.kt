package com.riyu.midiwand

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.riyu.midiwand.domain.usecase.PitchUseCase
import com.riyu.midiwand.ui.theme.MIDIWandTheme
import kotlin.math.roundToInt

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var pitchUseCase: PitchUseCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pitchUseCase = PitchUseCase(applicationContext)

        setContent {
            MIDIWandTheme {
                MidiWandApp(pitchUseCase = pitchUseCase)
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
            MidiWandApp(pitchUseCase)
        }
    }

}

@Composable
fun MidiWandApp(
    pitchUseCase: PitchUseCase,
    modifier: Modifier = Modifier
) {

    var sliderVal by remember {
        mutableStateOf(0F)
    }

    Column(
    ) {
        Text(
            text = "Hello",
            modifier = modifier
        )
        Slider(
            value = sliderVal,
            onValueChange = { sliderVal = it},
            modifier = modifier.padding(horizontal = 24.dp)
        )

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
        Text(text = "Pitch: ${pitch.roundToInt()} degrees")
    }
}


