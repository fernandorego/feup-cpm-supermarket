package org.feup.group4.supermarket.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.feup.group4.supermarket.model.Receipt
import java.time.LocalDate

class ReceiptsRepository private constructor(context: android.content.Context) :
    SQLiteOpenHelper(context, "receipts.db", null, 1) {
    companion object {
        private var instance: ReceiptsRepository? = null

        fun getInstance(context: android.content.Context): ReceiptsRepository {
            if (instance == null) {
                instance = ReceiptsRepository(context)
                instance!!.receipts = instance!!.getAllReceipts()
            }
            return instance!!
        }
    }

    private var receipts: MutableList<Receipt> = arrayListOf()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS receipts (" +
                    "id INTEGER NOT NULL PRIMARY KEY," +
//                    "date TEXT NOT NULL," +
                    "total REAL NOT NULL" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS receipts")
        onCreate(db)
    }

    fun addReceipt(receipt: Receipt) {
        val db = writableDatabase
        val values = ContentValues()
//        values.put("id", receipt.id)
//        values.put("date", receipt.date.toString())
        values.put("total", receipt.total)
        db.insert("receipts", null, values)
        db.close()

        receipts.add(receipt)
    }

    fun getReceipt(id: Int): Receipt? {
        val db = readableDatabase
        val cursor = db.query("receipts", arrayOf("total"), "id = ?", arrayOf(id.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            val receipt = Receipt(
                cursor.getDouble(0)
            )
            cursor.close()
            db.close()
            return receipt
        }
        cursor.close()
        db.close()
        return null
    }

    fun getAllReceipts(): MutableList<Receipt> {
        val receipts = mutableListOf<Receipt>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM receipts", null)
        if (cursor.moveToFirst()) {
            do {
                val receipt = Receipt(
//                    cursor.getInt(cursor.getColumnIndex("id")),
//                    java.time.LocalDate.parse(cursor.getString(cursor.getColumnIndex("date"))),
                    cursor.getDouble(0)
                )
                receipts.add(receipt)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return receipts
    }

    fun getAll(): List<Receipt> {
        return receipts
    }
}