package com.kuroda33.fushikiett

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

const val MY_REQUEST_CODE = 1000
class MainActivity : AppCompatActivity() , View.OnClickListener{
    var type:Int=1
    var ww: Int = 0
    var wh: Int = 0
    var furi:Int = 1
    //var targetWidth:Int =1
    var speed:Int = 2
    var direction:Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Buttonpursuit.setOnClickListener(this)
        Buttonsaccade.setOnClickListener(this)
        Buttonokn.setOnClickListener(this)
        Buttoncarolic.setOnClickListener(this)
        Buttoncarolicokn.setOnClickListener(this)
        Buttonhelp.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.Buttonokn-> okn()
            R.id.Buttonpursuit -> pursuit()
            R.id.Buttoncarolic -> carolic()
            R.id.Buttoncarolicokn -> carolicokn()
            R.id.Buttonsaccade -> saccade()
            R.id.Buttonhelp -> help()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == MY_REQUEST_CODE && intent != null) {
            if (resultCode == Activity.RESULT_OK) {
                val received = data!!
                // in case of nothing exist in Intent, just preventing NPE
                furi = received.extras.getInt("pursuit_furi")
                speed = received.extras.getInt("okn_speed")
                direction = received.extras.getInt("okn_direction")
            }
        }
    }
    private fun pursuit(){
        val intent= Intent(this,PusuitActivity::class.java)
        intent.putExtra("pursuit_furi",furi)
        intent.putExtra("okn_speed",speed)
        intent.putExtra("okn_direction",direction)
        startActivityForResult(intent, MY_REQUEST_CODE)
        //startActivity(intent)
    }
    private fun saccade(){
        val intent= Intent(this,SaccadeActivity::class.java)
        startActivity(intent)
    }
    private fun okn(){
        val intent= Intent(this,OKNActivity::class.java)
        intent.putExtra("pursuit_furi",furi)
        intent.putExtra("okn_speed",speed)
        intent.putExtra("okn_direction",direction)
        startActivityForResult(intent, MY_REQUEST_CODE)
        //startActivity(intent)
    }
    private fun carolic(){
        val intent= Intent(this,CarolicActivity::class.java)
        startActivity(intent)
    }
    private fun carolicokn(){
        val intent= Intent(this,CarolicOKNActivity::class.java)
        startActivity(intent)
    }
    private fun help(){
        val intent= Intent(this,HelpActivity::class.java)
        startActivity(intent)
    }
    override fun dispatchKeyEvent(e: KeyEvent): Boolean {
        var joy: InputDevice = e.device
        val joyName = joy.name
        // DOWNとUPが取得できるのでログの2重表示防止のためifer
        Log.d("KeyCode", "InputDevice:" + joyName)// toString())
        Log.d("KeyCode", "KeyCode:" + e.getKeyCode())
        var leftN:Int
        var rightN:Int
        var midN:Int
        if (e.action == KeyEvent.ACTION_DOWN){//||e.action== KeyEvent.ACTION_UP) {
            Log.d("KeyCode", "InputDevice:" + joyName)// toString())
            Log.d("KeyCode", "KeyCode:" + e.getKeyCode())

            if (joyName.startsWith("ELECOM BT Remote")) {//ElECOM BT REMOTE
                leftN=88
                rightN=87
                midN=85
            }else{
                return super.dispatchKeyEvent(e)
            }
            if(e.action== KeyEvent.ACTION_UP){
            }else{//KeyEvent.ACTION_DOWN
                if(e.keyCode==leftN) {//leftbuttonで実行
                    type -= 1
                }else if(e.keyCode==rightN) {//rightbuttonで実行
                    type += 1
                }else if(e.keyCode==midN){//midbuttonで実行
                    if(type==0)pursuit()
                    else if(type==1)saccade()
                    else if(type==2)okn()
                    else if(type==3)carolic()
                    else if(type==4)carolicokn()
                    else help()
                }
                if (type>5)type=0
                else if(type<0)type=5
                var pursuit:ImageButton = findViewById(R.id.Buttonpursuit)
                var saccade:ImageButton = findViewById(R.id.Buttonsaccade)
                var okn:ImageButton = findViewById(R.id.Buttonokn)
                var carolic:ImageButton = findViewById(R.id.Buttoncarolic)
                var carolicokn:ImageButton = findViewById(R.id.Buttoncarolicokn)
                var help:ImageButton = findViewById((R.id.Buttonhelp))
                pursuit.setImageResource(R.drawable.pursuit1)
                saccade.setImageResource(R.drawable.saccade1)
                okn.setImageResource(R.drawable.okn1)
                carolic.setImageResource(R.drawable.carolicett1)
                carolicokn.setImageResource(R.drawable.carolicokn1)
                help.setImageResource(R.drawable.help1)
                if(type==0)pursuit.setImageResource(R.drawable.pursuit)
                else if(type==1)saccade.setImageResource(R.drawable.saccade)
                else if(type==2)okn.setImageResource(R.drawable.okn)
                else if(type==3)carolic.setImageResource(R.drawable.carolicett)
                else if(type==4)carolicokn.setImageResource(R.drawable.carolicokn)
                else help.setImageResource(R.drawable.help)
            }
            return true
        }
        return super.dispatchKeyEvent(e)
    }
}
