package com.barros.mqttsample.client

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import timber.log.Timber

class MQTTClient(
    context: Context?,
    serverURI: String,
    clientID: String = ""
) {
    private var mqttClient = MqttAndroidClient(context, serverURI, clientID)

    private val defaultCbConnect = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Timber.d("(Default) Connection success")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Timber.d("Connection failure: $exception")
        }
    }

    private val defaultCbClient = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            Timber.d("Receive message: $message from topic: $topic")
        }

        override fun connectionLost(cause: Throwable?) {
            Timber.d("Connection lost $cause")
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            Timber.d("Delivery completed")
        }
    }

    private val defaultCbSubscribe = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Timber.d("Subscribed to topic")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Timber.d("Failed to subscribe topic")
        }
    }

    private val defaultCbUnsubscribe = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Timber.d("Unsubscribed to topic")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Timber.d("Failed to unsubscribe topic")
        }
    }

    private val defaultCbPublish = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Timber.d("Message published to topic")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Timber.d("Failed to publish message to topic")
        }
    }

    private val defaultCbDisconnect = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Timber.d("Disconnected")
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Timber.d("Failed to disconnect")
        }
    }

    fun connect(
        username: String = "",
        password: String = "",
        cbConnect: IMqttActionListener = defaultCbConnect,
        cbClient: MqttCallback = defaultCbClient
    ) {
        mqttClient.setCallback(cbClient)
        val options = MqttConnectOptions()
        options.userName = username
        options.password = password.toCharArray()

        try {
            mqttClient.connect(options, null, cbConnect)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun isConnected(): Boolean {
        return mqttClient.isConnected
    }

    fun subscribe(
        topic: String,
        qos: Int = 1,
        cbSubscribe: IMqttActionListener = defaultCbSubscribe
    ) {
        try {
            mqttClient.subscribe(topic, qos, null, cbSubscribe)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(
        topic: String,
        cbUnsubscribe: IMqttActionListener = defaultCbUnsubscribe
    ) {
        try {
            mqttClient.unsubscribe(topic, null, cbUnsubscribe)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(
        topic: String,
        msg: String,
        qos: Int = 1,
        retained: Boolean = false,
        cbPublish: IMqttActionListener = defaultCbPublish
    ) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, cbPublish)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun disconnect(cbDisconnect: IMqttActionListener = defaultCbDisconnect) {
        try {
            mqttClient.disconnect(null, cbDisconnect)
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}
