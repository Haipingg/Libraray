package com.example.onlinelibrary.ui


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
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
    companion object {
        private val DEFAULT_COUNTRY = arrayOf("中国", "42", "86")
        private const val TAG = "VerifyActivity"
        private const val COUNTDOWN = 60
        private  val TEMP_CODE = null
        private const val KEY_START_TIME = "start_time"
        private const val REQUEST_CODE_VERIFY = 1001
    }
    private var tvSms: TextView? = null
    private var tvAudio: TextView? = null
    private var etPhone: EditText? = null
    private var etCode: EditText? = null
    private var tvCode: TextView? = null
    private var tvVerify: TextView? = null
    private var callback: FakeActivity? = null

    private var currentId: String? = null
    private var tvCountry: TextView? = null
    private var currentPrefix: String? = null
    private var tvToast: TextView? = null
    private var handler: Handler? = null
    private var eventHandler: EventHandler? = null
    private var currentSecond = 0
    private var helper: SharePrefrenceHelper? = null
    private var toast: Toast? = null
    private var change = ""
    private var flag = false
    private var Sms = true
    @SuppressLint("ResourceType")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        initListener()

        btn_back.setOnClickListener {
            val intent = Intent(this, First::class.java)
            startActivity(intent)
        }

        /**默认获取短信和验证按钮不可点击，输入达到规范后，可点击*/
        btn_updata!!.isEnabled = false
        tv_send!!.isEnabled = false
        /**默认使用中国区号*/
        currentId = DEFAULT_COUNTRY.get(1)
        currentPrefix = DEFAULT_COUNTRY.get(2)
        /**helper初始赋值*/
        helper = SharePrefrenceHelper(this)
        helper!!.open("sms_sp")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_VERIFY) {
            edt_phone.setText("")
            edt_sms.setText("")
            /** 重置"获取验证码"按钮*/
            tv_send.setText(R.string.smssdk_get_code)
            tv_send.setEnabled(true)
            if (handler != null) {
                handler!!.removeCallbacksAndMessages(null)
            }
        }
    }

    /**性别选择按钮*/
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

    @SuppressLint("HandlerLeak")
    private fun initListener(){
        tv_send.setOnClickListener(this)
        rg_sex.setOnCheckedChangeListener(this)
        btn_updata.setOnClickListener(this)
        btn_yanzheng.setOnClickListener(this)
        /**发送验证码按钮是否可点击*/
        edt_phone!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            /**手机号输入大于5位，获取验证码按钮可点击*/
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                tv_send!!.isEnabled = edt_phone!!.text != null && edt_phone!!.text.length > 5
            }
            override fun afterTextChanged(s: Editable) {}
        })

        /**注册验证按钮是否可点击*/
        edt_sms!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            /**验证码输入6位并且手机大于5位，验证按钮可点击*/
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                btn_updata!!.setEnabled(edt_sms!!.text != null && edt_sms!!.text.length >= 6 && edt_phone!!.text != null && edt_phone!!.text.length > 5)
            }
            override fun afterTextChanged(s: Editable) {}
        })

        /**获取短信验证码并倒计时60秒*/
        handler = @SuppressLint("HandlerLeak")
        object : Handler() {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                if (tv_send != null) {
                    if (currentSecond > 0) {
                        tv_send.setText(getString(R.string.smssdk_get_code) + " (" + currentSecond + "s)")
                        tv_send.setEnabled(false)
                        currentSecond--
                        handler!!.sendEmptyMessageDelayed(0, 1000)
                    } else {
                        tv_send.setText(R.string.smssdk_get_code)
                        tv_send.setEnabled(true)
                    }
                }
            }
        }
        /**短信信息验证过程*/
        eventHandler = object : EventHandler() {
            override fun afterEvent(event: Int, result: Int, data: Any) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    runOnUiThread { //提交验证成功，跳转成功页面，否则toast提示
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            Toast.makeText(this@SingupActivity,"验证成功请点击注册",Toast.LENGTH_SHORT).show()
                            flag = true
                            Log.d("打印","$flag")
                        } else {
                            flag = false
                            processError(data)
                        }
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
    /**点击事件*/
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_yanzheng -> {
                if (!isNetworkConnected()) {
                    Toast.makeText(
                        this@SingupActivity,
                        getString(R.string.smssdk_network_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                /**判断短信验证码是否正确*/
                SMSSDK.submitVerificationCode(
                    currentPrefix,
                    edt_phone.text.toString().trim { it <= ' ' },
                    edt_sms.text.toString()
                )
            }

                R.id.btn_updata -> {
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
                        if (flag == false){
                            return
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
            R.id.tv_send -> {
                //获取验证码间隔时间小于1分钟，进行toast提示，在当前页面不会有这种情况，但是当点击验证码返回上级页面再进入会产生该情况
                val startTime = helper!!.getLong(KEY_START_TIME)
                if (System.currentTimeMillis() - startTime < COUNTDOWN * 1000) {
                    showErrorToast(getString(R.string.smssdk_busy_hint))

                }
                if (!isNetworkConnected()) {
                    Toast.makeText(
                        this@SingupActivity,
                        getString(R.string.smssdk_network_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if (Sms == true) {
                    SMSSDK.getVerificationCode(
                        currentPrefix,
                        edt_phone!!.text.toString().trim { it <= ' ' },
                        TEMP_CODE,
                        null
                    )
                }
            }
        }
    }
    /**短信验证报错弹窗*/
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

