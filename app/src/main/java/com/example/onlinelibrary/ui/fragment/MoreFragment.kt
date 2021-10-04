package com.example.onlinelibrary.ui.fragment


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.onlinelibrary.R
import kotlinx.android.synthetic.main.usertwo.*
import kotlin.concurrent.thread
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class MoreFragment : Fragment() {

    private val LL01: LinearLayout? = null
    private val LL02: LinearLayout? = null
    private val LL03: LinearLayout? = null
    private var mContext: Context? = null
    private var touxiang: ImageButton? = null
    private var head // 头像Bitmap
            : Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
        mContext = activity
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name5.setOnClickListener {
            thread {
                val intent = Intent("com.example.activitytest.ACTION_START")
                intent.addCategory("com.example.activitytest.ZHANG")
                startActivity(intent)
            }
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()

        /*
        点击头像进行更换头像
         */touxiang!!.setOnClickListener { v ->
            when (v.id) {
                R.id.usertx -> showTypeDialog()
            }
        }
    }
    private fun initView() {
        touxiang = activity!!.findViewById<View>(R.id.usertx) as ImageButton
        val bt = BitmapFactory.decodeFile(MoreFragment.path + "head.jpg") // 从SD卡中找头像，转换成Bitmap
        if (bt != null) {
            val drawable: Drawable = BitmapDrawable(bt) // 转换成drawable
            touxiang!!.setImageDrawable(drawable)
        } else {
            /**
             * 如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
             *
             */
        }
    }
    private fun showTypeDialog() {
        //显示对话框
        val builder = AlertDialog.Builder(activity)
        val dialog = builder.create()
        val view = View.inflate(activity, R.layout.dialog_select_photo, null)
        val tv_select_gallery = view.findViewById<View>(R.id.tv_select_gallery) as TextView
        val tv_select_camera = view.findViewById<View>(R.id.tv_select_camera) as TextView
        tv_select_gallery.setOnClickListener(View.OnClickListener
        // 在相册中选取
        {
            val intent1 = Intent(Intent.ACTION_PICK, null)
            //打开文件
            intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(intent1, 1)
            dialog.dismiss()
        })
        tv_select_camera.setOnClickListener(object : View.OnClickListener {
            // 调用照相机
            override fun onClick(v: View) {
                val intent2 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(File(Environment.getExternalStorageDirectory(), "head.jpg")))
                startActivityForResult(intent2, 2) // 采用ForResult打开
                dialog.dismiss()
            }
        })
        dialog.setView(view)
        dialog.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK) {
                cropPhoto(data!!.data) // 裁剪图片
            }
            2 -> if (resultCode == Activity.RESULT_OK) {
                val temp = File(Environment.getExternalStorageDirectory().toString() + "/head.jpg")
                cropPhoto(Uri.fromFile(temp)) // 裁剪图片
            }
            3 -> if (data != null) {
                val extras = data.extras
                head = extras!!.getParcelable("data")
                if (head != null) {
                    /**
                     * 上传服务器代码
                     */
                    setPicToView(head!!) // 保存在SD卡中
                    touxiang!!.setImageBitmap(head) // 用ImageButton显示出来
                }
            }
            else -> {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    fun cropPhoto(uri: Uri?) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true")
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250)
        intent.putExtra("outputY", 250)
        intent.putExtra("return-data", true)
        startActivityForResult(intent, 3)
    }
    private fun setPicToView(mBitmap: Bitmap) {
        val sdStatus = Environment.getExternalStorageState()
        if (sdStatus != Environment.MEDIA_MOUNTED) { // 检测sd是否可用
            return
        }
        var b: FileOutputStream? = null
        val file = File(path)
        file.mkdirs() // 创建文件夹
        val fileName = path + "head.jpg" // 图片名字
        try {
            b = FileOutputStream(fileName)
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b) // 把数据写入文件
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            try {
                // 关闭流
                b!!.flush()
                b.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private val path = "/sdcard/MyHead/" // sd路径
    }

}