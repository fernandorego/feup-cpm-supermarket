package org.feup.group4.supermarket.repository

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.feup.group4.supermarket.model.Product

class ProductRepository private constructor(context: android.content.Context) :
    SQLiteOpenHelper(context, "products.db", null, 1) {
    companion object {
        private var instance: ProductRepository? = null

        fun getInstance(context: android.content.Context): ProductRepository {
            if (instance == null) {
                instance = ProductRepository(context)
                instance!!.products = instance!!.getAllProducts()
            }
            return instance!!
        }
    }

    private var products: MutableList<Pair<Product, Int>> = arrayListOf()

    override fun onCreate(db: android.database.sqlite.SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS products (" +
                    "name TEXT NOT NULL PRIMARY KEY," +
                    "price REAL NOT NULL," +
                    "uuid TEXT NOT NULL" +
                    ")"
        )
    }

    override fun onUpgrade(db: android.database.sqlite.SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS products")
        onCreate(db)
    }

    fun removeAll() {
        val db = writableDatabase
        db.execSQL("DELETE FROM products")
        db.close()
        products.clear()
    }

    fun addProduct(product: Product) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", product.name)
        values.put("price", product.price)
        values.put("uuid", product.uuid.toString())
        db.insert("products", null, values)
        db.close()

        products.add(Pair(product, 1))
    }

    fun updateProduct(product: Product) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", product.name)
        values.put("price", product.price)
        db.update("products", values, "name = ?", arrayOf(product.name))
        db.close()

        products.forEach {
            if (it.first.name == product.name) {
                products[products.indexOf(it)] = Pair(product, it.second)
                Log.w("ProductRepository", products.toString())
            }
        }
    }

    fun removeProduct(product: Product) {
        val db = writableDatabase
        db.delete("products", "name = ?", arrayOf(product.name))
        db.close()

        products.forEach {
            if (it.first.name == product.name) {
                products.remove(it)
            }
        }
    }

    fun getProduct(productName: String): Product? {
        val db = readableDatabase
        val cursor = db.query(
            "products",
            arrayOf("name", "price", "uuid"),
            "name = ?",
            arrayOf(productName),
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            val price = cursor.getDouble(1)
            val uuid = java.util.UUID.fromString(cursor.getString(2))
            cursor.close()
            db.close()
            return Product(name, price, uuid)
        }
        cursor.close()
        db.close()
        return null
    }

    @SuppressLint("Range")
    fun getAllProducts(): MutableList<Pair<Product, Int>> {
        val products = mutableListOf<Pair<Product, Int>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM products", null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val price = cursor.getDouble(cursor.getColumnIndex("price"))
                val uuid = java.util.UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid")))
                products.add(Pair(Product(name, price, uuid), 1))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return products
    }

    fun getAll(): List<Pair<Product, Int>> {
        return products
    }
}