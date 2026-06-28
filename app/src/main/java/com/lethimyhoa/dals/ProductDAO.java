package com.lethimyhoa.dals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.lethimyhoa.models.Product;
import java.util.ArrayList;

public class ProductDAO {
    public static final String DATABASE_NAME = "K234111ESales.sqlite";
    public static final String TABLE_NAME = "Product";

    public static ArrayList<Product> getProducts(Context context) {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            double price = cursor.getDouble(2);
            int quantity = cursor.getInt(3);
            double coupon = cursor.getDouble(4);
            double vat = cursor.getDouble(5);
            String cateId = cursor.getString(6);
            
            products.add(new Product(id, name, price, quantity, coupon, vat, cateId));
        }
        cursor.close();
        database.close();
        return products;
    }
}
