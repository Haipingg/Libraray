package com.example.onlinelibrary.logic


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlinelibrary.R
import com.example.onlinelibrary.ui.fragment.HomeFragment

class BookAdapter(val context: HomeFragment, val bookList: List<Book>):
    RecyclerView.Adapter<BookAdapter.ViewHolder>()
{

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val bookImage: ImageView = view.findViewById(R.id.bookImage)
        val bookName: TextView = view.findViewById(R.id.bookName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = bookList[position]
        holder.bookName.text = book.name
        Glide.with(context).load(book.imageId).into(holder.bookImage)
    }

    override fun getItemCount() = bookList.size
}