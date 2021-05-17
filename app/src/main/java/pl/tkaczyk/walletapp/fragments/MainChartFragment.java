package pl.tkaczyk.walletapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.model.Categories;
import pl.tkaczyk.walletapp.model.Expenses;

public class MainChartFragment extends Fragment {
    public static final String QUOTE_KEY = "quote";
    private static final String TAG = "MainChartFragment";
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Button cancelButton, addButton, dateButton;
    private EditText valueExpenseEditText, descriptionEditText;
    private Spinner spinner;
    private int day, month, year;
    private String date;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView tvDate;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_chart, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        PieChart mPieChart = getView().findViewById(R.id.chart);
        makeChart(mPieChart);


        ImageView imageView = getView().findViewById(R.id.imageViewMainFragmentAddExpense);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewDialog();
            }
        });

    }

    public void primaryCategories() {
        //Dodać sprawdzenie czy są dodane kategorie, jak nie to dodać, jak tak to nic
        Categories categories1 = new Categories(-1, "Jedzenie");
        Categories categories2 = new Categories(-1, "Transport");
        Categories categories3 = new Categories(-1, "Opłaty");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        dataBaseHelper.addOne(categories1);
        dataBaseHelper.addOne(categories2);
        dataBaseHelper.addOne(categories3);
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

        dateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month + 1;
                    date = dayOfMonth + ":" + month + ":" + year;
                    tvDate.setText(date);
                }
            }, year, month, day);
            datePickerDialog.show();
        });


        addButton.setOnClickListener(v ->
                insertData(date));

        cancelButton.setOnClickListener(v ->
                dialog.dismiss());
    }


    private void insertData(String date) {
        String valueOfExpense = valueExpenseEditText.getText().toString();
        String categoryOfExpense = spinner.getSelectedItem().toString();
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        String descriptionOfExpense = descriptionEditText.getText().toString();

        Expenses expenses;
        expenses = new Expenses(-1, valueOfExpense, categoryOfExpense, signInAccount.getEmail(), date, descriptionOfExpense);

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


    void makeChart(PieChart mPieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();


//        db.collection("expenses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d(TAG, "onComplete show document data: " + document.getData());
//                    }
//                }
//            }
//        });
//
//        DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("sampleData/inspiration");
//        ArrayList<PieEntry> data = new ArrayList<>();
//        mDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    Log.d(TAG, "onComplete: Good");
//                } else {
//                    Log.d(TAG, "onComplete: Fail");
//                }
//            }
//        });
//        mDocumentReference.get().addOnSuccessListener(documentSnapshot -> {
//
//            data.add(new PieEntry(20, documentSnapshot.getString(QUOTE_KEY)));
//
//            PieDataSet pieDataSet = new PieDataSet(data, "Wydatki");
//            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//            pieDataSet.setValueTextColor(Color.BLACK);
//            pieDataSet.setValueTextSize(16f);
//
//            PieData pieData = new PieData(pieDataSet);
//
//            mPieChart.setData(pieData);
//            mPieChart.getDescription().setEnabled(false);
//            mPieChart.setCenterText("Wydatki");
//            mPieChart.animate();
//        });
    }
}
