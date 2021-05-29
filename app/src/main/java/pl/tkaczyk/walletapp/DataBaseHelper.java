package pl.tkaczyk.walletapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import pl.tkaczyk.walletapp.model.Categories;
import pl.tkaczyk.walletapp.model.Expenses;
import pl.tkaczyk.walletapp.model.Income;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String categoriesTable = "CATEGORIES_TABLE";
    public static final String categoriesName = "NAME";

    public static final String expensesTable = "EXPENSES_TABLE";
    public static final String expensesTableCategoryName = "CATEGORY";

    public static final String incomeTable = "INCOME_TABLE";

    public static final String tableValue = "VALUE";
    public static final String tableUser = "USER";
    public static final String tableDate = "DATE";
    public static final String tableDescription = "DESCRIPTION";
    public static final String tableMonth = "MONTH";


    public DataBaseHelper(@Nullable Context context) {
        super(context, "wallet.db", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableExpenses = "CREATE TABLE " + expensesTable + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " " + tableValue + " VARCHAR ," +
                " " + expensesTableCategoryName + " varchar," +
                " " + tableUser + " varchar," +
                " " + tableDate + " varchar," +
                " " + tableDescription + " varchar, " +
                " " + tableMonth + " varchar)";
        String createTableCategories = "CREATE TABLE " + categoriesTable + " (ID INTEGER primary KEY AUTOINCREMENT, " + categoriesName + " varchar)";
        String createTableIncome = "CREATE TABLE " + incomeTable + " (ID INTEGER primary KEY AUTOINCREMENT, " + tableValue + " varchar , " + tableDate + " varchar, " + tableDescription + " varchar, " + tableMonth + " varchar)";

        db.execSQL(createTableExpenses);
        db.execSQL(createTableCategories);
        db.execSQL(createTableIncome);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + expensesTable);
        db.execSQL("DROP TABLE IF EXISTS " + categoriesTable);
        db.execSQL("DROP TABLE IF EXISTS " + incomeTable);

        onCreate(db);
    }

    public boolean removeOne(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + categoriesTable + " WHERE " + categoriesName + " = " + "'" + name + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
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

    public boolean addOne(Income income) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(tableValue, income.getValue());
        cv.put(tableDate, income.getData());
        cv.put(tableDescription, income.getDescription());
        cv.put(tableMonth, income.getMonth());

        long insert = db.insert(incomeTable, null, cv);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addOne(Expenses expenses) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(tableValue, expenses.getValue());
        cv.put(expensesTableCategoryName, expenses.getCategory());
        cv.put(tableUser, expenses.getUserMail());
        cv.put(tableDate, expenses.getDate());
        cv.put(tableDescription, expenses.getDescription());
        cv.put(tableMonth, expenses.getMonth());


        long insert = db.insert(expensesTable, null, cv);

        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public List<String> getAllCategories() {
        List<String> categoriesList = new ArrayList<>();

        String queryString = "SELECT * FROM " + categoriesTable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            do {
                categoriesList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoriesList;
    }


    public String getSumOfExpenseByMonth(String month) {
        String query = "SELECT sum(" + tableValue + ") from " + expensesTable + " where " + tableMonth + "=" + "'" + month + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = null;
        String money = "";
        if (db != null) {
            cursor1 = db.rawQuery(query, null);
            if (cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                money = cursor1.getString(0);
            }
        }
        return money;
    }
    public String getAllSumOfExpenses() {
        String query = "SELECT sum(" + tableValue + ") from " + expensesTable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = null;
        String money = "";
        if (db != null) {
            cursor1 = db.rawQuery(query, null);
            if (cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                money = cursor1.getString(0);
            }
        }
        return money;
    }
    public String getSumOfIncomeByMonth(String month) {
        String query = "SELECT sum(" + tableValue + ") from " + incomeTable + " where " + tableMonth + "=" + "'" + month + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = null;
        String money = "";
        if (db != null) {
            cursor1 = db.rawQuery(query, null);
            if (cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                money = cursor1.getString(0);
            }
        }
        return money;
    }
    public String getAllSumOfIncome() {
        String query = "SELECT sum(" + tableValue + ") from " + incomeTable;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = null;
        String money = "";
        if (db != null) {
            cursor1 = db.rawQuery(query, null);
            if (cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                money = cursor1.getString(0);
            }
        }
        return money;
    }

    public Cursor getExpensesByMonthChart(String month) {
        String query = "SELECT sum("+tableValue+"), "+expensesTableCategoryName+" from " + expensesTable + " WHERE " + tableMonth + " = " + "'" + month + "' group by "+expensesTableCategoryName+"";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor getExpensesByMonth(String month) {
        String query = "SELECT * from " + expensesTable + " WHERE " + tableMonth + " = " + "'" + month + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor getIncomeByMonth(String month) {
        String query = "SELECT * from " + incomeTable + " WHERE " + tableMonth + " = " + "'" + month + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor getCategories() {
        String query = "SELECT * FROM " + categoriesTable;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

}
