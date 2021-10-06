package com.example.onlinelibrary.ui
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.onlinelibrary.R
import com.example.onlinelibrary.ui.fragment.AddFragment
import com.example.onlinelibrary.ui.fragment.HomeFragment
import com.example.onlinelibrary.ui.fragment.InboxFragment
import com.example.onlinelibrary.ui.fragment.MoreFragment
import kotlinx.android.synthetic.main.activity_first.*
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        loadFragment(HomeFragment())
        setSupportActionBar(toolbar)

        /**底部导航栏设置*/
        bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_home -> {
                    title = resources.getString(R.string.home)
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_add -> {
                    title = resources.getString(R.string.add)
                    loadFragment(AddFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_inbox -> {
                    title = resources.getString(R.string.inbox)
                    loadFragment(InboxFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_more -> {
                    title = resources.getString(R.string.more)
                    loadFragment(MoreFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

    }

    /**actionbar设置*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.looking -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.search -> {
                val intent = Intent(this, SwiperActivity::class.java)
                startActivity(intent)
            }
            R.id.settings -> {
                val intent = Intent(this, SwiperActivity::class.java)
                startActivity(intent)
            }
            R.id.newset -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    /**传输的用户数据信息*/
    fun getTitles(): String? {
        var user = ""
        val smsuser = intent.getStringExtra("smsextra")
        val usermassge = intent.getStringExtra("extra_data")
        if(usermassge == null){
            if (smsuser != null) {
                user = smsuser
            }
        }else{
            user = usermassge
        }
        return "$user"
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**点击返回键刷新页面，不知道有啥用*/
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    /**短信验证码登录跳转到此activity*/
    companion object {
        @JvmStatic
        fun startActivityForResult(context: Activity, requestCode: Int, success: Boolean, phone: String?) {
            val intent = Intent(context, SecondActivity::class.java)
            val good = phone
            Log.d("测试", "$good")
            context.startActivityForResult(intent, requestCode)
        }
    }


}