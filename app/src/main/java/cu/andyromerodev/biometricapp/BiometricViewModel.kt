package cu.andyromerodev.biometricapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
class BiometricViewModel(private val biometricService: BiometricService) : ViewModel() {

    private val _isBiometricAvailable = MutableStateFlow(false)
    val isBiometricAvailable: StateFlow<Boolean> = _isBiometricAvailable

    private val _authenticationResult = MutableStateFlow<BiometricAuthenticationResult>(BiometricAuthenticationResult.Initial)
    val authenticationResult: StateFlow<BiometricAuthenticationResult> = _authenticationResult


    init {
        checkBiometricAvailability()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkBiometricAvailability() {
        viewModelScope.launch {
            _isBiometricAvailable.value = biometricService.isBiometricAvailable()
        }
    }


    fun authenticateUser(activity: FragmentActivity) {
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                _authenticationResult.value = BiometricAuthenticationResult.Success
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                _authenticationResult.value = BiometricAuthenticationResult.Error(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                _authenticationResult.value = BiometricAuthenticationResult.Failed
            }
        }

        biometricService.authenticateUser(activity, callback)
    }
}

sealed class BiometricAuthenticationResult {
    object Initial : BiometricAuthenticationResult()
    object Success : BiometricAuthenticationResult()
    object Failed : BiometricAuthenticationResult()
    data class Error(val message: String) : BiometricAuthenticationResult()
}