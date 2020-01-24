package com.example.tanimals

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.api.models.consumer.pubsub.objects.PNMembershipResult
import com.pubnub.api.models.consumer.pubsub.objects.PNSpaceResult
import com.pubnub.api.models.consumer.pubsub.objects.PNUserResult
import java.util.*


class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val publishText = findViewById<EditText>(R.id.editTextPublish)
        val subscribeText = findViewById<TextView>(R.id.textViewSubscribe)


        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = "sub-c-34852842-3213-11ea-a820-f6a3bb2caa12"
        pnConfiguration.publishKey = "pub-c-b2cb93df-4314-4ac7-a7bd-4a3e8a58152e"
        pnConfiguration.logVerbosity = PNLogVerbosity.BODY
        val pubNub = PubNub(pnConfiguration)

        val subscribeCallback: SubscribeCallback = object : SubscribeCallback()  {
            override fun signal(pubnub: PubNub, pnSignalResult: PNSignalResult) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun status(pubnub: PubNub, status: PNStatus) {

            }

            override fun user(pubnub: PubNub, pnUserResult: PNUserResult) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun messageAction(
                pubnub: PubNub,
                pnMessageActionResult: PNMessageActionResult
            ) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun message(pubnub: PubNub, message: PNMessageResult) {
                runOnUiThread {
                    subscribeText.text = message.message.toString()
                }
            }

            override fun space(pubnub: PubNub, pnSpaceResult: PNSpaceResult) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {
            }

            override fun membership(pubnub: PubNub, pnMembershipResult: PNMembershipResult) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        pubNub.run {
            addListener(subscribeCallback)
            subscribe()
                .channels(listOf("suchChannel")) // subscribe to channels
                .withPresence()
                .execute()
        }

        publishText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                pubNub.publish()
                    .message(listOf(publishText.text.toString()))
                    .channel("suchChannel")
                    .async(object : PNCallback<PNPublishResult?>() {
                        override fun onResponse(
                            result: PNPublishResult?,
                            status: PNStatus
                        ) {
                            println(status)
                        }
                    })
                return@OnKeyListener true
            }
            false
        })
    }

    private val PICK_DATE_REQUEST = 1 // The request code.
    // ...
    fun pickContact(v: View) {

        val pickDateIntent = Intent(this,weatherPicker::class.java )

        startActivityForResult(pickDateIntent, PICK_DATE_REQUEST)
    }

}
