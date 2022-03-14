package com.example.onlinelibrary.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.smssdk.SMSSDK
import com.example.onlinelibrary.R
import com.example.onlinelibrary.logic.smsutil.DemoSpHelper
import java.util.*

class StartActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 200
    var DANGEROUS_PERMISSIONS =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
    private val permissions: MutableList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        for (permission in DANGEROUS_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                            this,
                            permission
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions.add(permission)
            }
        }
        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions.toTypedArray(),
                    PERMISSION_REQUEST_CODE
            )
        } else {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    val intent = Intent(this@StartActivity, First::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 3000)
        }


        init()
    }



    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val pers: MutableList<String> = ArrayList()
        if (PERMISSION_REQUEST_CODE == requestCode) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    pers.add(permissions[i])
                }
            }
            if (!pers.isEmpty()) {
                ActivityCompat.requestPermissions(
                        this,
                        pers.toTypedArray(),
                        PERMISSION_REQUEST_CODE
                )
            } else {
                val intent = Intent(this@StartActivity, First::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun init() {
        if (!DemoSpHelper.getInstance().isPrivacyGranted) {
            // 初始化MobTech隐私协议获取
            //PrivacyHolder.getInstance().init();
        }
        SMSSDK.setAskPermisionOnReadContact(true)
    }
}