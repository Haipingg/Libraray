package com.example.onlinelibrary.ui


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinelibrary.R
import com.example.onlinelibrary.logic.MyDatabaseHelper
import kotlinx.android.synthetic.main.activity_singup.*
import kotlin.math.log

class SingupActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {
    var change = ""
    @SuppressLint("ResourceType")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        initview()

        btn_back.setOnClickListener {
            val intent = Intent(this, First::class.java)
            startActivity(intent)
        }


    }

    private fun initview() {
        rg_sex.setOnCheckedChangeListener(this)
        btn_updata.setOnClickListener {
            val dbHelper = MyDatabaseHelper(this,"UserTable.db",1)
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
                    val db = dbHelper.writableDatabase
                    val vallue1 = ContentValues().apply{
                        put("name",UserName)
                        put("password",pwdone)
                        put("sex",change)
                        put("phone",userphone)
                    }
                    db.insert("User",null,vallue1)          //存储用户数据到数据库

                    Toast.makeText(this, "注册成功！请返回登录界面进行登录", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onCheckedChanged(group:  RadioGroup?, rgbtnId: Int) {
        when(rgbtnId){
            R.id.rgbtn_boy-> {
                change = rgbtn_boy.text.toString()
                Toast.makeText(this,"你的性别是男",Toast.LENGTH_SHORT).show()
            }
            R.id.rgbtn_girl ->{
                change = rgbtn_girl.text.toString()
                Toast.makeText(this,"你的性别是女" ,Toast.LENGTH_SHORT).show()
            }
        }

    }

}
