package com.racpanapps.yourbeats.gameFiles

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.audiofx.Visualizer
import android.os.Build
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.racpanapps.yourbeats.R
import com.racpanapps.yourbeats.classes.MediaPlayerInstance
import kotlin.math.abs
import kotlin.math.roundToInt


class VisualizeAudio : Visualizer.OnDataCaptureListener {

    companion object {
        var points = 0
    }
    private lateinit var screen : RelativeLayout
    private lateinit var score : TextView
    private lateinit var appContext : Context
    private var width : Int = 0
    private var height : Int = 0
    private var widthMin : Int = 0
    private var widthMax : Int = 0
    private var heightMin : Int = 0
    private var heightMax : Int = 0
    private var buttonCount = 0
    private lateinit var buttonColor : Drawable
    private var level = ""
    private lateinit var ampLevels : ArrayList<Double>
    private var ampLevelsAve = 0
    private var volStart = 0
    private var buttonTimer = false

    fun startVisualizer(audioSession: Int, screen: RelativeLayout, score: TextView, appContext : Context, level : String) {
        this.screen = screen
        this.score = score
        this.appContext = appContext
        this.level = level
        this.ampLevelsAve = 0
        this.ampLevels = arrayListOf()
        points = 0
        getDimensions(appContext)
        getBounds()
        val visualizer = Visualizer(audioSession)
        visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, false)
        visualizer.captureSize = 256
        visualizer.enabled = true
        val maxVolume = MediaPlayerInstance.audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val volume = MediaPlayerInstance.audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        volStart = ((volume.toDouble() / maxVolume.toDouble()) * 100).roundToInt()
    }

    override fun onWaveFormDataCapture(visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int) {
        var amplitude = 0.0
        for (i in 0..waveform!!.size.minus(1) / 2) {
            val y = (waveform[i * 2].toInt() or waveform[i * 2 + 1].toInt() shl 8) / 32768.0
            amplitude += abs(y)
        }
        amplitude = (amplitude / waveform.size.minus(1) / 2 * 100)
        if (ampLevels.size >= 150) {
            ampLevels.clear()
        }
        if (volStart > 20 && ampLevels.size < 150 && amplitude < 24.00) {
            ampLevels.add(amplitude)
        }
        if (ampLevels.size == 50) {
            var total = 0.0
            for (i in 0..49) {
                total+= ampLevels[i]
            }
            ampLevelsAve = (total / 50).roundToInt()
        }
        if (ampLevelsAve > 0) {
            convertAmplitudeToButton(appContext, amplitude.roundToInt(), ampLevelsAve)
        }
    }

    override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {

    }

    private fun getDimensions(appContext: Context) {
        val act : Activity = appContext as Activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = act.windowManager.currentWindowMetrics
            width = windowMetrics.bounds.width()
            height = windowMetrics.bounds.height()
        } else {
            val displayMetrics = DisplayMetrics()
            act.windowManager.defaultDisplay.getMetrics(displayMetrics)
            width = displayMetrics.widthPixels
            height = displayMetrics.heightPixels
        }
    }

    private fun getBounds() {
        widthMin = 30
        widthMax = screen.width - 230
        heightMin = 60
        heightMax = screen.height - 230
    }

    private fun convertAmplitudeToButton(act: Context, amp : Int, ampAve : Int) {
        var totalTime : Long = 1000
        var interval : Long = 200
        if (level == act.getString(R.string.normal)) {
            if (ampAve.minus(3) < amp && ampAve.plus(3) > amp && buttonTimer) {
                createButtons(act)
                totalTime = 1800
                interval = 300
            }
        } else {
            if (ampAve.minus(4) < amp && ampAve.plus(4) > amp && buttonTimer) {
                createButtons(act)
                totalTime = 800
                interval = 200
            }
        }
        countButton(totalTime, interval)
    }

    private fun createButtons(act: Context) {
        getButtonColor(act)
        val btn = Button(act)
        btn.layoutParams = FrameLayout.LayoutParams(200, 200)
        btn.background = buttonColor
        btn.minHeight = 0
        btn.x = (widthMin..widthMax).random().toFloat()
        btn.y = (heightMin..heightMax).random().toFloat()
        screen.addView(btn)
        if (level == act.getString(R.string.normal)) {
            shrinkButton(btn, (btn.x + 100).toInt(), (btn.y + 100).toInt(), 2000, 80)
        } else {
            shrinkButton(btn, (btn.x + 100).toInt(), (btn.y + 100).toInt(), 1000, 50)
        }
    }

    private fun getButtonColor(act : Context) {
        when (buttonCount) {
            0 -> {  buttonColor = act.getDrawable(R.drawable.red_gradient)!!
                buttonCount++ }
            1 -> {  buttonColor = act.getDrawable(R.drawable.yellow_gradient)!!
                buttonCount++ }
            2 -> {  buttonColor = act.getDrawable(R.drawable.dark_orange_gradient)!!
                buttonCount++ }
            3 -> {  buttonColor = act.getDrawable(R.drawable.rose_gradient)!!
                buttonCount = 0 }
        }
    }

    private fun countButton(totalTime : Long, interval : Long) {
        val timer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                buttonTimer = false
            }
            override fun onFinish() {
                buttonTimer = true
            }
        }
        timer.start()
    }

    private fun shrinkButton(btn : Button, centerX : Int, centerY : Int, totalTime : Long, interval : Long) {
        val timer = object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val percent : Double = (millisUntilFinished.toDouble() / totalTime)
                val size = (200 * percent).roundToInt()
                val newCenterX = btn.x + (size / 2)
                val newCenterY = btn.y + (size / 2)
                val params = btn.layoutParams
                params.width = size
                params.height = size
                btn.x+=(centerX - newCenterX)
                btn.y+=(centerY - newCenterY)
                btn.layoutParams = params
                btn.setOnClickListener {
                    setScore((percent * 100).roundToInt())
                    screen.removeView(btn)
                }
            }
            override fun onFinish() {
                screen.removeView(btn)
            }
        }
        timer.start()
    }

    private fun setScore(pts : Int) {
        points+=pts
        score.text = appContext.getString(R.string.score, points)
    }
}