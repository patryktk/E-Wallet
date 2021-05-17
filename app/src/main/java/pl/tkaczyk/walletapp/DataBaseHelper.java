package pl.tkaczyk.walletapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pl.tkaczyk.walletapp.model.Categories;
import pl.tkaczyk.walletapp.model.Expenses;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String categoriesTable = "CATEGORIES_TABLE";
    public static final String categoriesName = "NAME";
    public static final String expensesTable = "EXPENSES_TABLE";
    public static final String expensesTableValue = "VALUE";
    public static final String expensesTableUserMail = "USER";
    public static final String expensesTableDate = "DATE";
    public static final String expensesTableDescription = "DESCRIPTION";
    public static final String expensesTableCategoryName = "CATEGORY";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "wallet.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + expensesTable + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " " + expensesTableValue + " VARCHAR ," +
                " " + expensesTableCategoryName + " varchar," +
                " " + expensesTableUserMail + " varchar," +
                " " + expensesTableDate + " varchar," +
                " " + expensesTableDescription + " varchar)";
        String createSecondTableStatement = "CREATE TABLE " + categoriesTable + " (ID INTEGER primary KEY AUTOINCREMENT, " + categoriesName + " varchar)";

        db.execSQL(createTableStatement);
        db.execSQL(createSecondTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean removeOne(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + categoriesTable +" WHERE " + categoriesName + " = " + "'" + name + "'" ;

        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }

    public boolean addOne(Categories categories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(categoriesName, categories.getName());

        long insert = db.insert(categoriesTable, null, cv);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addOne(Expenses expenses) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(expensesTableValue, expenses.getValue());
        cv.put(expensesTableCategoryName, expenses.getCategory());
        cv.put(expensesTableUserMail, expenses.getUserMail());
        cv.put(expensesTableDate, expenses.getDate());
        cv.put(expensesTableDescription, expenses.getDescription());


        long insert = db.insert(expensesTable, null, cv);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<String> getAllCategories(){
        List<String> categoriesList = new ArrayList<>();

        String queryString = "SELECT * FROM " + categoriesTable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do {
                categoriesList.add(cursor.getString(1));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoriesList;
    }

    public List<Expenses> getCurrentUserExpenses(String user){
        List<Expenses> expensesList = new ArrayList<>();

        String queryString = "select * from " + expensesTable + " where " + expensesTableUserMail + " = " + user;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()){
            do{
               String value = cursor.getString(1);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return expensesList;
    }
    public Cursor getCategories(){
        String query = "SELECT * FROM " + categoriesTable;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }
}
