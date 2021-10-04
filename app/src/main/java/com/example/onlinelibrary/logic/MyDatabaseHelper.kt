package com.example.onlinelibrary.logic

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast


class MyDatabaseHelper(val context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {      //创建SQLite数据库
    //创建用户表
    private val createUser = "create table User (" +
            " id integer primary key autoincrement," +
            "name text," +
            "password text," +
            "sex text," +
            "phone text)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createUser)
        Toast.makeText(context,"创建成功！", Toast.LENGTH_LONG).show()

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists User")
        onCreate(db)
    }

}