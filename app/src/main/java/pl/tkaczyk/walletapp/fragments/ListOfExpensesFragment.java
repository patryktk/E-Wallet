package pl.tkaczyk.walletapp.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.adapter.AdapterRVExpenses;


public class ListOfExpensesFragment extends Fragment {
    RecyclerView mRecyclerView;
    DataBaseHelper db;
    ArrayList<String> arrayListExpensesValue, arrayListExpenseDate, arrayListExpenseCategory, arrayListExpenseId, arrayListExpenseDescription, arrayListExpenseMonth;
    ArrayList<String> arrayListIncomeValue, arrayListIncomeDate, arrayListIncomeCategory;
    AdapterRVExpenses mAdapterRVExpenses;
    Button buttonExpenseMonthName;
    String monthName;
    TextView textViewFragmentExpenseListRed, textViewFragmentExpenseListGreen, noData;
    ImageView emptyImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        String currentMonth = pickMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
        listOfExpenses(getView(), currentMonth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_expenses, container, false);
        String currentMonth = pickMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
        db = new DataBaseHelper(getContext());


        textViewFragmentExpenseListRed = view.findViewById(R.id.textViewFragmentExpenseListRed);
        double sumOfExpense = db.getSumOfExpenseByMonth2(currentMonth);
        textViewFragmentExpenseListRed.setText(" - " + sumOfExpense + " zł ");
        textViewFragmentExpenseListGreen = view.findViewById(R.id.textViewFragmentExpenseListGreen);
        double sumOfIncome = db.getSumOfIncomeByMonth2(currentMonth);
        textViewFragmentExpenseListGreen.setText(" + " + sumOfIncome + " zł ");
        buttonExpenseMonthName = view.findViewById(R.id.buttonExpenseMonth);
        buttonExpenseMonthName.setText(currentMonth);
        mRecyclerView = view.findViewById(R.id.recyclerViewExpenses);
        emptyImage = view.findViewById(R.id.empty_imageview);
        noData = view.findViewById(R.id.noData);


        buttonExpenseMonthName.setOnClickListener(v -> {
            btnMonth(view);
        });
        listOfExpenses(view, currentMonth);


        return view;
    }

    public void listOfExpenses(View view, String month) {
        arrayListExpenseId = new ArrayList<>();
        arrayListExpenseCategory = new ArrayList<>();
        arrayListExpensesValue = new ArrayList<>();
        arrayListExpenseDate = new ArrayList<>();
        arrayListExpenseDescription = new ArrayList<>();
        arrayListExpenseMonth = new ArrayList<>();
        storeDataInArrays(month);

        arrayListIncomeCategory = new ArrayList<>();
        arrayListIncomeDate = new ArrayList<>();
        arrayListIncomeValue = new ArrayList<>();
        storeIncomeDataInArray(month);


        mAdapterRVExpenses = new AdapterRVExpenses(getContext(), arrayListExpenseId, arrayListExpensesValue, arrayListExpenseDate, arrayListExpenseCategory, arrayListExpenseDescription, arrayListExpenseMonth);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapterRVExpenses);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        textViewFragmentExpenseListRed.setText(" - " + db.getSumOfExpenseByMonth(month) + " zł ");

    }


    public void btnMonth(View view) {
        final Calendar today = Calendar.getInstance();
        int miesiac = today.get(Calendar.MONTH) + 1;
        int rok = today.get(Calendar.YEAR);
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(view.getContext(),
                (selectedMonth, selectedYear) -> {
                    buttonExpenseMonthName.setText(pickMonth(selectedMonth + 1));
                    listOfExpenses(view, pickMonth(selectedMonth + 1));
                }, rok, miesiac);
        builder.setActivatedMonth(miesiac - 1)
                .setTitle("Select month")
                .showMonthOnly()
                .build().show();

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


    private void storeDataInArrays(String month) {
        Cursor cursor = db.getExpensesByMonth(month);
        if (cursor.getCount() == 0) {
            emptyImage.setVisibility(View.VISIBLE);
            noData.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                arrayListExpenseId.add(cursor.getString(0));
                arrayListExpensesValue.add(cursor.getString(1));
                arrayListExpenseCategory.add(cursor.getString(2));
                arrayListExpenseDate.add(cursor.getString(4));
                arrayListExpenseDescription.add(cursor.getString(5));
                arrayListExpenseMonth.add(cursor.getString(6));
            }
            emptyImage.setVisibility(View.GONE);
            noData.setVisibility(View.GONE);
        }
    }

    private void storeIncomeDataInArray(String month) {
        Cursor cursor = db.getIncomeByMonth(month);
        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "NO DATA!", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                arrayListIncomeValue.add("+ " + cursor.getString(1) + " zł");
                arrayListIncomeDate.add(cursor.getString(2));
                arrayListIncomeCategory.add("Przychód");
            }
        }
    }

}