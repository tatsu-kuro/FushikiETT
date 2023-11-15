package com.kuroda33.fushikiett

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_help.*

class HelpActivity : AppCompatActivity() {
    var timetap:Long=0
    var keytap:Long=0
    var englang:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        Log.d("debug", "The system bars are visible")
  //      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        timetap=System.currentTimeMillis()-300
        helptext.alpha = 1.0f
        helpetext.alpha = 0f
    }
    override fun onTouchEvent(event: MotionEvent) :Boolean {
        when(event.getAction()) {

            MotionEvent.ACTION_UP -> {

                if((System.currentTimeMillis()-timetap)<300){
 //                  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    finish()
                }else{
                    if(englang) {
                        helptext.alpha = 1.0f
                        helpetext.alpha = 0f
                        englang=false
                    }else{
                        helptext.alpha = 0f
                        helpetext.alpha = 1.0f
                        englang=true
                    }
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
 //               getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                if (System.currentTimeMillis() - keytap < 300){
                    finish()
                }
                else{
                    if(englang) {
                        helptext.alpha = 1.0f
                        helpetext.alpha = 0f
                        englang=false
                    }else{
                        helptext.alpha = 0f
                        helpetext.alpha = 1.0f
                        englang=true
                    }
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
