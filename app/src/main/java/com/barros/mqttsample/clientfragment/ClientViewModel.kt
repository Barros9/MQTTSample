package com.barros.mqttsample.clientfragment

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.barros.mqttsample.R
import com.barros.mqttsample.client.MQTTClient
import com.barros.mqttsample.model.ConnectInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

class ClientViewModel @ViewModelInject constructor(
    @ApplicationContext application: Context,
    @Assisted private val handle: SavedStateHandle
) : ViewModel() {

    private val applicationContext: WeakReference<Context> = WeakReference<Context>(application)
    private val connectInfo = handle.get<ConnectInfo>("connectInfo")!!
    private var mqttClient: MQTTClient = MQTTClient(applicationContext.get(), connectInfo.serverURI, connectInfo.clientID)

    private val _showToast = MutableLiveData("")
    val showToast: LiveData<String> = _showToast

    private val _navigateToConnect = MutableLiveData(false)
    val navigateToConnect: LiveData<Boolean> = _navigateToConnect

    private val _pubTopic = MutableLiveData<String>()
    val pubTopic: LiveData<String> = _pubTopic

    private val _subTopic = MutableLiveData<String>()
    val subTopic: LiveData<String> = _subTopic

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    init {
        mqttClient.connect(
            username = connectInfo.username,
            password = connectInfo.password,
            cbConnect = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.connection_success))
                    _showToast.value = applicationContext.get()!!.getString(R.string.connection_success)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.connection_failure, exception))
                    _showToast.value = applicationContext.get()!!.getString(R.string.connection_failure, exception)
                    _navigateToConnect.value = true
                }
            },
            cbClient = object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.connection_lost, cause))
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.receive_message, message, topic))
                    _showToast.value = applicationContext.get()!!.getString(R.string.receive_message, message, topic)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.delivery_complete))
                }
            }
        )
    }

    fun subscribe(topic: String) {
        checkConnection(applicationContext.get()!!.getString(R.string.subscribe)) {
            mqttClient.subscribe(
                topic = topic,
                qos = 1,
                cbSubscribe = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.subscribe_success, topic))
                        _showToast.value = applicationContext.get()!!.getString(R.string.subscribe_success, topic)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.subscribe_failure, topic))
                        _showToast.value = applicationContext.get()!!.getString(R.string.subscribe_failure, topic)
                    }
                })
        }
    }

    fun unsubscribe(topic: String) {
        checkConnection(applicationContext.get()!!.getString(R.string.unsubscribe)) {
            mqttClient.unsubscribe(
                topic = topic,
                cbUnsubscribe = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.unsubscribe_success, topic))
                        _showToast.value = applicationContext.get()!!.getString(R.string.unsubscribe_success, topic)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.unsubscribe_failure, topic))
                        _showToast.value = applicationContext.get()!!.getString(R.string.unsubscribe_failure, topic)
                    }
                })
        }
    }

    fun publish(topic: String, message: String) {
        checkConnection(applicationContext.get()!!.getString(R.string.publish)) {
            mqttClient.publish(
                topic = topic,
                msg = message,
                qos = 1,
                retained = false,
                cbPublish = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.publish_success, message, topic))
                        _showToast.value = applicationContext.get()!!.getString(R.string.publish_success, message, topic)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        _showToast.value = applicationContext.get()!!.getString(R.string.publish_failure)
                        Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.publish_failure))
                    }
                })
        }
    }

    fun disconnect() {
        checkConnection(applicationContext.get()!!.getString(R.string.disconnect)) {
            mqttClient.disconnect(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.disconnection_success))
                    _showToast.value = applicationContext.get()!!.getString(R.string.disconnection_success)
                    _navigateToConnect.value = true
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    _showToast.value = applicationContext.get()!!.getString(R.string.disconnection_failure)
                    Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.disconnection_failure))
                }
            })
        }
    }

    private fun <T> checkConnection(message: String, block: () -> T) {
        if (mqttClient.isConnected()) {
            block()
        } else {
            _showToast.value = applicationContext.get()!!.getString(R.string.no_server_connected, message)
            Log.d(this.javaClass.name, applicationContext.get()!!.getString(R.string.no_server_connected, message))
        }
    }

    fun clean() {
        _pubTopic.value = ""
        _subTopic.value = ""
        _message.value = ""
    }

    fun preFill() {
        _pubTopic.value = applicationContext.get()!!.getString(R.string.mqtt_test_topic)
        _subTopic.value = applicationContext.get()!!.getString(R.string.mqtt_test_topic)
        _message.value = applicationContext.get()!!.getString(R.string.zero)
    }
}
