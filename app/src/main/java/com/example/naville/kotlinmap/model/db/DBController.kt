package com.example.naville.kotlinmap.model.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.naville.kotlinmap.model.contract.CreateDB


class DBController(context: Context) {

    private var db: SQLiteDatabase? = null
    //    private var database: CreateDB? = null
    private val dbHelper = CreateDB.FeedReaderDBHelper(context)


    fun insertData(username: String, password: String) {

        val values = ContentValues()
        val result: Long

        db = dbHelper.writableDatabase
        values.put(CreateDB.FeedEntry.COLUMN_USERNAME, username)
        values.put(CreateDB.FeedEntry.COLUMN_PASSWORD, password)

        result = db!!.insert(CreateDB.FeedEntry.TABLE_NAME, null, values)
        db!!.close()

        if (result.equals(-1)) {
            return println("Erro ao inserir dados!")
        } else {
            return print("Registrado com sucesso!")
        }
    }

    fun loadData(): Cursor {

        val fields = arrayOf(CreateDB.FeedEntry.COLUMN_ID, CreateDB.FeedEntry.COLUMN_USERNAME)
        val cursor: Cursor = db!!.query(CreateDB.FeedEntry.TABLE_NAME, fields, null, null, null, null, null)
        db = dbHelper.readableDatabase

        cursor.moveToFirst()

        db!!.close()

        return cursor
    }

}