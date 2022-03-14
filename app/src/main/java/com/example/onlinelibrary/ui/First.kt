package com.example.onlinelibrary.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinelibrary.R
import com.example.onlinelibrary.logic.MyDatabaseHelper
import com.example.onlinelibrary.logic.smsprivacy.OnDialogListener
import com.example.onlinelibrary.logic.smsprivacy.PrivacyDialog
import com.example.onlinelibrary.logic.smsutil.DemoSpHelper
import com.example.onlinelibrary.ui.smsui.VerifyActivity
import com.mob.MobSDK
import com.mob.OperationCallback
import kotlinx.android.synthetic.main.activity_first.*
import java.util.*


class First : AppCompatActivity(){
    private val TAG = "First"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        initcreate()
        initview()
    }

    /**自定义类Util实现toast不频繁重复弹出*/
    object Util {
        private var toast: Toast? = null
        fun showToast(context: Context?, content: String?) {
            if (toast == null) {
                toast = Toast.makeText(context,
                        content,
                        Toast.LENGTH_SHORT)
            } else {
                toast!!.setText(content)
            }
            toast!!.show()
        }
    }

    private fun initcreate() {
        val dbHelper = MyDatabaseHelper(this, "UserTable.db", 1)
        /**sharePreference实现的记住用户名和密码功能*/
        val userInfo = getPreferences(MODE_PRIVATE)
        val isRemeberMe = userInfo.getBoolean("remeber_password", false)
        if (isRemeberMe) {
            edt_UserId.setText(userInfo.getString("username", ""))
            edt_pwd1.setText(userInfo.getString("password", ""))
            cb_remeber_me.isChecked = true
        }
        btn_dl.setOnClickListener {
            /**验证用户名和密码是否正确*/
            val db = dbHelper.writableDatabase
            val cursor = db.query("User", null, null, null, null, null, null)
            if(cursor.moveToFirst()){
                do {
                    //获取到数据库中的用户名和密码
                    val phone = cursor.getString(cursor.getColumnIndex("phone"))
                    val userpassword = cursor.getString(cursor.getColumnIndex("password"))
                    val username=edt_UserId.text.toString()
                    val password=edt_pwd1.text.toString()
                    val data = username
                    if(phone == username && userpassword == password){
                        val editor = userInfo.edit()
                        if (cb_remeber_me.isChecked) {
                            editor.putBoolean("remeber_password", true)
                            editor.putString("username", username)
                            editor.putString("password", password)
                        } else {
                            editor.clear()
                        }
                        editor.apply()
                        Util.showToast(this, "登录成功")
                        Log.d("测试", "登录成功: ")
                        val intent = Intent(this, SecondActivity::class.java)
                        intent.putExtra("extra_data", data)
                        startActivity(intent)
                        finish()
                    }else{
                        Log.d("测试", "登陆失败: ")
                    }
                }while (cursor.moveToNext())
            }
        }
    }

    private fun initview(){

        btn_yjdl.setOnClickListener(View.OnClickListener { startActivity(Intent(this@First, VerifyActivity::class.java)) })
        btn_zhuce.setOnClickListener(View.OnClickListener { startActivity(Intent(this@First, SingupActivity::class.java)) })
        if (!DemoSpHelper.getInstance().isPrivacyGranted) {
            val privacyDialog = PrivacyDialog(this@First, object : OnDialogListener {
                override fun onAgree() {
                    uploadResult(true)
                    DemoSpHelper.getInstance().isPrivacyGranted = true
                    goOn()
                }

                override fun onDisagree() {
                    uploadResult(false)
                    DemoSpHelper.getInstance().isPrivacyGranted = false
                    val handler = Handler {
                        System.exit(0)
                        false
                    }
                    handler.sendEmptyMessageDelayed(0, 500)
                }
            })
            privacyDialog.show()
        } else {
            goOn()
        }
    }



    private fun uploadResult(granted: Boolean) {
        MobSDK.submitPolicyGrantResult(granted, object : OperationCallback<Void?>() {
            override fun onComplete(aVoid: Void?) {
                // Nothing to do
            }

            override fun onFailure(throwable: Throwable) {
                // Nothing to do
                Log.e(TAG, "Submit privacy grant result error", throwable)
            }
        })
    }

    /**可以继续流程，一般是接受隐私条款后*/
    private fun goOn() {
        // 动态权限申请
        if (Build.VERSION.SDK_INT >= 23) {
            val readPhone = checkSelfPermission("android.permission.READ_PHONE_STATE")
            val receiveSms = checkSelfPermission("android.permission.RECEIVE_SMS")
            val readContacts = checkSelfPermission("android.permission.READ_CONTACTS")
            val readSdcard = checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE")
            var requestCode = 0
            val permissions = ArrayList<String>()
            if (readPhone != PackageManager.PERMISSION_GRANTED) {
                requestCode = requestCode or (1 shl 0)
                permissions.add("android.permission.READ_PHONE_STATE")
            }
            if (receiveSms != PackageManager.PERMISSION_GRANTED) {
                requestCode = requestCode or (1 shl 1)
                permissions.add("android.permission.RECEIVE_SMS")
            }
            if (readContacts != PackageManager.PERMISSION_GRANTED) {
                requestCode = requestCode or (1 shl 2)
                permissions.add("android.permission.READ_CONTACTS")
            }
            if (readSdcard != PackageManager.PERMISSION_GRANTED) {
                requestCode = requestCode or (1 shl 3)
                permissions.add("android.permission.READ_EXTERNAL_STORAGE")
            }
            if (requestCode > 0) {
                val permission = arrayOfNulls<String>(permissions.size)
                requestPermissions(permissions.toArray(permission), requestCode)
                return
            }
        }
    }



}
