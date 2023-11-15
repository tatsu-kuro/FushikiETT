package com.kuroda33.fushikiett

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_pusuit.*
import java.util.*
import kotlin.math.sin
import android.view.InputDevice
import android.view.KeyEvent
import android.widget.Toast

class PusuitActivity : AppCompatActivity(), View.OnClickListener, SurfaceHolder.Callback  {
    var ww:Int=0
    var wh:Int=0
    var timetap:Long=0
    var furi:Int=1
    var direction:Int = 1
    var speed:Int = 1
    private var mTimer: Timer? = null
    val paintred: Paint = Paint()
    private var mHandler = Handler()
    var KeepScreenOn:Boolean=true
    var time0:Long=0
    var keytap:Long=0
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
        setContentView(R.layout.activity_pusuit)
        //intent:Intent=getIntent()

        paintred.isAntiAlias = false
        paintred.style = Paint.Style.FILL
        paintred.color = Color.RED
 
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        furi = intent.extras.getInt("pursuit_furi")
        speed = intent.extras.getInt("okn_speed")
        direction = intent.extras.getInt("okn_direction")
        Log.d("CANVAS:",furi.toString())
        //intent.putExtra("pursuit_furi",111)
  //       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        hideSystemUI()
        mTimer = Timer()
        KeepScreenOn=true
        // タイマーの始動
        var holder = surfacePur.holder
        holder.addCallback(this)
        time0=System.currentTimeMillis()
        keytap=System.currentTimeMillis()
        timetap=System.currentTimeMillis()-300
        Log.d("CANVASstart","start")
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                //val temp=System.currentTimeMillis()-time0
                drawCanvas()
                if(KeepScreenOn&&(System.currentTimeMillis()-time0)>1000*60*10){
                    KeepScreenOn=false
                    mHandler.post{
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }
            }
        }, 100, 16)
        setButtons(false)
        furi1.setOnClickListener(this)
        furi2.setOnClickListener(this)
    }
    private fun drawCanvas() {
        val canvas = surfacePur.holder.lockCanvas()
        if(ww==0||canvas==null)return
        canvas.drawColor(Color.WHITE)
        val tt=(3.1415*(System.currentTimeMillis()-time0)*3.0/5000.0).toFloat()
        val wwf=ww/2f
//        val tt=System.currentTimeMillis()-time0
        if(furi==1) {
            //canvas.drawCircle(ww.toFloat()/2f+(ww*9/20).toFloat()* sin(3.1415f*tt.toFloat()/1666),wh.toFloat()/2f,(ww/50).toFloat(),paintred)
            canvas.drawCircle(wwf+wwf*0.9f* sin(tt),wh.toFloat()/2f,wwf/25f,paintred)
        }else{
          //  canvas.drawCircle(ww.toFloat()/2f+(ww*6/20).toFloat()* sin(3.1415f*tt.toFloat()/1666),wh.toFloat()/2f,(ww/50).toFloat(),paintred)
            canvas.drawCircle(wwf+wwf*0.6f* sin(tt),wh.toFloat()/2f,wwf/25f,paintred)
        }
        //     Log.d("CANVAS:",tt.toString())
        surfacePur.holder.unlockCanvasAndPost(canvas)
    }
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        //       surfaceWidth = width
        //       surfaceHeight = height
        ww=width
        wh=height
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
    override fun onClick(v: View) {
        when(v.id){
            R.id.furi1->{
                furi=1
            }
            R.id.furi2 ->{
                furi=2
            }
        }
    }
    private fun setButtons(flag: Boolean){
        if(flag==true){
            furi1.alpha=1.0f
            furi1.isEnabled=true
            furi2.alpha=1.0f
            furi2.isEnabled=true
        }else{
            furi1.alpha=0f
            furi1.isEnabled=false
            furi2.alpha=0f
            furi2.isEnabled=false
        }
    }
    override fun onTouchEvent(event: MotionEvent) :Boolean {

        when(event.getAction()) {

            MotionEvent.ACTION_UP -> {
                if (furi1.alpha == 0f){
                    setButtons(true)
                }else{
                    setButtons(false)
                }
                if((System.currentTimeMillis()-timetap)<300){
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    val intent = Intent()
                    intent.putExtra("okn_speed",speed)
                    intent.putExtra("okn_direction",direction)
                    intent.putExtra("pursuit_furi",furi)
                    setResult(Activity.RESULT_OK,intent)
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
        var leftN=88
        var rightN=87
        if (e.action == KeyEvent.ACTION_DOWN){//||e.action== KeyEvent.ACTION_UP) {
            if (joyName.startsWith("ELECOM BT Remote")) {//ElECOM BT REMOTE
                 if (System.currentTimeMillis() - keytap < 300){
                     getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                     val intent = Intent()
                     intent.putExtra("okn_speed",speed)
                     intent.putExtra("okn_direction",direction)
                     intent.putExtra("pursuit_furi",furi)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
                if(e.keyCode==leftN){
                    furi=1
                }else if(e.keyCode==rightN){
                    furi=2
                }
                keytap=System.currentTimeMillis()
            }else{
                return super.dispatchKeyEvent(e)
            }
            return true
        }
        return super.dispatchKeyEvent(e)
    }
}