package cu.andyromerodev.biometricapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BiometricViewModelFactory(private val biometricService: BiometricService) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BiometricViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BiometricViewModel(biometricService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
