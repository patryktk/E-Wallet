package pl.tkaczyk.walletapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainChartFragment extends Fragment {
    public static final String VALUE_KEY = "value";
    public static final String QUOTE_KEY = "quote";
    private static final String TAG = "MainChartFragment";
    private final DocumentReference mDocumentReference = FirebaseFirestore.getInstance().document("sampleData/inspiration");
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private Button cancelButtonPopUp, saveButtonPopUp;


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
                popUp(view);
            }
        });

    }

    public void popUp(View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.activity_popup, null);

        PopupWindow popupWindow = new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        popupWindow.setWidth(350);
        popupWindow.setHeight(475);

        popupWindow.showAtLocation(anchorView, Gravity.CENTER,0,0);
    }

    void makeChart(PieChart mPieChart) {
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
