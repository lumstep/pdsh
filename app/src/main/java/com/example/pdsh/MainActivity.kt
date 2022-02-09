package com.example.pdsh

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var sword: ImageView
    private lateinit var tip: ImageView
    private lateinit var bubbles: MutableMap<ImageView, Int>
    private lateinit var set: MutableSet<Int>
    private lateinit var swordAnim: Animation
    private lateinit var tipAnim: Animation
    private var status = true

    private var headsId = setOf(
        R.id.h1,
        R.id.h2,
        R.id.h3,
        R.id.h4,
        R.id.h5,
        R.id.h6,
        R.id.h7,
        R.id.h8,
        R.id.h9,
        R.id.h10,
        R.id.h11,
        R.id.h12
    )
    private var headsImage = setOf(
        R.drawable.dima, R.drawable.stepa,
        R.drawable.kirill, R.drawable.artem
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun bubbles(bubble: ImageView) {
        CoroutineScope(Dispatchers.Main).launch { bubble.visibility = View.VISIBLE }
        bubble.animate().apply {
            duration = 6000
            scaleXBy(1.5f)
            scaleYBy(1.5f)
            translationYBy(-500f)
            withEndAction {
                bubble.animate().apply {
                    duration = 150
                    scaleXBy(2f)
                    scaleYBy(2f)
                    withEndAction {
                        duration = 0
                        scaleX(1f)
                        scaleY(1f)
                        translationY(0f)
                        translationX(0f)
                        set.add(bubbles.getValue(bubble))
                        CoroutineScope(Dispatchers.Main).launch {
                            bubble.visibility = View.GONE
                        }
                        Log.d("myLog", "Исчез пузырь ${bubbles[bubble]}")
                    }
                }
            }
        }
    }

    fun bubbleBump(m: View) {
        GlobalScope.launch {
            val head = chooseHead()
            m.animate().apply {
                duration = 200
                scaleXBy(2f)
                scaleYBy(2f)
                withEndAction {
                    scaleX(1f)
                    scaleY(1f)
                    translationY(0f)
                    translationX(0f)
                    CoroutineScope(Dispatchers.Main).launch { m.visibility = View.GONE }
                    Log.d("myLog", "Вы лопнули пузырь")
                    CoroutineScope(Dispatchers.Main).launch { head.visibility = View.VISIBLE }
                    val x = m.x - head.height / 3.5
                    val y = m.y - head.height / 3.5
                    Log.d("myLog", "Координаты X: $x , Y: $y")
                    head.x = x.toFloat()
                    head.y = y.toFloat()
                    addToSet(m)
                    Log.d("myLog", "Крутится голова")
                    head.animate().apply {
                        duration = 8000
                        rotation(1440f)
                        scaleX(0.5f)
                        scaleY(0.5f)
                        alpha(0f)
                        withEndAction {
                            duration = 0
                            alpha(1f)
                            scaleY(1f)
                            scaleX(1f)
                            rotation(0f)
                            CoroutineScope(Dispatchers.Main).launch {
                                head.visibility = View.GONE
                            }
                            Log.d("myLog", "Голова $head исчезла")
                        }
                    }
                }
            }
        }
    }

    @Synchronized
    private fun addToSet(m: View) {
        val l = bubbles.getValue(findViewById(m.id))
        set.add(l)
        Log.d("myLog", "В set добавлен $l")
    }

    private fun getKeyBubble(): ImageView {
        val j = set.random()
        Log.d("myLog", "Число = $j выбрано")
        set.remove(j)
        Log.d("myLog", "Оставшиеся числа: $set")
        return when (j) {
            1 -> findViewById(R.id.imageView)
            2 -> findViewById(R.id.imageView2)
            3 -> findViewById(R.id.imageView3)
            4 -> findViewById(R.id.imageView4)
            5 -> findViewById(R.id.imageView5)
            6 -> findViewById(R.id.imageView6)
            else -> {
                Log.d("myLog", "Something is going wrong...")
                getKeyBubble()
            }

        }
    }

    private fun chooseHead(): ImageView {
        var head: ImageView
        headsId.forEach { i ->
            if (findViewById<ImageView>(i).visibility == View.GONE) {
                Log.d("myLog", "Free Id i $i")
                head = findViewById(i)
                head.setImageResource(headsImage.random())
                return head
            }
        }
        Log.d("myLog", "Голова не выбрана ...")
        return chooseHead()
    }

    fun onClickChPerButton(v: View) {
        val intent = Intent(this, ChoosePerson::class.java)
        startActivity(intent)
    }

    private fun init() {
        sword = findViewById(R.id.swordIm)
        tip = findViewById(R.id.tipIm)

        mMediaPlayer = MediaPlayer.create(applicationContext, R.raw.fon)
        mMediaPlayer.isLooping = true

        swordAnim = AnimationUtils.loadAnimation(this, R.anim.sword_anim)
        tipAnim = AnimationUtils.loadAnimation(this, R.anim.tip_anim)
    }

    override fun onPause() {
        super.onPause()
        mMediaPlayer.stop()
        swordAnim.cancel()
        tipAnim.cancel()
        status = false
    }

    override fun onResume() {
        super.onResume()
        mMediaPlayer.start()
        sword.startAnimation(swordAnim)
        tip.startAnimation(tipAnim)

        status = true
        set = mutableSetOf(1, 2, 3, 4, 5, 6)

        GlobalScope.launch {
            bubbles = mutableMapOf()
            bubbles[findViewById(R.id.imageView)] = 1
            bubbles[findViewById(R.id.imageView2)] = 2
            bubbles[findViewById(R.id.imageView3)] = 3
            bubbles[findViewById(R.id.imageView4)] = 4
            bubbles[findViewById(R.id.imageView5)] = 5
            bubbles[findViewById(R.id.imageView6)] = 6

            while (status) {
                delay(2000)
                Log.d("myLog", "Новый поиск")
                val bubble = getKeyBubble()
                Log.d("myLog", "Ключ выбран ${bubbles[bubble]}")
                bubbles(bubble)
            }
        }

    }
}
