package com.oliverbotello.oledtext

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.huawei.agconnect.remoteconfig.AGConnectConfig
import kotlin.math.roundToInt

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

val Int.sp: Int
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics).roundToInt();

class MainActivity : AppCompatActivity() {
    private val config = AGConnectConfig.getInstance()
    private lateinit var txtvwMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        initView()
        getRemoteConfig()
    }

    private fun initView() {
        txtvwMessage = findViewById(R.id.txtvw_led)

        setTextConfig(DEFAULT_TEXT, TEXT_COLOR)
        setAnimationConfig(DEFAULT_DURATION)
    }

    private fun setTextConfig(message: String, color: String) {
        txtvwMessage.text = message.uppercase()
        txtvwMessage.layoutParams.width = (message.length * 256.sp * .7).toInt()

        txtvwMessage.setTextColor(Color.parseColor(color))
    }

    private fun setAnimationConfig(duration: Long) {
        val animation: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            -1.0f,
            Animation.RELATIVE_TO_PARENT,
            0f,
            Animation.RELATIVE_TO_PARENT,
            0f
        )
        animation.duration = 1000L * duration
        animation.fillAfter = true
        animation.repeatCount = Animation.INFINITE

        txtvwMessage.startAnimation(animation)
    }

    private fun getDefaultConfig() = mutableMapOf<String, Any>(
        "message" to DEFAULT_TEXT,
        "color" to TEXT_COLOR,
        "duration" to DEFAULT_DURATION
    )

    private fun getRemoteConfig() {
        config.fetch().addOnSuccessListener {
            config.apply(it)
        }.addOnFailureListener {
            config.applyDefault(getDefaultConfig())
        }.addOnCompleteListener {
            setTextConfig(config.getValueAsString("message"), config.getValueAsString("color"))
            setAnimationConfig(config.getValueAsLong("duration"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        config.clearAll()
    }
}