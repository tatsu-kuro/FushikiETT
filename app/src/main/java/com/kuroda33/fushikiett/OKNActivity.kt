package com.kuroda33.fushikiett

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_okn.*
import android.graphics.*
import android.os.Handler
import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.activity_pusuit.*
import java.util.*
import kotlin.math.sin
class OKNActivity : AppCompatActivity(), View.OnClickListener, SurfaceHolder.Callback {
    //   private var surfaceWidth: Int = 0   // サーフェスビューの幅
    //   private var surfaceHeight: Int = 0  // サーフェスビューの高さ
    private var mHandler = Handler()
    var ww:Int=0//window width
    var wh:Int=0//window height
    var bw:Long=0//band width
    var bd:Long=0//band distance
    var timetap:Long=0
    var furi:Int=1
    var speed:Int=2
    var direction:Int=2
    private var mTimer: Timer? = null
    val paintblack: Paint = Paint()
    val paintwhite: Paint = Paint()
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
        setContentView(R.layout.activity_okn)
        keytap = System.currentTimeMillis()
        paintblack.isAntiAlias = false
        paintblack.style = Paint.Style.FILL
        paintblack.color = Color.BLACK
        paintwhite.isAntiAlias = false
        paintwhite.style = Paint.Style.FILL
        paintwhite.color = Color.WHITE

        Buttonleft.setOnClickListener(this)
        Buttonright.setOnClickListener(this)
        Buttonsp1.setOnClickListener(this)
        Buttonsp2.setOnClickListener(this)
        Buttonsp3.setOnClickListener(this)
 //       ButtonAll.setOnClickListener(this)
        setButtons(false)
        //ButtonAll.alpha=0f

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        furi = intent.extras.getInt("pursuit_furi")
        speed = intent.extras.getInt("okn_speed")
        direction = intent.extras.getInt("okn_direction")
        //      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        hideSystemUI()
        KeepScreenOn=true
        // タイマーの始動
        mTimer = Timer()
        var holder = surfaceView.holder
        holder.addCallback(this)
        time0=System.currentTimeMillis()
        timetap=System.currentTimeMillis()-300
        Log.d("CANVASstart","start")
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                drawCanvas()
                if(KeepScreenOn&&(System.currentTimeMillis()-time0)>1000*60*10){
                    KeepScreenOn=false
                    mHandler.post{
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }
            }
        }, 100, 16)
    }
    override fun onDestroy() {
        super.onDestroy()
        if (mTimer != null){
            mTimer!!.cancel()
            mTimer = null
        }
    }
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        //       surfaceWidth = width
        //       surfaceHeight = height
        ww=width
        wh=height
        bw=(ww/10).toLong()
        bd=(ww/5).toLong()
        //       Log.d("WIDTH:",ww.toString())
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
    }

    private fun drawCanvas() {
        val canvas = surfaceView.holder.lockCanvas()
        if(ww==0||canvas==null)return
        canvas.drawColor(Color.WHITE)
        var torg: Long
        val tt=System.currentTimeMillis()-time0
        if (speed == 1) torg = tt / 3
        else if (speed == 2) torg = tt * 2 / 3
        else torg = tt * 4 / 3
        torg=torg*ww/1024//amazon KFAUWI(width:1024)を基準

         if(direction==2) {
            for (i in 0..4) {
                var xs = (i * bd + torg) % ww
                canvas.drawRect(xs.toFloat(), 0f, xs.toFloat() + bw.toFloat(), wh.toFloat(), paintblack)
                if (xs + bw > ww) {
                    xs = xs + bw - ww
                    canvas.drawRect(0f, 0f, xs.toFloat(), wh.toFloat(), paintblack)
                }
            }
        }else{
            for (i in 0..4) {
                var xs = (i * bd + torg) % ww
                canvas.drawRect((ww-xs).toFloat(), 0f, (ww-xs).toFloat() + bw.toFloat(), wh.toFloat(), paintblack)
                if (ww-xs+bw >ww ) {
                    xs = bw-xs
                    canvas.drawRect(0f, 0f, xs.toFloat(), wh.toFloat(), paintblack)
                }
            }
        }
        //     Log.d("CANVAS:",tt.toString())
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.Buttonleft->{
                direction=1
            }
            R.id.Buttonright->{
                direction=2
            }
            R.id.Buttonsp1->{
                speed=1
            }
            R.id.Buttonsp2->{
                speed=2
            }
            R.id.Buttonsp3->{
                speed=3
            }

         }
    }
    override fun onTouchEvent(event: MotionEvent) :Boolean {

        when(event.getAction()) {

            MotionEvent.ACTION_UP -> {
                if (Buttonright.alpha == 0f) {
                    setButtons(true)
                } else {
                    setButtons(false)
                }
                if ((System.currentTimeMillis() - timetap) < 300) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    val intent = Intent()
                    intent.putExtra("pursuit_furi",furi)
                    intent.putExtra("okn_speed",speed)
                    intent.putExtra("okn_direction",direction)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
            timetap=System.currentTimeMillis()
            }
        }
        return super.onTouchEvent(event)
    }
    private fun setButtons(flag: Boolean){
        if(flag==true){
            Buttonright.alpha=1.0f
            Buttonright.isEnabled=true
            Buttonleft.alpha=1.0f
            Buttonleft.isEnabled=true
            Buttonsp1.alpha=1.0f
            Buttonsp1.isEnabled=true
            Buttonsp2.alpha=1.0f
            Buttonsp2.isEnabled=true
            Buttonsp3.alpha=1.0f
            Buttonsp3.isEnabled=true

        }else{
            Buttonright.alpha=0f
            Buttonright.isEnabled=false
            Buttonleft.alpha=0f
            Buttonleft.isEnabled=false
            Buttonsp1.alpha=0f
            Buttonsp1.isEnabled=false
            Buttonsp2.alpha=0f
            Buttonsp2.isEnabled=false
            Buttonsp3.alpha=0f
            Buttonsp3.isEnabled=false
        }
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
                    intent.putExtra("pursuit_furi",furi)
                    intent.putExtra("okn_speed",speed)
                    intent.putExtra("okn_direction",direction)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
                keytap=System.currentTimeMillis()
                if(e.keyCode==leftN){
                    direction=1
                    speed += 1
                    if(speed>3){
                        speed=1
                    }
                }else if(e.keyCode==rightN){
                    direction=2
                    speed += 1
                    if(speed>3){
                        speed=1
                    }
                }
            }else{
                return super.dispatchKeyEvent(e)
            }
            return true
        }
        return super.dispatchKeyEvent(e)
    }
}