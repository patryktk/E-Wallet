package pl.tkaczyk.walletapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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


        ImageView imageView = getView().findViewById(R.id.addImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewDialog();
            }
        });

    }

    public void createNewDialog() {
        dialogBuilder = new AlertDialog.Builder(getView().getContext());
        final View popupView = getLayoutInflater().inflate(R.layout.activity_popup, null);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        fillSpinner(popupView);

        cancelButton = (Button) popupView.findViewById(R.id.buttonPopupCancel);
        addButton = (Button) popupView.findViewById(R.id.buttonPopupAdd);
        valueExpenseEditText = (EditText) popupView.findViewById(R.id.editTextValueOfExpense);
        spinner = (Spinner) popupView.findViewById(R.id.spinner);
        dateButton = (Button) popupView.findViewById(R.id.buttonPopupCalendar);
        tvDate = (TextView) popupView.findViewById(R.id.tvDate);
        descriptionEditText = (EditText) popupView.findViewById(R.id.editTextDescription);

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

//        Wydatki wydatki = new Wydatki(valueOfExpense, categoryOfExpense, signInAccount.getEmail(), date, descriptionOfExpense);

//        db.collection("expenses").add(wydatki).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//            @Override
//            public void onSuccess(DocumentReference documentReference) {
//                Log.d(TAG, "onSuccess: " + documentReference.getId());
//            }
//        });

    }

    public void fillSpinner(View view) {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference categoryRef = rootRef.collection("categories");
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        List<String> categories = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        categoryRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    String name = documentSnapshot.getString("name");
                    Log.d(TAG, name.toString());
                    categories.add(name);
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.d(TAG, "createNewDialog: Error", task.getException());
            }
        });

    }


    void makeChart(PieChart mPieChart) {
        db.collection("expenses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Log.d(TAG, "onComplete show document data: " + document.getData());
                    }
                }
            }
        });

        DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("sampleData/inspiration");
        ArrayList<PieEntry> data = new ArrayList<>();
        mDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Log.d(TAG, "onComplete: Good");
                } else {
                    Log.d(TAG, "onComplete: Fail");
                }
            }
        });
        mDocumentReference.get().addOnSuccessListener(documentSnapshot -> {

            data.add(new PieEntry(20, documentSnapshot.getString(QUOTE_KEY)));

            PieDataSet pieDataSet = new PieDataSet(data, "Wydatki");
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            pieDataSet.setValueTextColor(Color.BLACK);
            pieDataSet.setValueTextSize(16f);

            PieData pieData = new PieData(pieDataSet);

            mPieChart.setData(pieData);
            mPieChart.getDescription().setEnabled(false);
            mPieChart.setCenterText("Wydatki");
            mPieChart.animate();
        });
    }
}
