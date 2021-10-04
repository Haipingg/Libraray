package com.example.onlinelibrary.ui.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.onlinelibrary.R
import com.example.onlinelibrary.logic.ZXingUtils
import com.example.onlinelibrary.ui.SecondActivity
import kotlinx.android.synthetic.main.fragment_add.*
import kotlin.concurrent.thread


class AddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add, container, false)
        return view
    }

    val updateText = 1
    val handler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message) {
            val titles = (activity as SecondActivity).getTitles()
            when(msg.what){
               updateText ->{
                   //这里是UI二维码界面代码，使用handler在子线程中运行
                   val context = "$titles"
                   val mColor = Color.BLACK
                   val bitmap = ZXingUtils.createQRImage(context, 800, 800, mColor)
                   Glide.with(this@AddFragment).load(bitmap).into(QrCodeImage)
               }
            }
        }
    }

    @SuppressLint("SetTextI18n", "HandlerLeak")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val titles = (activity as SecondActivity).getTitles()
        Log.d("new","$titles")

        usermassge.setText("用户: "+titles)
        QrCode.setOnClickListener {
            //生成二维码
            thread {
                val msg = Message()
                msg.what = updateText
                handler.sendMessage(msg)        //将message对象发送出去,handler解析异步消息，将子线程方法调用到主线程用
            }

        }

        btn_wd.setOnClickListener {
            val intent = Intent("com.example.activitytest.ACTION_START")
            intent.addCategory("com.example.activitytest.MY_BOOK")
            startActivity(intent)
        }

    }

}