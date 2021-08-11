package pl.tkaczyk.walletapp.fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.DecimalDigitalFilter;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.ReminderBroadcast;
import pl.tkaczyk.walletapp.model.Categories;
import pl.tkaczyk.walletapp.model.Expenses;

public class MainChartFragment extends Fragment {

    DataBaseHelper db;
    String saldo, currentMonth;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Button cancelButton, addButton, dateButton;
    private AppCompatButton cancelButton1, addButton1, dateButton1;
    private EditText valueExpenseEditText, descriptionEditText;
    private Spinner spinner;
    private int day, month, year;
    private String date, monthName, chooseYear = "";
    private TextView tvDate, noData;
    private PieChart mPieChart;
    ImageView noDataImg;
    private FloatingActionButton floatingActionButton, floatingActionButton2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_chart, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        db = new DataBaseHelper(getContext());
        currentMonth = pickMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
        mPieChart = getView().findViewById(R.id.chart);
        noData = getView().findViewById(R.id.noDataMain);
        noDataImg = getView().findViewById(R.id.noDataMainImg);
        floatingActionButton = getView().findViewById(R.id.floatingActionButtonAddExpense);
        floatingActionButton2 = getView().findViewById(R.id.floatingActionButtonAddExpense2);
        primaryCategories();

        accountBalance();
        setup(currentMonth,saldo);


        floatingActionButton.setOnClickListener(view -> createNewDialog());
        floatingActionButton2.setOnClickListener(view -> createNewDialog());

        AppCompatButton appCompatButton = getView().findViewById(R.id.appButtonMainFragmentSwitchEI);
        appCompatButton.setOnClickListener(v ->{
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new IncomeFragment()).commit();
        });


    }
    void setup(String month, String saldo){
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());

        Cursor cursor;
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        cursor = db.getExpensesByMonthChart(month,signInAccount.getEmail(), currentYear);
        if(cursor.getCount()>0){
            floatingActionButton2.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            noDataImg.setVisibility(View.GONE);
            setupPieChart(saldo);
            makeChart(month);
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Pierwsze uruchomienie");
            alert.setMessage("Wykres będzie się zapełniał w miarę dodawania wydatków");
            alert.setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Miłego Korzystania z aplikacji!", Toast.LENGTH_SHORT).show();
                }
            });
            alert.show();
            floatingActionButton2.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            noDataImg.setVisibility(View.VISIBLE);
            mPieChart.setNoDataText("");
        }
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

    public void primaryCategories() {
        DataBaseHelper db = new DataBaseHelper(getContext());
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        List<String> categoriesList = db.getAllCategories(signInAccount.getEmail());
        if (categoriesList.isEmpty()) {
            Categories categories1 = new Categories(-1, "Jedzenie",signInAccount.getEmail());
            Categories categories2 = new Categories(-1, "Transport",signInAccount.getEmail());
            Categories categories3 = new Categories(-1, "Opłaty",signInAccount.getEmail());
            DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
            dataBaseHelper.addOne(categories1);
            dataBaseHelper.addOne(categories2);
            dataBaseHelper.addOne(categories3);
        }
    }

    public void createNewDialog() {
        dialogBuilder = new AlertDialog.Builder(getView().getContext());
        final View popupView = getLayoutInflater().inflate(R.layout.popup_expense_add, null);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        fillSpinner(popupView);

        cancelButton = (Button) popupView.findViewById(R.id.buttonPopupCancel);
        addButton = (Button) popupView.findViewById(R.id.buttonExpensePopupAdd);
        valueExpenseEditText = (EditText) popupView.findViewById(R.id.editTextValueOfExpense);
        spinner = (Spinner) popupView.findViewById(R.id.spinner);
        dateButton = (Button) popupView.findViewById(R.id.buttonPopupCalendar);
        tvDate = (TextView) popupView.findViewById(R.id.tvDate);
        descriptionEditText = (EditText) popupView.findViewById(R.id.editTextExpenseNameAdd);

        valueExpenseEditText.setFilters(new InputFilter[]{new DecimalDigitalFilter()});

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
                    chooseYear = String.valueOf(year);
                }
            }, year, month, day);
            datePickerDialog.show();
        });
        addButton.setOnClickListener(v -> {
            String value = valueExpenseEditText.getText().toString();
            String date = tvDate.getText().toString();
            if (TextUtils.isEmpty(value)) {
                valueExpenseEditText.setError("Nie może być puste");
            } else if (TextUtils.isEmpty(date)) {
                tvDate.setError("Nie może być puste");
            } else {
                insertData(date);
                accountBalance();
                setupPieChart(saldo);
                makeChart(currentMonth);
                dialog.dismiss();
                onStart();
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
        Double valueOfExpense = Double.parseDouble(valueExpenseEditText.getText().toString());
        String categoryOfExpense = spinner.getSelectedItem().toString();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        String descriptionOfExpense = descriptionEditText.getText().toString();

        Expenses expenses;
        expenses = new Expenses(-1,valueOfExpense, categoryOfExpense, signInAccount.getEmail(), date, descriptionOfExpense, monthName, chooseYear);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        boolean success = dataBaseHelper.addOne(expenses);
        if (success) {
            Toast.makeText(getContext(), "Pomyślnie dodano :", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Dodanie nie powiodło się", Toast.LENGTH_SHORT).show();
        }

    }

    public void fillSpinner(View view) {
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        DataBaseHelper db = new DataBaseHelper(getContext());
        List<String> categoriesList = db.getAllCategories(signInAccount.getEmail());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    void makeChart(String month) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));


        Cursor cursor = db.getExpensesByMonthChart(month, signInAccount.getEmail(), currentYear);
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                entries.add(new PieEntry(cursor.getInt(0), cursor.getString(1)));
            }
        } else {
            entries.add(new PieEntry(1, ""));
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

    void setupPieChart(String saldo1) {
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

}
