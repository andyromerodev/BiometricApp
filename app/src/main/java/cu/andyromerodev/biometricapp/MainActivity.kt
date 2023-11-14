package cu.andyromerodev.biometricapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import cu.andyromerodev.biometricapp.ui.theme.BiometricAppTheme

@RequiresApi(Build.VERSION_CODES.Q)
class MainActivity : FragmentActivity() {

    private val biometricViewModel: BiometricViewModel by viewModels {
        BiometricViewModelFactory(BiometricService(this))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this
        setContent {
            BiometricAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    BiometricToggle(biometricViewModel, activity)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun BiometricToggle(viewModel: BiometricViewModel, activity: FragmentActivity) {
    var isBiometricSwitchChecked by remember { mutableStateOf(false) }
    val isBiometricAvailable by viewModel.isBiometricAvailable.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Switch(
        checked = isBiometricSwitchChecked,
        onCheckedChange = { isChecked ->
            isBiometricSwitchChecked = isChecked
            if (isChecked) {
                viewModel.checkBiometricAvailability() // Verifica la disponibilidad al activar el switch
            }
            if (isChecked && !isBiometricAvailable) {
                showDialog = true
                isBiometricSwitchChecked = false // Restablecer el switch si la biometría no está disponible
            } else if (isChecked && isBiometricAvailable) {
                viewModel.authenticateUser(activity)
            }
        }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Biometric Not Available") },
            text = { Text("Biometric authentication is not set up on this device. Please set it up in your device settings.") },
            confirmButton = { TextButton(onClick = { showDialog = false }) { Text("OK") } }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BiometricAppTheme {
        Greeting("Android")
    }
}