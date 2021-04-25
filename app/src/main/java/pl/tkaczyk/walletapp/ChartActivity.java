package pl.tkaczyk.walletapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {
    private static final String TAG = "ChartActivity";

    public static final String VALUE_KEY = "value";
    public static final String QUOTE_KEY = "quote";
    TextView email;
    Button logout;
    private FirebaseAuth mAuth;
    private DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("sampleData/inspiration");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        logout = findViewById(R.id.logout);
        email = findViewById(R.id.email);


        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);


        if (signInAccount != null) {
            email.setText(signInAccount.getEmail());
        }

        logout.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        PieChart mPieChart = findViewById(R.id.chart);
        ArrayList<PieEntry> data = new ArrayList<>();
        mDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Log.d(TAG, "onComplete: Good");
                }else{
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