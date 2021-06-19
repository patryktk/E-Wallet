package pl.tkaczyk.walletapp.fragments;

import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Calendar;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.adapter.AdapterBudget;


public class BudgetFragment extends Fragment {
    BarChart mBarChart;
    String monthName;
    DataBaseHelper db;
    ArrayList<String> arrayListBudgetCategory, arrayListBudgetValue;
    TextView incomeValue;
    AdapterBudget mAdapterBudget;
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        mBarChart = (BarChart) view.findViewById(R.id.barChart);
        incomeValue = (TextView) view.findViewById(R.id.textViewBudgetIncomeValue);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewBudget);
        setupBarChart();
        setDataInChart();
        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //index of selected bar
                int x = mBarChart.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry)e);
                listOfExpenses(x, view);


            }

            @Override
            public void onNothingSelected() {

            }
        });

        return view;
    }

    private void listOfExpenses(int x, View view) {
        String monthOfBar = pickMonth(x + 1);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());

        incomeValue.setText(db.getSumOfIncomeByMonth2(monthOfBar,signInAccount.getEmail()).toString() + " zł");

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

        Cursor cursor = db.getExpensesByMonthChart(month,signInAccount.getEmail());
        while (cursor.moveToNext()){
            arrayListBudgetValue.add(cursor.getString(0) + " zł");
            arrayListBudgetCategory.add(cursor.getString(1) + " zł");
        }

    }

    private void setDataInChart() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        db = new DataBaseHelper(getContext());
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        Float currentMonth = Calendar.getInstance().get(Calendar.MONTH) +1f;

        for (int i = 1; i < 12; i++) {
            double d =  db.getSumOfIncomeByMonth2(pickMonth(i),signInAccount.getEmail());
            float f = (float) d;
            barEntries.add(new BarEntry(i, f));
        }
        barEntries.add(new BarEntry(13, 0));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Przychody");
        barDataSet.setColors(ColorTemplate.rgb("#9ACD32"));


        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
        for (int i = 1; i < 12; i++) {
            double d =  db.getSumOfExpenseByMonth2(pickMonth(i), signInAccount.getEmail());
            float f = (float) d;
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

    private void setupBarChart() {
        db = new DataBaseHelper(getContext());
        mBarChart.setXAxisRenderer(new CustomXAxisRender(mBarChart.getViewPortHandler(), mBarChart.getXAxis(), mBarChart.getTransformer(YAxis.AxisDependency.LEFT)));
        String[] labels = {"","Jan \n"+ moneyBallance("Styczeń") +"zł",
                "Feb \n" + moneyBallance("Luty") +"zł",
                "Mar \n" + moneyBallance("Marzec") +"zł",
                "Apr \n" + moneyBallance("Kwiecień") +"zł",
                "May \n" + moneyBallance("Maj") +"zł",
                "June \n" + moneyBallance("Czerwiec") +"zł",
                "Jul \n" + moneyBallance("Lipiec") +"zł",
                "Aug \n" + moneyBallance("Sierpień") +"zł",
                "Sept \n" + moneyBallance("Wrzesień") +"zł",
                "Oct \n" + moneyBallance("Październik") +"zł",
                "Nov \n" + moneyBallance("Listopad") +"zł",
                "Dec \n" + moneyBallance("Grudzień") +"zł"
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

    }
    public String moneyBallance(String month){
        String saldo;
        db = new DataBaseHelper(getContext());
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        double wydatki = db.getSumOfExpenseByMonth2(month,signInAccount.getEmail());
        double przychód =  db.getSumOfIncomeByMonth2(month, signInAccount.getEmail());
        double różnica = przychód  - wydatki;

        saldo = String.format("%.2f", różnica);
        if (wydatki > przychód) {
            saldo = "-" + saldo;
        }
        return saldo;

    }
    public class CustomXAxisRender extends XAxisRenderer{

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



}