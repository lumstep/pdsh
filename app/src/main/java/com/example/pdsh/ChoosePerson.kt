package com.example.pdsh

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator


class ChoosePerson : AppCompatActivity() {
    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var head: ImageView
    private lateinit var body: ImageView
    private lateinit var shirt: ImageView

    val colors = arrayOf(arrayOf(R.color.suit1,R.color.white), arrayOf(R.color.suit2,R.color.black),arrayOf(R.color.suit3,R.color.black),arrayOf(R.color.suit4,R.color.black))

    private var heads = listOf(
        R.drawable.dima, R.drawable.stepa,
        R.drawable.kirill, R.drawable.artem
    )
    private var i = 0
    private var j = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.person)

        init()
    }

    fun onClickNextPerson(v: View) {
        if (j == heads.size - 1) j = 0
        else j++
        Log.d("myLog", "Выбрана голова $j")
        head.setImageResource(heads[j])

    }

    fun onClickPrevPerson(v: View) {
        if (j == 0) j = heads.size -1
        else j--
        Log.d("myLog", "Выбрана голова $j")
        head.setImageResource(heads[j])

    }

    fun onClickNextColor(v: View) {
        if (i == colors.size-1) i = 0
        else i++
        DrawableCompat.setTint(body.drawable, ContextCompat.getColor(this, colors[i][0]))
        DrawableCompat.setTint(shirt.drawable, ContextCompat.getColor(this, colors[i][1]))
    }

    fun onClickPrevColor(v: View) {
        if (i == 0) i = colors.size -1
        else i--
        DrawableCompat.setTint(body.drawable, ContextCompat.getColor(this, colors[i][0]))
        DrawableCompat.setTint(shirt.drawable, ContextCompat.getColor(this, colors[i][1]))
    }

    private fun init() {
        head = findViewById(R.id.person)
        body = findViewById(R.id.suit)
        shirt = findViewById(R.id.shirt)

        mMediaPlayer = MediaPlayer.create(applicationContext, R.raw.chper)
        mMediaPlayer.isLooping = true


    }

    private fun startDance() {
        val k = body.drawable
        val h = shirt.drawable
        if (k is AnimatedVectorDrawable && h is AnimatedVectorDrawable) {
            k.reset()
            h.reset()
            k.start()
            h.start()
            headDance()
            k.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable) {
                    super.onAnimationEnd(drawable)
                    k.start()
                    h.start()
                    headDance()
                }
            })
        }
    }

    fun headDance() {
        head.animate().apply {
            duration = 500
            rotation(4f)
            translationXBy(10f)
            withEndAction {
                duration = 500
                rotation(-14f)
                translationXBy(-105f)
                translationYBy(-10f)
                interpolator = AccelerateInterpolator()
                withEndAction {
                    duration = 2000
                    rotation(20f)
                    translationXBy(230f)
                    translationYBy(35f)
                    interpolator = LinearInterpolator()
                    withEndAction {
                        duration = 1000
                        rotation(-20f)
                        rotationYBy(360f)
                        translationXBy(-258f)
                        translationYBy(-35f)
                        interpolator = FastOutSlowInInterpolator()
                        withEndAction {
                            duration = 1000
                            rotation(0f)
                            translationY(0f)
                            translationX(0f)
                            interpolator = FastOutSlowInInterpolator()
                        }
                    }
                }
            }
        }
    }

    fun onClickStartButton(v: View) {

    }

    override fun onPause() {
        super.onPause()
        mMediaPlayer.stop()
    }

    override fun onResume() {
        super.onResume()
        mMediaPlayer.start()
        startDance()
    }
}