package com.kuroda33.fushikiett

import android.graphics.Color
import android.graphics.Paint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_saccade.*
import java.util.*
import kotlin.math.sin
import android.view.InputDevice
import android.view.KeyEvent

class SaccadeActivity : AppCompatActivity(), SurfaceHolder.Callback {
    var ww:Int=0
    var wh:Int=0
    var timetap:Long=0
    var keytap:Long=0
 //   var bw:Long=0//band width
 //   var bd:Long=0//band distance
//    var ovalw:Long=0
    //   var oval: ImageView?=null
    //   var ovalnum:Int=0
    var whiteF:Boolean=false
    var time0:Long=0
    val endCnt:Long=1000*60*5//5min
    var curCnt:Long=0
 //   var targetMode:Int=0//0:pusuit 1:saccades 2:random
    private var mTimer: Timer? = null
    val paintred: Paint = Paint()
    val paintblack: Paint = Paint()
    private var mHandler = Handler()
    var KeepScreenOn:Boolean=true
    // タッチイベントを処理するためのインタフェース
 //   private var mGestureDetector: GestureDetector? = null
 //   private var SWIPE_DISTANCE = 500
    // 最低スワイプスピード
 //   private val SWIPE_VELOCITY = 100
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saccade)

        paintred.isAntiAlias = false
        paintred.style = Paint.Style.FILL
        paintred.color = Color.RED
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        hideSystemUI()
        mTimer = Timer()
        KeepScreenOn=true
        // タイマーの始動
        var holder = surfacePur.holder
        holder.addCallback(this)
        time0=System.currentTimeMillis()
        timetap=System.currentTimeMillis()
        keytap=System.currentTimeMillis()-300
        Log.d("CANVASstart","start")
        var tempMin:Int=0
        //  Log.d("CANVASstart","start")
        // Log.d("TIME:", t"The system bars are NOT visible")
        drawWhite()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                curCnt = System.currentTimeMillis()-time0
                if(!whiteF)drawWhite()
                if((curCnt.toInt()/1000)!=tempMin)drawCanvas2(tempMin)

                tempMin=curCnt.toInt()/1000
                if(curCnt>endCnt){
                    finish()
                }
            }
        }, 100, 16)

    }

    var lastp:Int=0
    private fun drawWhite() {
        val canvas = surfacePur.holder.lockCanvas()
        if(canvas==null)return
        canvas.drawColor(Color.WHITE)
        surfacePur.holder.unlockCanvasAndPost(canvas)
        whiteF=true
    }
    private fun drawCanvas2(tt:Int) {
        val canvas = surfacePur.holder.lockCanvas()
        if(ww==0||canvas==null)return
        //    val temp=tt.toInt()%1000
        //    val cnt=tt.toInt()/1000
        Log.d("CANVAS:(2)",tt.toString())
        canvas.drawColor(Color.WHITE)

        val rand= Random()
        var raInt=rand.nextInt(10)
        var xi:Float
        var yi:Float
        if(raInt==9)raInt=4//center(4)は*2
        if(raInt==lastp){
            raInt += 1
            if(raInt==9)raInt=0
        }
        lastp=raInt
        if(raInt%3==0)xi=-1f
        else if(raInt%3==1)xi=0f
        else xi=1f
        if(raInt/3==0)yi=-1f
        else if(raInt/3==1)yi=0f
        else yi=1f
        canvas.drawCircle(
            ww.toFloat() / 2f +xi* (ww * 9 / 20).toFloat() ,
            wh.toFloat() / 2f +yi* (wh * 5 / 12).toFloat(),
            (ww / 50).toFloat(),
            paintred
        )
  /*      canvas.drawCircle(
            ww.toFloat()/2f+(ww*9/20).toFloat()* sin(3.1415f*tt.toFloat()/1666),
            wh.toFloat()/2f,
            (ww/50).toFloat(),
            paintred
        )*/
        surfacePur.holder.unlockCanvasAndPost(canvas)
    }
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        //       surfaceWidth = width
        //       surfaceHeight = height
        ww=width
        wh=height
 //       bw=(ww/10).toLong()
  //      bd=(ww/5).toLong()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (mTimer != null){
            mTimer!!.cancel()
            mTimer = null
        }
    }
    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
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
        return super.onTouchEvent(event)
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
 //   override fun onTouchEvent(event: MotionEvent): Boolean {
 //       return mGestureDetector!!.onTouchEvent(event)
 //   }
    // タッチイベントのリスナー
 /*   private val mOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
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

                    //               endCnt = 1000*20*3
                    //                onWindowFocusChanged(true)
                    // 終了位置から開始位置の移動距離が指定値より大きい
                    // Y軸の移動速度が指定値より大きい
                } else if (event1.x - event2.x > SWIPE_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
                    Log.d("onFling","左へ")

                    //endCnt = curCnt + 1000*20*3
                    //              onWindowFocusChanged(false)
                }

            } catch (e: Exception) {
                // TODO
            }
            return false
        }
        override fun onShowPress(e: MotionEvent){
            try {
                Log.d("ttt","www")
                //   leanBackMode()
            }catch(e:Exception) {
                //TODO
            }
        }
    }*/
}
