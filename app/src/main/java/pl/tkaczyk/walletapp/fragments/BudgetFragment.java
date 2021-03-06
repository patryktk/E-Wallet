package pl.tkaczyk.walletapp.fragments;

import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.adapter.AdapterBudget;


public class BudgetFragment extends Fragment {
    BarChart mBarChart;
    String monthName, chooseYear = "";
    DataBaseHelper db;
    ArrayList<String> arrayListBudgetCategory, arrayListBudgetValue;
    TextView incomeValue, incomeName;
    AdapterBudget mAdapterBudget;
    RecyclerView mRecyclerView;
    AppCompatButton yearButton;
    int barPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        mBarChart = (BarChart) view.findViewById(R.id.barChart);
        incomeValue = (TextView) view.findViewById(R.id.textViewBudgetIncomeValue);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewBudget);

        yearButton = (AppCompatButton) view.findViewById(R.id.buttonBudgetYear);
        yearButton.setText(String.valueOf(currentYear));
        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickYear(view);
            }
        });
        setupBarChart();
        setDataInChart();
        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //index of selected bar
                barPosition = mBarChart.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry) e);
                listOfExpenses(barPosition, view);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        return view;
    }

    private void pickYear(View view) {
        final Calendar today = Calendar.getInstance();
        int miesiac = today.get(Calendar.MONTH) + 1;
        int rok = today.get(Calendar.YEAR);
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(view.getContext(),
                (selectedMonth, selectedYear) -> {
                    chooseYear = String.valueOf(selectedYear);
                    yearButton.setText(String.valueOf(selectedYear));

                    clearChart();

                    setupBarChart();
                    setDataInChart();

                    mBarChart.groupBars(1, 0.1f, 0.02f);
                    mBarChart.setVisibleXRange(0.2f,2.5f);
                    mBarChart.invalidate();
                    //od??wie??y?? wykres, zmieniaja si?? warto??ci, ale wygl??d wykresu ju?? nie ;-;
                }, rok, miesiac);
        builder.setActivatedYear(rok)
                .setTitle("Select year")
                .showYearOnly()
                .build().show();
    }

    private void listOfExpenses(int x, View view) {
        String monthOfBar = pickMonth(x + 1);
        incomeValue = view.findViewById(R.id.textViewBudgetIncomeValue);
        incomeName = view.findViewById(R.id.textViewBudgetIncomeName);
        incomeValue.setVisibility(View.VISIBLE);
        incomeName.setVisibility(View.VISIBLE);
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        if (chooseYear == "") {
            incomeValue.setText(db.getSumOfIncomeByMonth2(monthOfBar, signInAccount.getEmail(), currentYear).toString() + " z??");
        } else {
            incomeValue.setText(db.getSumOfIncomeByMonth2(monthOfBar, signInAccount.getEmail(), chooseYear).toString() + " z??");
        }

        arrayListBudgetCategory = new ArrayList<>();
        arrayListBudgetValue = new ArrayList<>();
        storeDataInArray(monthOfBar);

        mAdapterBudget = new AdapterBudget(getContext(), arrayListBudgetCategory, arrayListBudgetValue);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapterBudget);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    private void storeDataInArray(String month) {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        Cursor cursor;
        if (chooseYear == "") {
            cursor = db.getExpensesByMonthChart(month, signInAccount.getEmail(), currentYear);
        } else {
            cursor = db.getExpensesByMonthChart(month, signInAccount.getEmail(), chooseYear);

        }
        while (cursor.moveToNext()) {
            arrayListBudgetValue.add(cursor.getString(0) + " z??");
            arrayListBudgetCategory.add(cursor.getString(1));
        }

    }

    private void setDataInChart() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        db = new DataBaseHelper(getContext());
        Float currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1f;

        double d;

        if(chooseYear == ""){
            for(int i = 1; i < 12; i++){
                d = db.getSumOfIncomeByMonth2(pickMonth(i), signInAccount.getEmail(), currentYear);
                float f = (float) d;
                barEntries.add(new BarEntry(i, f));
            }
        }else{
            for(int i = 1; i < 12; i++){
                d = db.getSumOfIncomeByMonth2(pickMonth(i), signInAccount.getEmail(), chooseYear);
                float f = (float) d;
                barEntries.add(new BarEntry(i, f));
            }
        }

        barEntries.add(new BarEntry(13, 0));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Przychody");
        barDataSet.setColors(ColorTemplate.rgb("#9ACD32"));


        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        double x;
        for (int i = 1; i < 12; i++) {
            if (chooseYear == "") {
                x = db.getSumOfExpenseByMonth2(pickMonth(i), signInAccount.getEmail(), currentYear);
            } else {
                x = db.getSumOfExpenseByMonth2(pickMonth(i), signInAccount.getEmail(), chooseYear);
            }

            float f = (float) x;
            barEntries1.add(new BarEntry(i, f));
        }

        BarDataSet barDataSet1 = new BarDataSet(barEntries1, "Wydatki");
        barDataSet1.setColors(ColorTemplate.rgb("#DC143C"));

        BarData data = new BarData(barDataSet, barDataSet1);

        float groupSpace = 0.1f;
        float barSpace = 0.02f;
        float barWidth = 0.43f;
        mBarChart.setVisibleXRangeMaximum(0.2f);
        mBarChart.moveViewToX(currentMonth);
        mBarChart.setData(data);


        data.setBarWidth(barWidth);
        mBarChart.groupBars(1, groupSpace, barSpace);

    }
    private void clearChart(){
        mBarChart.clear();
        mBarChart.invalidate();
    }

    private void setupBarChart() {
        db = new DataBaseHelper(getContext());
        mBarChart.setXAxisRenderer(new CustomXAxisRender(mBarChart.getViewPortHandler(), mBarChart.getXAxis(), mBarChart.getTransformer(YAxis.AxisDependency.LEFT)));
        String[] labels = {"",
                "Sty \n" + moneyBallance("Stycze??") + "z??",
                "Lut \n" + moneyBallance("Luty") + "z??",
                "Mar \n" + moneyBallance("Marzec") + "z??",
                "Kwi \n" + moneyBallance("Kwiecie??") + "z??",
                "Maj \n" + moneyBallance("Maj") + "z??",
                "Cze \n" + moneyBallance("Czerwiec") + "z??",
                "Lip \n" + moneyBallance("Lipiec") + "z??",
                "Sie \n" + moneyBallance("Sierpie??") + "z??",
                "Wrz \n" + moneyBallance("Wrzesie??") + "z??",
                "Pa?? \n" + moneyBallance("Pa??dziernik") + "z??",
                "Lis \n" + moneyBallance("Listopad") + "z??",
                "Gru \n" + moneyBallance("Grudzie??") + "z??"
        };
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setGranularity(1); // only intervals of 1 day
        xAxis.setAxisMinimum(1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));


        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(true);
        mBarChart.setMaxVisibleValueCount(50);
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawGridBackground(true);
        mBarChart.getDescription().setEnabled(false);

    }

    public String moneyBallance(String month) {
        String saldo;
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        db = new DataBaseHelper(getContext());
        double wydatki;
        if (chooseYear == "") {
            wydatki = db.getSumOfExpenseByMonth2(month, signInAccount.getEmail(), currentYear);
        } else {
            wydatki = db.getSumOfExpenseByMonth2(month, signInAccount.getEmail(), chooseYear);
        }
        double przych??d;
        if (chooseYear == "") {
            przych??d = db.getSumOfIncomeByMonth2(month, signInAccount.getEmail(), currentYear);
        } else {
            przych??d = db.getSumOfIncomeByMonth2(month, signInAccount.getEmail(), chooseYear);
        }
        double r????nica = przych??d - wydatki;

        saldo = String.format("%.2f", r????nica);
        if (wydatki > przych??d) {
            saldo = "-" + saldo;
        }
        return saldo;

    }

    private String pickMonth(int month) {

        switch (month) {
            case 1:
                monthName = "Stycze??";
                break;
            case 2:
                monthName = "Luty";
                break;
            case 3:
                monthName = "Marzec";
                break;
            case 4:
                monthName = "Kwiecie??";
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
                monthName = "Sierpie??";
                break;
            case 9:
                monthName = "Wrzesie??";
                break;
            case 10:
                monthName = "Pa??dziernik";
                break;
            case 11:
                monthName = "Listopad";
                break;
            case 12:
                monthName = "Grudzie??";
                break;

        }
        return monthName;
    }

    public class CustomXAxisRender extends XAxisRenderer {

        public CustomXAxisRender(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y,
                                 MPPointF anchor, float angleDegrees) {
            String line[] = formattedLabel.split("\n");
            Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
            for (int i = 1; i < line.length; i++) { // we've already processed 1st line
                Utils.drawXAxisValue(c, line[i], x, y + mAxisLabelPaint.getTextSize() * i,
                        mAxisLabelPaint, anchor, angleDegrees);
            }
        }
    }

}