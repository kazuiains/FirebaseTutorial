package id.adiyusuf.remoteconfig

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import id.adiyusuf.remoteconfig.ui.theme.RemoteConfigTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RemoteConfigTheme {
                val state = rememberScrollState()
                var value by remember { mutableStateOf("") }
                var showImage by remember { mutableStateOf(false) }
                var maxLength by remember { mutableIntStateOf(0) }

                val remoteConfig = Firebase.remoteConfig
                val configSettings = remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600
                }
                remoteConfig.setConfigSettingsAsync(
                    configSettings
                )
                remoteConfig.setDefaultsAsync(R.xml.rc_defaults)
                remoteConfig.fetchAndActivate().addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.i(TAG, "Remote Config parameters updated ")
                    } else {
                        Log.i(TAG, "Failed to update Remote Config parameters")
                    }
                }

                LaunchedEffect(key1 = true) {
                    showImage = Firebase.remoteConfig.getBoolean("show_background_image")
                    maxLength =
                        if (Firebase.remoteConfig.getString("greeting_max_length").isNotEmpty())
                            Firebase.remoteConfig.getString("greeting_max_length").toInt() else 100
                }

                Screen(
                    state,
                    value = value,
                    onValueChange = {
                        if (it.length <= maxLength) {
                            value = it
                        }
                    },
                    show = showImage,
                )
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}

@Composable
fun Screen(
    state: ScrollState,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    value: String = "",
    show: Boolean = false,
) {
    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if (show) {
                Image(
                    painterResource(R.drawable.violet),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    value = value,
                    placeholder = {
                        Text(placeholder)
                    },
                    onValueChange = onValueChange,
                    colors = TextFieldDefaults.colors().copy(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.White.copy(0.5f),
                        disabledContainerColor = Color.White.copy(0.5f),
                        unfocusedContainerColor = Color.White.copy(0.5f),
                        errorContainerColor = Color.White.copy(0.5f),
                    ),
                    minLines = 8,
                    maxLines = 8,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    RemoteConfigTheme {
        val state = rememberScrollState()
        var value by remember { mutableStateOf("") }
        Screen(
            state,
            value = value,
            onValueChange = {
                value = it
            },
        )
    }
}