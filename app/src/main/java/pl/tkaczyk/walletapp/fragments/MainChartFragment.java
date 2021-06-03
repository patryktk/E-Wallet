package pl.tkaczyk.walletapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
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
import java.util.List;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.DecimalDigitalFilter;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.model.Categories;
import pl.tkaczyk.walletapp.model.Expenses;

public class MainChartFragment extends Fragment {

    DataBaseHelper db;
    String saldo, currentMonth;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Button cancelButton, addButton, dateButton;
    private EditText valueExpenseEditText, descriptionEditText;
    private Spinner spinner;
    private int day, month, year;
    private String date, monthName;
    private TextView tvDate;
    private PieChart mPieChart;

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
        primaryCategories();

        accountBalance();
        setupPieChart(saldo);
        makeChart(currentMonth);

        ImageView imageView = getView().findViewById(R.id.imageViewMainFragmentAddExpense);
        imageView.setOnClickListener(view -> createNewDialog());

        Button buttonSwitch = getView().findViewById(R.id.buttonMainFragmentSwitchEI);
        buttonSwitch.setOnClickListener(v -> {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new IncomeFragment()).commit();
        });

    }

    private void accountBalance() {
        db = new DataBaseHelper(getContext());

//        String x = db.getAllSumOfIncome();
//        if(x == null){
//            x = "0";
//        }
//        Double plus = Double.valueOf(x);
        Double plus = db.getAllSumOfIncome2();
//        String y = db.getAllSumOfExpenses();
//        if(y == null){
//            y = "0";
//        }
//        Double minus = Double.valueOf(y);
        Double minus = db.getAllSumOfExpenses2();

        Double saldo1 = plus - minus;
//        String.format("%.2f", saldo1);
//        System.out.format("%,.2f%n",saldo1);
//        saldo = saldo1.toString();
        saldo = String.format("%.2f", saldo1);
        if (minus > plus) {
            saldo = "-" + saldo;
        }

    }

    public void primaryCategories() {
        DataBaseHelper db = new DataBaseHelper(getContext());
        List<String> categoriesList = db.getAllCategories();
        if (categoriesList.isEmpty()) {
            Categories categories1 = new Categories(-1, "Jedzenie");
            Categories categories2 = new Categories(-1, "Transport");
            Categories categories3 = new Categories(-1, "Opłaty");
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
        expenses = new Expenses(-1, valueOfExpense, categoryOfExpense, signInAccount.getEmail(), date, descriptionOfExpense, monthName);

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
        DataBaseHelper db = new DataBaseHelper(getContext());
        List<String> categoriesList = db.getAllCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    void makeChart(String month) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        Cursor cursor = db.getExpensesByMonthChart(month);
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToNext();
                entries.add(new PieEntry(cursor.getInt(0), cursor.getString(1)));
            }
        } else {
                entries.add(new PieEntry(1, ""));
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Pierwsze uruchomienie");
            alert.setMessage("Wykres będzie się zapełniał w miarę dodawania wydatków");
            alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Miłego Korzystania z aplikacji!", Toast.LENGTH_SHORT).show();
                }
            });
            alert.show();
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
