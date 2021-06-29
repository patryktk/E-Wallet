package pl.tkaczyk.walletapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.ArrayList;
import java.util.Calendar;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.model.Income;

public class IncomeFragment extends Fragment {

    DataBaseHelper db;
    String saldo, currentMonth;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Button cancelButton, addButton, dateButton;
    private EditText valueIncomeEditText, descriptionEditText;
    private int day, month, year;
    private String date, monthName;
    private TextView tvDate;
    private PieChart mPieChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        db = new DataBaseHelper(getContext());
        currentMonth = pickMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
        mPieChart = getView().findViewById(R.id.chart_income);

        accountBalance();
        setupPieChart(saldo);
        makeChart(currentMonth);


        Button switchButton = getView().findViewById(R.id.buttonIncomeSwitchEI2);
        switchButton.setOnClickListener(v -> {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainChartFragment()).commit();
        });

        ImageView imageView = getView().findViewById(R.id.imageViewIncomeAddExpense);
        imageView.setOnClickListener(view -> createNewDialog());
    }

    private void accountBalance() {
        db = new DataBaseHelper(getContext());
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        Double plus = db.getAllSumOfIncome2(signInAccount.getEmail());
        Double minus = db.getAllSumOfExpenses2(signInAccount.getEmail());
        Double saldo1 = plus - minus;

        saldo = String.format("%.2f", saldo1);
        if (minus > plus) {
            saldo = "-" + saldo;
        }

    }

    private void makeChart(String month) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        Double sumOfExpense = db.getSumOfExpenseByMonth2(month,signInAccount.getEmail(), currentYear);
        Double sumOfIncome = db.getSumOfIncomeByMonth2(month, signInAccount.getEmail(), currentYear);
        if(sumOfExpense == 0 || sumOfIncome  == 0){
            entries.add(new PieEntry(1, ""));
        }else{
            entries.add(new PieEntry(sumOfExpense.intValue(), "Wydatki"));
            entries.add(new PieEntry(sumOfIncome.intValue(), "Przychód"));
        }
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        mPieChart.setData(data);
        mPieChart.invalidate();
    }

    private void setupPieChart(String saldo1) {
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setUsePercentValues(true);
        mPieChart.setEntryLabelTextSize(12);
        mPieChart.setEntryLabelColor(Color.BLACK);
        mPieChart.setCenterText(saldo1 + " zł");
        mPieChart.setCenterTextSize(24);
        mPieChart.getDescription().setEnabled(false);

        Legend l = mPieChart.getLegend();
        l.setEnabled(false);
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
                accountBalance();
                setupPieChart(saldo);
                makeChart(currentMonth);
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

    private void insertData(String date) {
        Double valueOfIncome = Double.valueOf(valueIncomeEditText.getText().toString());
        String descriptionOfIncome = descriptionEditText.getText().toString();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));



        Income income;
        income = new Income(-1, valueOfIncome,date, descriptionOfIncome, monthName,signInAccount.getEmail(),year);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        boolean success = dataBaseHelper.addOne(income);
        if (success) {
            Toast.makeText(getContext(), "Pomyślnie dodano", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Dodanie nie powiodło się", Toast.LENGTH_SHORT).show();
        }

    }

}
