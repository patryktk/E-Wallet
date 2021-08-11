package pl.tkaczyk.walletapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Calendar;
import java.util.List;

public class EditExpenseActivity extends AppCompatActivity {

    EditText editTextValue, editTextDescription;
    TextView tvDate;
    Button updateButton, deleteButton, dateButton;
    Spinner spinner;
    DataBaseHelper db;

    String id, category, date, description, month;
    Double value;
    String calendarDate, monthName;
    private int calendarDay, calendarMonth, calendarYear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        editTextValue = findViewById(R.id.editTextEditExpenseValue);
        editTextDescription = findViewById(R.id.editTextEditExpenseDescription);
        tvDate = findViewById(R.id.tvEditExpenseDate);
        updateButton = findViewById(R.id.buttonEditExpenseEdit);
        deleteButton = findViewById(R.id.buttonEditExpenseDelete);
        dateButton = findViewById(R.id.buttonEditExpenseDate);
        spinner = findViewById(R.id.spinnerEditExpense);
        fillSpinner();


        getAndSetIntentData();
        db = new DataBaseHelper(this);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                calendarDay = calendar.get(Calendar.DAY_OF_MONTH);
                calendarMonth = calendar.get(Calendar.MONTH);
                calendarYear = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        calendarDate = dayOfMonth + "/" + month + "/" + year;
                        tvDate.setText(calendarDate);
                        monthName = pickMonth(month);
                    }
                }, calendarYear, calendarMonth, calendarDay);
                datePickerDialog.show();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                value = Double.valueOf(editTextValue.getText().toString());
                category = spinner.getSelectedItem().toString();
                date = tvDate.getText().toString();
                description = editTextDescription.getText().toString();
                db.updateExpense(id, value, category, date, description, monthName);
            }
        });
        deleteButton.setOnClickListener(V ->{
            confirmDeleteDialog();
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());
    }
    void confirmDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usunąc wydatek?");
        builder.setMessage("Jesteś pewny że chcesz usunać ten wydatek?");
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteOneExpense(id);
                finish();
            }
        });
        builder.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private String pickMonth(int month) {
        switch (month) {
            case 1:
                monthName = "Styczeń";
                break;
            case 2:
                monthName = "Luty";
                break;
            case 3:
                monthName = "Marzec";
                break;
            case 4:
                monthName = "Kwiecień";
                break;
            case 5:
                monthName = "Maj";
                break;
            case 6:
                monthName = "Czerwiec";
                break;
            case 7:
                monthName = "Lipiec";
                break;
            case 8:
                monthName = "Sierpień";
                break;
            case 9:
                monthName = "Wrzesień";
                break;
            case 10:
                monthName = "Październik";
                break;
            case 11:
                monthName = "Listopad";
                break;
            case 12:
                monthName = "Grudzień";
                break;

        }
        return monthName;
    }

    void getAndSetIntentData() {
        db = new DataBaseHelper(this);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (getIntent().hasExtra("Id") && getIntent().hasExtra("Value") && getIntent().hasExtra("Category") && getIntent().hasExtra("Date") && getIntent().hasExtra("Description") && getIntent().hasExtra("Month")) {
            id = getIntent().getStringExtra("Id");
            value = Double.valueOf(getIntent().getStringExtra("Value"));
            category = getIntent().getStringExtra("Category");
            date = getIntent().getStringExtra("Date");
            description = getIntent().getStringExtra("Description");
            monthName = getIntent().getStringExtra("Month");

            editTextValue.setText(value.toString());
            editTextDescription.setText(description);
            tvDate.setText(date);
            int categoryId = db.getIdCategoriesByName(category, signInAccount.getEmail()) - 1;
            spinner.setSelection(categoryId);

        } else {
            Toast.makeText(this, "NO DATA!", Toast.LENGTH_SHORT).show();
        }
    }

    void fillSpinner() {
        DataBaseHelper db = new DataBaseHelper(this);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        List<String> categoriesList = db.getAllCategories(signInAccount.getEmail());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}