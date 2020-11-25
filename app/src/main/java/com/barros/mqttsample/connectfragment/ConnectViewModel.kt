package com.barros.mqttsample.connectfragment

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.barros.mqttsample.R
import com.barros.mqttsample.model.ConnectInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference

class ConnectViewModel @ViewModelInject constructor(@ApplicationContext application: Context) : ViewModel() {

    private val applicationContext: WeakReference<Context> = WeakReference<Context>(application)

    private val _showNoConnectionToast = MutableLiveData(false)
    val showNoConnectionToast: LiveData<Boolean> = _showNoConnectionToast

    private val _connectInfo = MutableLiveData<ConnectInfo>()
    val connectInfo: LiveData<ConnectInfo> = _connectInfo

    private val _navigateToClient = MutableLiveData<Boolean>()
    val navigateToClient: LiveData<Boolean> = _navigateToClient

    init {
        checkInternetConnection()
    }

    private fun checkInternetConnection() {
        val connectivityManager = applicationContext.get()!!.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            _showNoConnectionToast.value = when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> false
                else -> true
            }
        }
    }

    fun connect(serverURI: String, clientID: String, username: String, password: String) {
        _connectInfo.value = ConnectInfo(
            serverURI = serverURI,
            clientID = clientID,
            username = username,
            password = password
        )
        _navigateToClient.value = true
    }

    fun preFill() {
        _connectInfo.value = ConnectInfo(
            serverURI = applicationContext.get()!!.getString(R.string.mqtt_server_uri),
            clientID = applicationContext.get()!!.getString(R.string.mqtt_cliend_id),
            username = applicationContext.get()!!.getString(R.string.mqtt_username),
            password = applicationContext.get()!!.getString(R.string.mqtt_password)
        )
    }

    fun clean() {
        _connectInfo.value = ConnectInfo(
            serverURI = "",
            clientID = "",
            username = "",
            password = ""
        )
    }

    fun navigationCompleted() {
        _navigateToClient.value = false
    }
}
