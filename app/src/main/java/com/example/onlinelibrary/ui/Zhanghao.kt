package com.example.onlinelibrary.ui
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinelibrary.R
import kotlinx.android.synthetic.main.activity_zhanghao.*
import kotlinx.android.synthetic.main.activity_zhanghao.name5
import kotlinx.android.synthetic.main.fragment_more.*

class Zhanghao: AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zhanghao)

        name8.setOnClickListener{
            val intent = Intent(this, First::class.java)
            startActivity(intent)
        }



    }





}