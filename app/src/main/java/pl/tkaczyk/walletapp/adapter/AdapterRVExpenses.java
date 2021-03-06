package pl.tkaczyk.walletapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import pl.tkaczyk.walletapp.EditExpenseActivity;
import pl.tkaczyk.walletapp.EditIncomeActivity;
import pl.tkaczyk.walletapp.R;

public class AdapterRVExpenses extends RecyclerView.Adapter<AdapterRVExpenses.ViewHolder> {

    private Context mContext;
    private ArrayList arrayExpenseId,arrayExpenseValue, arrayExpenseDate, arrayExpenseCategory, arrayExpenseDescription, arrayExpenseMonth, arrayExpenseMark;

    public AdapterRVExpenses(Context mContext, ArrayList arrayExpenseId, ArrayList arrayExpenseValue, ArrayList arrayExpenseDate, ArrayList arrayExpenseCategory, ArrayList arrayExpenseDescription, ArrayList arrayExpenseMonth, ArrayList arrayExpenseMark) {
        this.mContext = mContext;
        this.arrayExpenseId = arrayExpenseId;
        this.arrayExpenseValue = arrayExpenseValue;
        this.arrayExpenseDate = arrayExpenseDate;
        this.arrayExpenseCategory = arrayExpenseCategory;
        this.arrayExpenseDescription = arrayExpenseDescription;
        this.arrayExpenseMonth = arrayExpenseMonth;
        this.arrayExpenseMark = arrayExpenseMark;
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
        viewHolder.expenseDescription.setText(String.valueOf(arrayExpenseDescription.get(position)));
        viewHolder.expenseMark.setText(String.valueOf(arrayExpenseMark.get(position)));

        viewHolder.expenseRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrayExpenseCategory.get(position) == "Przych??d"){
                    Intent intent = new Intent(mContext, EditIncomeActivity.class);
                    intent.putExtra("Id", String.valueOf(arrayExpenseId.get(position)));
                    intent.putExtra("Value", String.valueOf(arrayExpenseValue.get(position)));
                    intent.putExtra("Date", String.valueOf(arrayExpenseDate.get(position)));
                    intent.putExtra("Description", String.valueOf(arrayExpenseDescription.get(position)));
                    intent.putExtra("Month", String.valueOf(arrayExpenseMonth.get(position)));
                    intent.putExtra("Mark", String.valueOf(arrayExpenseMark.get(position)));
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(mContext, EditExpenseActivity.class);
                    intent.putExtra("Id", String.valueOf(arrayExpenseId.get(position)));
                    intent.putExtra("Value", String.valueOf(arrayExpenseValue.get(position)));
                    intent.putExtra("Category", String.valueOf(arrayExpenseCategory.get(position)));
                    intent.putExtra("Date", String.valueOf(arrayExpenseDate.get(position)));
                    intent.putExtra("Description", String.valueOf(arrayExpenseDescription.get(position)));
                    intent.putExtra("Month", String.valueOf(arrayExpenseMonth.get(position)));
                    intent.putExtra("Mark", String.valueOf(arrayExpenseMark.get(position)));

                    mContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayExpenseCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView expenseId,expenseValue, expenseDate, expenseCategory, expenseDescription, expenseMark;
        LinearLayout expenseRow;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            expenseValue = itemView.findViewById(R.id.textViewExpenseRowValue);
            expenseDate = itemView.findViewById(R.id.textViewExpenseRowDate);
            expenseCategory = itemView.findViewById(R.id.textViewExpenseRowCategory);
            expenseDescription = itemView.findViewById(R.id.textViewExpenseRowDescription);
            expenseMark = itemView.findViewById(R.id.textViewExpenseMark);

            expenseRow = itemView.findViewById(R.id.expense_row);


        }
    }
}
