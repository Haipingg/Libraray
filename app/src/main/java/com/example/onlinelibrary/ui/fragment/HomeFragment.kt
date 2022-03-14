package com.example.onlinelibrary.ui.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.onlinelibrary.R
import com.example.onlinelibrary.logic.Book
import com.example.onlinelibrary.logic.BookAdapter
import com.example.onlinelibrary.logic.swper.DataBean
import com.example.onlinelibrary.logic.swper.ImageAdapter
import com.example.onlinelibrary.ui.BorrowActivity
import com.youth.banner.Banner
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.util.BannerUtils
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.concurrent.thread


class HomeFragment :Fragment(){

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBooks()



        val layoutManager = GridLayoutManager(activity, 4)
        recyclerView.layoutManager = layoutManager
        val adapter = BookAdapter(this, bookList)
        recyclerView.adapter =adapter
        fab.setOnClickListener{
            thread {
                val intent = Intent(activity, BorrowActivity::class.java)
                startActivity(intent)
                }
            }
        /**下拉刷新*/
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            refreshBooks(adapter)
        }
    }

    private fun  refreshBooks(adapter: BookAdapter){
        thread {
            Thread.sleep(2000)
            activity?.runOnUiThread{
                initBooks()
                adapter.notifyDataSetChanged()
                swipeRefresh.isRefreshing = false
            }
        }
    }

             val bookList = ArrayList<Book>()
             private fun initBooks(){
                 val books = mutableListOf(Book("newbookimg", R.drawable.bg2),
                         Book("哈哈哈", R.drawable.bg1))
                 bookList.clear()
                 repeat(50){
                     val index = (0 until  books.size).random()
                     bookList.add((books[index]))
                        }
                }

}





