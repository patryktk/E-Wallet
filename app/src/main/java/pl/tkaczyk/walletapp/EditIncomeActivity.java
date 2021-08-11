package pl.tkaczyk.walletapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Calendar;

public class EditIncomeActivity extends AppCompatActivity {

    EditText editTextValue, editTextDescription;
    TextView tvDate;
    AppCompatButton updateButton, deleteButton, dateButton;
    DataBaseHelper db;

    String id, category, date, description, month;
    Double value;
    String calendarDate, monthName;
    private int calendarDay, calendarMonth, calendarYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_income);

        editTextValue = findViewById(R.id.editTextEditIncomeValue);
        editTextDescription = findViewById(R.id.editTextEditIncomeDescription);
        tvDate = findViewById(R.id.tvEditIncomeDate);
        updateButton = findViewById(R.id.buttonEditIncomeEdit);
        deleteButton = findViewById(R.id.buttonEditIncomeDelete);
        dateButton = findViewById(R.id.buttonEditIncomeDate);

        getAndSetIntentData();
        db = new DataBaseHelper(this);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                calendarDay = calendar.get(Calendar.DAY_OF_MONTH);
                calendarMonth = calendar.get(Calendar.MONTH);
                calendarYear = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditIncomeActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                date = tvDate.getText().toString();
                description = editTextDescription.getText().toString();
                db.updateIncome(id, value, date, description, monthName);
            }
        });
        deleteButton.setOnClickListener(V ->{
            confirmDeleteDialog();
        });
        Toolbar toolbar = findViewById(R.id.toolbarIncome);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(v ->
                onBackPressed());
    }
    void confirmDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usunąć przychód?");
        builder.setMessage("Jesteś pewny że chcesz usunać ten przychód?");
        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deleteOneIncome(id);
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
        if (getIntent().hasExtra("Id") && getIntent().hasExtra("Value")  && getIntent().hasExtra("Date") && getIntent().hasExtra("Description") && getIntent().hasExtra("Month")) {
            id = getIntent().getStringExtra("Id");
            value = Double.valueOf(getIntent().getStringExtra("Value"));
            date = getIntent().getStringExtra("Date");
            description = getIntent().getStringExtra("Description");
            monthName = getIntent().getStringExtra("Month");

            editTextValue.setText(value.toString());
            editTextDescription.setText(description);
            tvDate.setText(date);

        } else {
            Toast.makeText(this, "NO DATA!", Toast.LENGTH_SHORT).show();
        }
    }
}