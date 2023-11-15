package com.kuroda33.fushikiett

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_carolic.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_okn.*
import kotlinx.android.synthetic.main.activity_pusuit.*
import java.util.*
import kotlin.math.sin

class CarolicOKNActivity : AppCompatActivity(), SurfaceHolder.Callback {
    var ww:Int=0
    var wh:Int=0
    var bw:Long=0//band width
    var bd:Long=0//band distance
    var timetap:Long=0
    var time0:Long=0
    var keytap:Long=0
    var lenArray = arrayListOf<Int>()//length time
    var width:Int=0
    var height:Int=0
    private var mTimer: Timer? = null
    private var sTimer: Timer? = null
    private var mHandler = Handler()
    private var cnt:Int = 0
    private var SWIPE_DISTANCE = 500
    // 最低スワイプスピード
    private val SWIPE_VELOCITY = 100

    // タッチイベントを処理するためのインタフェース
    private var mGestureDetector: GestureDetector? = null
    val paintred: Paint = Paint()
    val paintblack: Paint = Paint()
    val paintwhite: Paint = Paint()
    var backcolor:Int=0//0:black 1:white
    var circlecolor:Int=0//0:black 1:white 2:red
    var movef:Float=0f
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        hideSystemUI()
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )

        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                Log.d("debug", "The system bars are visible")
            } else {
                Log.d("debug", "The system bars are NOT visible")
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carolic)
        mGestureDetector = GestureDetector(this, mOnGestureListener) // => 忘れない
        hideSystemUI()
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        SWIPE_DISTANCE=displayMetrics.widthPixels/4
        Log.d("width:",width.toString())
        paintwhite.isAntiAlias = false
        paintwhite.style = Paint.Style.FILL
        paintwhite.color = Color.BLACK
        paintblack.isAntiAlias = false
        paintblack.style = Paint.Style.FILL
        paintblack.color = Color.BLACK

        paintred.isAntiAlias = false
        paintred.style = Paint.Style.FILL
        paintred.color = Color.RED

        Time.alpha=0.1f
        lenArray.clear()
        lenArray.add(0)
        lenArray.add(10)
        lenArray.add(100)//lenArray.last() + 90)
        lenArray.add(110)//lenArray.last() + 10)
        lenArray.add(115)//lenArray.last() + 5)
        lenArray.add(130)//lenArray.last() + 15)
        lenArray.add(140)//lenArray.last() + 10)
        mTimer = Timer()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // タイマーの始動
        backcolor=0
        circlecolor=0
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler.post {
                    Time.text = String.format("%d", cnt)
                }
                if(cnt==lenArray[1]) {
                    backcolor=0
                    circlecolor=2
                    movef=0f

                }else if(cnt==lenArray[1]+1) {
                    backcolor=0
                    circlecolor=0

                }else if(cnt==lenArray[2]) {
                    backcolor=1
                    circlecolor=0
                    movef=0f

                }else if(cnt==lenArray[3]){
                    backcolor=0
                    circlecolor=0

                }else if(cnt==lenArray[4]) {
                    backcolor=1
                    circlecolor=0
                    movef=1f

                }else if(cnt==lenArray[5]){
                    backcolor=0
                    circlecolor=0

                }else if(cnt==lenArray[6]){
                    finish()//遷移するとscreen_keep_onフラグはリセットされる
                }
                cnt += 1
            }
        }, 100, 1000)
        sTimer = Timer()
        // タイマーの始動
        var holder = surfaceCar.holder
        holder.addCallback(this)
        time0=System.currentTimeMillis()
        keytap=System.currentTimeMillis()-300
        timetap=System.currentTimeMillis()
        Log.d("CANVASstart","start")
        sTimer!!.schedule(object : TimerTask() {
            override fun run() {
                drawCanvas()
            }
        }, 100, 16)
    }

    private fun drawCanvas() {
        val canvas = surfaceCar.holder.lockCanvas()
        if(ww==0||canvas==null)return
        if(backcolor==0)canvas.drawColor(Color.BLACK)
        else canvas.drawColor(Color.WHITE)
        val tt=System.currentTimeMillis()-time0
        var torg: Long
        torg = tt / 3
        torg=torg*ww/1024//amazon KFAUWI(width:1024)を基準

        if(movef==1f) {
            for (i in 0..4) {
                var xs = (i * bd + torg) % ww
                canvas.drawRect(xs.toFloat(), 0f, xs.toFloat() + bw.toFloat(), wh.toFloat(), paintblack)
                if (xs + bw > ww) {
                    xs = xs + bw - ww
                    canvas.drawRect(0f, 0f, xs.toFloat(), wh.toFloat(), paintblack)
                }
            }
        }
        else {
            if (circlecolor == 0)
                canvas.drawCircle(ww.toFloat() / 2f, wh.toFloat() / 2f, (ww / 50).toFloat(), paintblack)
            else if (circlecolor == 1)
                canvas.drawCircle(ww.toFloat() / 2f, wh.toFloat() / 2f, (ww / 50).toFloat(), paintwhite)
            else
                canvas.drawCircle(ww.toFloat() / 2f, wh.toFloat() / 2f, (ww / 50).toFloat(), paintred)
        }
        surfaceCar.holder.unlockCanvasAndPost(canvas)
    }
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        ww=width
        wh=height
        bw=(ww/10).toLong()
        bd=(ww/5).toLong()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (mTimer != null){
            mTimer!!.cancel()
            mTimer = null
        }
        if(sTimer!=null){
            sTimer!!.cancel()
            sTimer=null
        }
    }
    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
    }
    fun nextScene(){
        //      remain=0
        //      mediaPlay.release()
        for(i in 0..5){
            if(cnt>lenArray[i]&&cnt<lenArray[i+1]){
                cnt=lenArray[i+1]
            }
        }
    }
    fun backScene(){
        for(i in 0..5){
            if(cnt>lenArray[i] && cnt<lenArray[i+1]+5){
                cnt=lenArray[i]
            }else if(cnt>lenArray[6]) {
                cnt=lenArray[6]
            }
        }

    }
    override fun onTouchEvent(event: MotionEvent) :Boolean {

        when(event.getAction()) {

            MotionEvent.ACTION_UP -> {

                if((System.currentTimeMillis()-timetap)<300){
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    finish()
                }
                timetap=System.currentTimeMillis()
            }
        }
//        return super.onTouchEvent(event)
        return mGestureDetector!!.onTouchEvent(event)
    }
    // タッチイベントのリスナー
    private val mOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        // フリックイベント
        override fun onFling(event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            try {
                // 移動距離・スピードを出力
                //              val distance_y = Math.abs(event1.y - event2.y)
                //              val velocity_y = Math.abs(velocityY)
                //          val distance_x = Math.abs(event1.x - event2.x)
                //          val velocity_x = Math.abs(velocityX)

                if (event2.x - event1.x > SWIPE_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
                    Log.d("onFling","右へ")
                    backScene()
//                    hideSystemUI()
                    onWindowFocusChanged(true)
                    // 終了位置から開始位置の移動距離が指定値より大きい
                    // Y軸の移動速度が指定値より大きい
                } else if (event1.x - event2.x > SWIPE_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
                    Log.d("onFling","左へ")
                    nextScene()
                    //                    hideSystemUI()
                    onWindowFocusChanged(false)
                }

            } catch (e: Exception) {
                // TODO
            }
            return false
        }
    }
    override fun dispatchKeyEvent(e: KeyEvent): Boolean {
        var joy: InputDevice = e.device
        val joyName = joy.name
        // DOWNとUPが取得できるのでログの2重表示防止のためifer
        Log.d("KeyCode", "InputDevice:" + joyName)// toString())
        Log.d("KeyCode", "KeyCode:" + e.getKeyCode())

        if (e.action == KeyEvent.ACTION_DOWN){//||e.action== KeyEvent.ACTION_UP) {
            if (joyName.startsWith("ELECOM BT Remote")) {//ElECOM BT REMOTE
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                if (System.currentTimeMillis() - keytap < 300)finish()
                keytap=System.currentTimeMillis()
            }else{
                return super.dispatchKeyEvent(e)
            }
            return true
        }
        return super.dispatchKeyEvent(e)
    }
}
