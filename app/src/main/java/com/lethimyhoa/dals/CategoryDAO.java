package com.lethimyhoa.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lethimyhoa.models.Category;

import java.util.ArrayList;

public class CategoryDAO {
    public static final String DATABASE_NAME = "K234111ESales.sqlite";
    public static final String TABLE_NAME = "Category";

    public static SQLiteDatabase database = null;

    public static ArrayList<Category> getCategories(Context context)
    {
        ArrayList<Category> categories = new ArrayList<>();
        try {
            database = context.openOrCreateDatabase(DATABASE_NAME,
                    Context.MODE_PRIVATE, null);
            // Ensure table exists or handle missing table gracefully
            database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (CategoryId TEXT PRIMARY KEY, CategoryName TEXT)");

            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            while(cursor.moveToNext()){
                String cateId = cursor.getString(0);
                String cateName = cursor.getString(1);
                Category c = new Category(cateId, cateName);
                categories.add(c);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("CategoryDAO", "Error getting categories: " + e.getMessage());
        }
        return categories;
    }
    public static long insertCategory(Context context, Category category)
    {
        try {
            database = context.openOrCreateDatabase(DATABASE_NAME,
                    Context.MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (CategoryId TEXT PRIMARY KEY, CategoryName TEXT)");
            ContentValues values = new ContentValues();
            values.put("CategoryId", category.getCategoryId());
            values.put("CategoryName", category.getCategoryName());
            return database.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e("CategoryDAO", "Error inserting category: " + e.getMessage());
            return -1;
        }
    }
    public static long deleteCategory(Context context, Category category)
    {
        try {
            database = context.openOrCreateDatabase(DATABASE_NAME,
                    Context.MODE_PRIVATE, null);
            return database.delete(TABLE_NAME, "CategoryId=?", new String[]{category.getCategoryId()});
        } catch (Exception e) {
            Log.e("CategoryDAO", "Error deleting category: " + e.getMessage());
            return -1;
        }
    }

}

