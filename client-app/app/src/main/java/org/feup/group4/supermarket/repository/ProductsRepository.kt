package org.feup.group4.supermarket.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.feup.group4.supermarket.model.Product
import java.util.*

class ProductsRepository private constructor(context: android.content.Context) :
    SQLiteOpenHelper(context, "products.db", null, 1) {
    companion object {
        private var instance: ProductsRepository? = null

        fun getInstance(context: android.content.Context): ProductsRepository {
            if (instance == null) {
                instance = ProductsRepository(context)
                instance!!.products = instance!!.getAllProducts()
            }
            return instance!!
        }
    }

    private var products: MutableList<Product> = arrayListOf()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS products (" +
                    "name TEXT NOT NULL PRIMARY KEY," +
                    "price REAL NOT NULL," +
                    "uuid TEXT NOT NULL" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS products")
        onCreate(db)
    }

    fun deleteDatabase() {
        writableDatabase.execSQL("DROP TABLE IF EXISTS products")
        onCreate(writableDatabase)
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

        products.add(product)
    }

    fun updateProduct(product: Product) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", product.name)
        values.put("price", product.price)
        values.put("uuid", product.uuid.toString())
        db.update("products", values, "name = ?", arrayOf(product.name))
        db.close()

        products.forEach {
            if (it.name == product.name) {
                products[products.indexOf(it)] = product
            }
        }
    }

    fun removeProduct(product: Product) {
        val db = writableDatabase
        db.delete("products", "name = ?", arrayOf(product.name))
        db.close()

        products.forEach {
            if (it.name == product.name) {
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
            val uuid = UUID.fromString(cursor.getString(2))
            cursor.close()
            db.close()
            return Product(name, price, uuid)
        }
        cursor.close()
        db.close()
        return null
    }

    fun getAllProducts(): MutableList<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM products", null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(0)
                val price = cursor.getDouble(1)
                val uuid = UUID.fromString(cursor.getString(2))
                products.add(Product(name, price, uuid))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return products
    }

    fun getAll(): List<Product> {
        return products
    }
}