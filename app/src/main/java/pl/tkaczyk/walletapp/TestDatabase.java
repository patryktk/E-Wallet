package pl.tkaczyk.walletapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestDatabase extends AppCompatActivity {
    public static final String AUTHOR_KEY = "author";
    public static final String QUOTE_KEY = "quote";
    private static final String TAG = "TestDatabase";
    TextView mQuoteTextView;

    private DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("sampleData/inspiration");



    @Override
    protected void onStart() {
        super.onStart();
        mDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    String quoteText = documentSnapshot.getString(QUOTE_KEY);
                    String authorText = documentSnapshot.getString(AUTHOR_KEY);
                    mQuoteTextView.setText("\"" + quoteText + "\" -- " + authorText);
                } else if (e != null) {
                    Log.w(TAG, "onEvent: Got on exception", e);
                }
            }
        });
        PieChart mPieChart = (PieChart) findViewById(R.id.chart);
        ArrayList<PieEntry> data = new ArrayList<>();
        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                data.add(new PieEntry(20,documentSnapshot.getString(QUOTE_KEY)));

                PieDataSet pieDataSet = new PieDataSet(data, "Wydatki");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieDataSet.setValueTextColor(Color.BLACK);
                pieDataSet.setValueTextSize(16f);

                PieData pieData = new PieData(pieDataSet);

                mPieChart.setData(pieData);
                mPieChart.getDescription().setEnabled(false);
                mPieChart.setCenterText("Wydatki");
                mPieChart.animate();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);
        mQuoteTextView = (TextView) findViewById(R.id.textView);
    }

    public void fetch(View view) {
        mDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String quoteText = documentSnapshot.getString(QUOTE_KEY);
                    String authorText = documentSnapshot.getString(AUTHOR_KEY);

                    mQuoteTextView.setText("\"" + quoteText + "\" -- " + authorText);

//                     Map<String , Object> mydata =documentSnapshot.getData(); //dostajemy wszystkie dane z bazy
                }
            }
        });
    }

    public void saveQuote(View view) {
        EditText quoteView = (EditText) findViewById(R.id.editText1);
        EditText authorView = (EditText) findViewById(R.id.editText2);
        String quoteText = quoteView.getText().toString();
        String authorText = authorView.getText().toString();

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        if (quoteText.isEmpty() || authorText.isEmpty()) {
            return;
        }
        Map<String, Object> dataToSave = new HashMap<>();
        dataToSave.put(QUOTE_KEY, quoteText);
        dataToSave.put(AUTHOR_KEY, authorText);
        dataToSave.put("User", signInAccount.getEmail());
        dataToSave.put("Date", new Date());
        mDocumentReference.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Document saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Document was not saved", e);
            }
        });
    }
}