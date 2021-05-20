package pl.tkaczyk.walletapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import pl.tkaczyk.walletapp.R;

public class AdapterRVExpenses extends RecyclerView.Adapter<AdapterRVExpenses.ViewHolder> {

    private Context mContext;
    private ArrayList arrayExpenseValue, arrayExpenseDate, arrayExpenseCategory;

    public AdapterRVExpenses(Context context, ArrayList arrayExpenseValue, ArrayList arrayExpenseDate, ArrayList arrayExpenseCategory) {
        this.mContext = context;
        this.arrayExpenseValue = arrayExpenseValue;
        this.arrayExpenseDate = arrayExpenseDate;
        this.arrayExpenseCategory = arrayExpenseCategory;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenses_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterRVExpenses.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.expenseValue.setText(String.valueOf(arrayExpenseValue.get(position)));
        viewHolder.expenseCategory.setText(String.valueOf(arrayExpenseCategory.get(position)));
        viewHolder.expenseDate.setText(String.valueOf(arrayExpenseDate.get(position)));

    }

    @Override
    public int getItemCount() {
        return arrayExpenseCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView expenseValue, expenseDate, expenseCategory;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            expenseValue = itemView.findViewById(R.id.textViewExpenseRowValue);
            expenseDate = itemView.findViewById(R.id.textViewExpenseRowDate);
            expenseCategory = itemView.findViewById(R.id.textViewExpenseRowCategory);
        }
    }
}
