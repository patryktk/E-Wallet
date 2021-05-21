package pl.tkaczyk.walletapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Calendar;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.model.Income;

public class IncomeFragment extends Fragment {

    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Button cancelButton, addButton, dateButton;
    private EditText valueIncomeEditText, descriptionEditText;
    private int day, month, year;
    private String date, monthName;
    private TextView tvDate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Button switchButton = getView().findViewById(R.id.buttonIncomeSwitchEI2);
        switchButton.setOnClickListener(v -> {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainChartFragment()).commit();
        });

        ImageView imageView = getView().findViewById(R.id.imageViewIncomeAddExpense);
        imageView.setOnClickListener(view -> createNewDialog());
    }

    private void createNewDialog() {
        dialogBuilder = new AlertDialog.Builder(getView().getContext());
        final View popupView = getLayoutInflater().inflate(R.layout.popup_income_add, null);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        cancelButton = (Button) popupView.findViewById(R.id.buttonIncomePopupCancel);
        addButton = (Button) popupView.findViewById(R.id.buttonIncomePopupAdd);
        dateButton = (Button) popupView.findViewById(R.id.buttonIncomePopupCalendar);
        valueIncomeEditText = (EditText) popupView.findViewById(R.id.editTextValueOfIncome);
        descriptionEditText = (EditText) popupView.findViewById(R.id.editTextIncomeNameAdd);
        tvDate = (TextView) popupView.findViewById(R.id.tvIncomeDate);

        dateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    date = dayOfMonth + "/" + month + "/" + year;
                    tvDate.setText(date);
                    monthName = pickMonth(month);
                }
            }, year, month, day);
            datePickerDialog.show();
        });
        addButton.setOnClickListener(v -> {
            String value = valueIncomeEditText.getText().toString();
            String date = tvDate.getText().toString();
            if (TextUtils.isEmpty(value)) {
                valueIncomeEditText.setError("Nie może być puste");
            } else if (TextUtils.isEmpty(date)) {
                tvDate.setError("Nie może być puste");
            } else {
                insertData(date);
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v ->
                dialog.dismiss());

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

    private void insertData(String date){
        String valueOfIncome = valueIncomeEditText.getText().toString();
        String descriptionOfIncome = descriptionEditText.getText().toString();

        Income income;
        income = new Income(-1,valueOfIncome, date, descriptionOfIncome, monthName);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        boolean success = dataBaseHelper.addOne(income);
        if(success){
            Toast.makeText(getContext(), "Pomyślnie dodano", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Dodanie nie powiodło się", Toast.LENGTH_SHORT).show();
        }

    }

}
