package com.example.databasetry1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "studentApp.db";
    public static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // User table
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT UNIQUE, password TEXT)");

        // Student assignment table
        db.execSQL("CREATE TABLE students(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, gender TEXT, subjects TEXT, studentClass TEXT, phone TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add phone column if upgrading
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE students ADD COLUMN phone TEXT");
        }
    }

    // =============================
    // USER AUTH METHODS
    // =============================

    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("email", email);
        cv.put("password", password);

        long result;
        try {
            result = db.insertOrThrow("users", null, cv);
        } catch (Exception e) {
            return false; // Email already exists
        }

        return result != -1;
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // =============================
    // STUDENT TABLE METHODS
    // =============================

    public boolean insertStudent(String name, String gender, String subjects, String studentClass, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("gender", gender);
        cv.put("subjects", subjects);
        cv.put("studentClass", studentClass);
        cv.put("phone", phone);

        long result = db.insert("students", null, cv);
        return result != -1;
    }

    public ArrayList<String> getAllStudents() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM students", null);

        while (cursor.moveToNext()) {
            String entry = cursor.getString(0) + ": " + // id
                    cursor.getString(1) + ", " +       // name
                    cursor.getString(2) + ", " +       // gender
                    cursor.getString(3) + ", " +       // subjects
                    cursor.getString(4) + ", " +       // class
                    cursor.getString(5);               // phone
            list.add(entry);
        }

        cursor.close();
        return list;
    }

    public void deleteStudent(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("students", "id=?", new String[]{id});
    }

    public void updateStudent(String id, String name, String gender, String subjects, String studentClass, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("gender", gender);
        cv.put("subjects", subjects);
        cv.put("studentClass", studentClass);
        cv.put("phone", phone);

        db.update("students", cv, "id=?", new String[]{id});
    }
}
