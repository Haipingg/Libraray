package com.example.onlinelibrary.ui


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import cn.smssdk.UserInterruptException
import com.example.onlinelibrary.R
import com.example.onlinelibrary.logic.MyDatabaseHelper
import com.example.onlinelibrary.logic.smsutil.DemoResHelper
import com.mob.tools.FakeActivity
import com.mob.tools.utils.ResHelper
import com.mob.tools.utils.SharePrefrenceHelper
import kotlinx.android.synthetic.main.activity_singup.*
import org.json.JSONObject



class SingupActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private val TAG = "SingupActivity"
    private val COUNTDOWN = 60
    private val TEMP_CODE = "1319972"
    private val REQUEST_CODE_VERIFY = 1001
    private val KEY_START_TIME = "start_time"
    private var tvToast: TextView? = null
    private val currentId: String? = null
    private val currentPrefix: String? = null
    private val callback: FakeActivity? = null
    private var handler: Handler? = null
    private var eventHandler: EventHandler? = null
    private var currentSecond = 0
    private val helper: SharePrefrenceHelper? = null
    private var toast: Toast? = null
    private var change = ""
    private var flag = false
    @SuppressLint("ResourceType")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        initview()
        initListener()

        btn_back.setOnClickListener {
            val intent = Intent(this, First::class.java)
            startActivity(intent)
        }

        btn_send.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_VERIFY) {
            edt_phone.setText("")
            edt_sms.setText("")
            // 重置"获取验证码"按钮
            btn_send.setText(R.string.smssdk_get_code)
            btn_send.setEnabled(true)
            if (handler != null) {
                handler!!.removeCallbacksAndMessages(null)
            }
        }
    }

    private fun initview() {
        rg_sex.setOnCheckedChangeListener(this)
        btn_updata.setOnClickListener {
            val dbHelper = MyDatabaseHelper(this, "UserTable.db", 1)
            val UserName = edt_UserName.text.toString()
            val pwdone = edt_pwd_one.text.toString()
            val pwdtwo = edt_pwd_two.text.toString()
            val sms = edt_sms.text.toString()
            val userphone = edt_phone.text.toString()

            if(pwdone != pwdtwo){
                Toast.makeText(this, "两次密码不匹配！", Toast.LENGTH_SHORT).show()
            }else {
                if(userphone.length != 11){
                    Toast.makeText(this, "号码长度不正确！", Toast.LENGTH_SHORT).show()
                }else{
                    if(flag == false){
                        Toast.makeText(this, "短信验证码错误！", Toast.LENGTH_SHORT).show()
                    }else{
                        val db = dbHelper.writableDatabase
                        val vallue1 = ContentValues().apply{
                            put("name", UserName)
                            put("password", pwdone)
                            put("sex", change)
                            put("phone", userphone)
                        }
                        db.insert("User", null, vallue1)          //存储用户数据到数据库

                        Toast.makeText(this, "注册成功！请返回登录界面进行登录", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onCheckedChanged(group: RadioGroup?, rgbtnId: Int) {
        when(rgbtnId){
            R.id.rgbtn_boy -> {
                change = rgbtn_boy.text.toString()
                Toast.makeText(this, "你的性别是男", Toast.LENGTH_SHORT).show()
            }
            R.id.rgbtn_girl -> {
                change = rgbtn_girl.text.toString()
                Toast.makeText(this, "你的性别是女", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initListener(){
        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                if (btn_send != null) {
                    if (currentSecond > 0) {
                        btn_send.setText(getString(R.string.smssdk_get_code) + " (" + currentSecond + "s)")
                        btn_send.setEnabled(false)
                        currentSecond--
                        handler!!.sendEmptyMessageDelayed(0, 1000)
                    } else {
                        btn_send.setText(R.string.smssdk_get_code)
                        btn_send.setEnabled(true)
                    }
                }
            }
        }

        eventHandler = object : EventHandler() {
            override fun afterEvent(event: Int, result: Int, data: Any) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    runOnUiThread { //提交验证成功，跳转成功页面，否则toast提示
                        flag = result == SMSSDK.RESULT_COMPLETE
                    }
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE || event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
                    runOnUiThread(Runnable {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            currentSecond = COUNTDOWN
                            (handler as Handler).sendEmptyMessage(0)
                            helper!!.putLong(KEY_START_TIME, System.currentTimeMillis())
                        } else {
                            if (data != null && data is UserInterruptException) {
                                // 由于此处是开发者自己决定要中断发送的，因此什么都不用做
                                return@Runnable
                            }
                            processError(data)
                        }
                    })
                }
            }
        }
        SMSSDK.registerEventHandler(eventHandler)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_updata -> {
                if (!isNetworkConnected()) {
                    Toast.makeText(
                        this,
                        getString(R.string.smssdk_network_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.btn_send -> {
                //获取验证码间隔时间小于1分钟，进行toast提示，在当前页面不会有这种情况，但是当点击验证码返回上级页面再进入会产生该情况

                if (!isNetworkConnected()) {
                    Toast.makeText(
                        this,
                        getString(R.string.smssdk_network_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
   
    private fun showErrorToast(text: String) {
        if (toast == null) {
            toast = Toast(this)
            val rootView = LayoutInflater.from(this).inflate(
                R.layout.smssdk_error_toast_layout,
                null
            )
            tvToast = rootView.findViewById<TextView>(R.id.tvToast)
            toast!!.setView(rootView)
            toast!!.setGravity(Gravity.CENTER, 0, ResHelper.dipToPx(this, -100))
        }
        tvToast?.setText(text)
        toast!!.show()
    }
    private fun isNetworkConnected(): Boolean {
        val manager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    private fun processError(data: Any) {
        var status = 0
        // 根据服务器返回的网络错误，给toast提示
        try {
            (data as Throwable).printStackTrace()
            val `object` = JSONObject(
                data.message
            )
            val des = `object`.optString("detail")
            status = `object`.optInt("status")
            if (!TextUtils.isEmpty(des)) {
                showErrorToast(des)
                return
            }
        } catch (e: Exception) {
            Log.w(TAG, "", e)
        }
        // 如果木有找到资源，默认提示
        val resId = DemoResHelper.getStringRes(
            applicationContext,
            "smsdemo_network_error"
        )
        val netErrMsg = applicationContext.resources.getString(resId)
        showErrorToast(netErrMsg)
    }


}

