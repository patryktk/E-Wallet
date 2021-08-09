package pl.tkaczyk.walletapp.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.adapter.AdapterRVExpenses;


public class ListOfExpensesFragment extends Fragment {
    RecyclerView mRecyclerView;
    DataBaseHelper db;
    ArrayList<String> arrayListExpensesValue, arrayListExpenseDate, arrayListExpenseCategory, arrayListExpenseId, arrayListExpenseDescription, arrayListExpenseMonth, arrayListMark;
    AdapterRVExpenses mAdapterRVExpenses;
    AppCompatButton buttonExpenseMonthName;
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

        double sumOfIncome = db.getSumOfIncomeByMonth2(currentMonth, signInAccount.getEmail(), currentYear);
        double sumOfExpense = db.getSumOfExpenseByMonth2(currentMonth, signInAccount.getEmail(), currentYear);

        textViewFragmentExpenseListRed = view.findViewById(R.id.textViewFragmentExpenseListRed);
        textViewFragmentExpenseListGreen = view.findViewById(R.id.textViewFragmentExpenseListGreen);

        if (sumOfExpense == 0.0) {
            textViewFragmentExpenseListRed.setVisibility(View.GONE);
        } else {
            textViewFragmentExpenseListRed.setVisibility(View.VISIBLE);
            textViewFragmentExpenseListRed.setText(" - " + sumOfExpense + " zł ");
        }
        if (sumOfIncome == 0.0) {
            textViewFragmentExpenseListGreen.setVisibility(View.GONE);
        } else {
            textViewFragmentExpenseListGreen.setVisibility(View.VISIBLE);
            textViewFragmentExpenseListGreen.setText(" + " + sumOfIncome + " zł ");
        }
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
        arrayListMark = new ArrayList<>();
        storeDataInArrays(month);


        mAdapterRVExpenses = new AdapterRVExpenses(getContext(), arrayListExpenseId, arrayListExpensesValue, arrayListExpenseDate, arrayListExpenseCategory, arrayListExpenseDescription, arrayListExpenseMonth, arrayListMark);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapterRVExpenses);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        if (chooseYear == "") {
            textViewFragmentExpenseListRed.setText(" - " + db.getSumOfExpenseByMonth2(month, signInAccount.getEmail(), currentYear) + " zł ");
        } else {
            textViewFragmentExpenseListRed.setText(" - " + db.getSumOfExpenseByMonth2(month, signInAccount.getEmail(), chooseYear) + " zł ");
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
                    double sumOfExpense = db.getSumOfExpenseByMonth2(pickMonth(selectedMonth + 1), signInAccount.getEmail(), String.valueOf(selectedYear));
                    if (sumOfExpense == 0.0) {
                        textViewFragmentExpenseListRed.setVisibility(View.GONE);
                    } else {
                        textViewFragmentExpenseListRed.setVisibility(View.VISIBLE);
                        textViewFragmentExpenseListRed.setText(" - " + sumOfExpense + " zł ");
                    }
                    double sumOfIncome = db.getSumOfIncomeByMonth2(pickMonth(selectedMonth + 1), signInAccount.getEmail(), String.valueOf(selectedYear));
                    if (sumOfIncome == 0.0) {
                        textViewFragmentExpenseListGreen.setVisibility(View.GONE);
                    } else {
                        textViewFragmentExpenseListGreen.setVisibility(View.VISIBLE);
                        textViewFragmentExpenseListGreen.setText(" + " + sumOfIncome + " zł ");
                    }

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
        Cursor cursorIncome;
        Cursor cursorExpense;
        if (chooseYear == "") {
            cursorExpense = db.getExpensesByMonth(month, signInAccount.getEmail(), currentYear);
            cursorIncome = db.getIncomeByMonth(month, signInAccount.getEmail(), currentYear);
        } else {
            cursorExpense = db.getExpensesByMonth(month, signInAccount.getEmail(), chooseYear);
            cursorIncome = db.getIncomeByMonth(month, signInAccount.getEmail(), chooseYear);
        }
        if (cursorExpense.getCount() == 0 && cursorIncome.getCount() == 0) {
            emptyImage.setVisibility(View.VISIBLE);
            noData.setVisibility(View.VISIBLE);
        } else {
            while (cursorExpense.moveToNext()) {
                arrayListExpenseId.add(cursorExpense.getString(0));
                arrayListExpensesValue.add(cursorExpense.getString(1));

                //Dalej jest 0.0 idk
//                String x = cursorExpense.getString(1);
//                Double y = Double.valueOf(x);
//                DecimalFormat decim = new DecimalFormat("0.00");
//                Double yy = Double.parseDouble(decim.format(y));
//                String xx = String.valueOf(yy);
//                arrayListExpensesValue.add(xx);

                arrayListExpenseCategory.add(cursorExpense.getString(2));
                arrayListExpenseDate.add(cursorExpense.getString(4));
                arrayListExpenseDescription.add(cursorExpense.getString(5));
                arrayListExpenseMonth.add(cursorExpense.getString(6));
                arrayListMark.add("-");
            }
            while (cursorIncome.moveToNext()) {
                arrayListExpenseId.add(cursorIncome.getString(0));
                arrayListExpensesValue.add(cursorIncome.getString(1));
                arrayListExpenseCategory.add("Przychód");
                arrayListExpenseDate.add(cursorIncome.getString(2));
                arrayListExpenseDescription.add(cursorIncome.getString(3));
                arrayListExpenseMonth.add(cursorIncome.getString(4));
                arrayListMark.add("+");
            }
            emptyImage.setVisibility(View.GONE);
            noData.setVisibility(View.GONE);
        }
    }


}