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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
    String monthName, chooseYear = "";
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
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        db = new DataBaseHelper(getContext());


        textViewFragmentExpenseListRed = view.findViewById(R.id.textViewFragmentExpenseListRed);
        double sumOfExpense = db.getSumOfExpenseByMonth2(currentMonth,signInAccount.getEmail(), currentYear);

        textViewFragmentExpenseListRed.setText(" - " + sumOfExpense + " zł ");
        textViewFragmentExpenseListGreen = view.findViewById(R.id.textViewFragmentExpenseListGreen);
        double sumOfIncome = db.getSumOfIncomeByMonth2(currentMonth, signInAccount.getEmail(), currentYear);
        textViewFragmentExpenseListGreen.setText(" + " + sumOfIncome + " zł ");
        buttonExpenseMonthName = view.findViewById(R.id.buttonExpenseMonth);
        buttonExpenseMonthName.setText(currentMonth + " " + currentYear);
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
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
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

        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        if(chooseYear == ""){
            textViewFragmentExpenseListRed.setText(" - " + db.getSumOfExpenseByMonth(month, signInAccount.getEmail(), currentYear) + " zł ");
        }else{
            textViewFragmentExpenseListRed.setText(" - " + db.getSumOfExpenseByMonth(month, signInAccount.getEmail(), chooseYear) + " zł ");
        }


    }


    public void btnMonth(View view) {
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        final Calendar today = Calendar.getInstance();
        int miesiac = today.get(Calendar.MONTH) + 1;
        int rok = today.get(Calendar.YEAR);
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(view.getContext(),
                (selectedMonth, selectedYear) -> {
                    buttonExpenseMonthName.setText(pickMonth(selectedMonth + 1) + " " + selectedYear);

                    chooseYear = String.valueOf(selectedYear);
                    double sumOfExpense = db.getSumOfExpenseByMonth2(pickMonth(selectedMonth + 1),signInAccount.getEmail(), String.valueOf(selectedYear));
                    textViewFragmentExpenseListRed.setText(" - " + sumOfExpense + " zł ");

                    double sumOfIncome = db.getSumOfIncomeByMonth2(pickMonth(selectedMonth + 1), signInAccount.getEmail(), String.valueOf(selectedYear));
                    textViewFragmentExpenseListGreen.setText(" + " + sumOfIncome + " zł ");

                    listOfExpenses(view, pickMonth(selectedMonth + 1));
                }, rok, miesiac);
        builder.setActivatedMonth(miesiac - 1)
                .setTitle("Select month and year")
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
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        Cursor cursor;
        if(chooseYear == ""){
            cursor = db.getExpensesByMonth(month, signInAccount.getEmail(), currentYear);
        }else{
            cursor = db.getExpensesByMonth(month, signInAccount.getEmail(), chooseYear);
        }
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
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        Cursor cursor;
        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        if(chooseYear == ""){
            cursor = db.getIncomeByMonth(month, signInAccount.getEmail(), currentYear);
        }else{
            cursor = db.getIncomeByMonth(month, signInAccount.getEmail(), chooseYear);
        }
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