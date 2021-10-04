package com.example.onlinelibrary.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinelibrary.R
import com.example.onlinelibrary.logic.MyDatabaseHelper
import kotlinx.android.synthetic.main.activity_first.*


class First : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        initcreate()


        btn_zhuce.setOnClickListener{
            val intent = Intent(this, SingupActivity::class.java)
            startActivity(intent)
        }
        btn_yjdl.setOnClickListener{
            val intent = Intent(this, VerifyActivity::class.java)
            startActivity(intent)
        }

    }

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
        val userInfo = getPreferences(MODE_PRIVATE)
        val isRemeberMe = userInfo.getBoolean("remeber_password", false)
        if (isRemeberMe) {
            edt_UserId.setText(userInfo.getString("username", ""))
            edt_pwd1.setText(userInfo.getString("password", ""))
            cb_remeber_me.isChecked = true
        }
        btn_dl.setOnClickListener {
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
                        Util.showToast(this,"登录成功")
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



}
