package com.example.naville.kotlinmap.model.contract

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.example.naville.kotlinmap.model.contract.CreateDB.FeedEntry.SQL_CREATE_ENTRIES
import com.example.naville.kotlinmap.model.contract.CreateDB.FeedEntry.SQL_DELETE_ENTRIES


object CreateDB {


    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "users"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ID = "_id"

//        const val SQL_CREATE_ENTRIES =
//                "CREATE TABLE ${FeedEntry.TABLE_NAME} (" +
//                        "${BaseColumns._ID} INTEGER PRIMARY KEY," +
//                        "${FeedEntry.COLUMN_USERNAME} TEXT, "+
//                        "${FeedEntry.COLUMN_PASSWORD} TEXT)"

        const val SQL_CREATE_ENTRIES =
                "CREATE TABLE ${FeedEntry.TABLE_NAME} (" +
                        "${FeedEntry.COLUMN_ID} INTEGER PRIMARY KEY," +
                        "${FeedEntry.COLUMN_USERNAME} TEXT, "+
                        "${FeedEntry.COLUMN_PASSWORD} TEXT)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_NAME}"

    }


    class FeedReaderDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATA_BASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(SQL_CREATE_ENTRIES)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }

        override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            onUpgrade(db, oldVersion, newVersion)
        }

        companion object {
            const val DATA_BASE_VERSION = 1
            const val DATABASE_NAME = "FeedReader.db"
        }

    }

}