package pl.tkaczyk.walletapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import pl.tkaczyk.walletapp.model.Categories;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(@Nullable Context context) {
        super(context, "wallet.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String createTableStatement = "CREATE TABLE EXPENSES_TABLE (ID INTEGER PRIMARY KEY AUTOINCREMENT, DESCRIPTION VARCHAR,FOREIGN KEY(categories_id) REFERENCES categories_table(id), USER VARCHAR, DATA VARCHAR)";
        String createSecondTableStatement = "CREATE TABLE CATEGORIES_TABLE (ID INTEGER primary KEY AUTOINCREMENT, name varchar)";

//        db.execSQL(createTableStatement);
        db.execSQL(createSecondTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(Categories categories){
         SQLiteDatabase db = this.getWritableDatabase();
        return true;
    }
}
