package com.canapini_grasselli.app_sudoku.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.canapini_grasselli.app_sudoku.R
import com.canapini_grasselli.app_sudoku.model.SudokuViewModel

@Composable
fun NetworkConnectivityManager(viewModel: SudokuViewModel) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val isCurrentlyConnected = isNetworkAvailable(connectivityManager)
        viewModel.updateNetworkStatus(isCurrentlyConnected)

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModel.updateNetworkStatus(true)
            }

            override fun onLost(network: Network) {
                viewModel.updateNetworkStatus(false)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}


private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

object StringResources {
    val CHECK_CONN_RES = R.string.check_conn
}