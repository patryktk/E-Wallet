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

public class AdapterBudget extends RecyclerView.Adapter<AdapterBudget.ViewHolder> {

    private Context mContext;
    private ArrayList arrayBudgetCategory, arrayBudgetValue;

    public AdapterBudget(Context context, ArrayList arrayBudgetCategory, ArrayList arrayBudgetValue) {
        mContext = context;
        this.arrayBudgetCategory = arrayBudgetCategory;
        this.arrayBudgetValue = arrayBudgetValue;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterBudget.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.budgetValue.setText(String.valueOf(arrayBudgetValue.get(position)));
        viewHolder.budgetCategory.setText(String.valueOf(arrayBudgetCategory.get(position)));

    }

    @Override
    public int getItemCount() {
        return arrayBudgetCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView budgetCategory, budgetValue;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            budgetCategory = itemView.findViewById(R.id.textViewBudgetRowCategory);
            budgetValue = itemView.findViewById(R.id.textViewBudgetRowValue);
        }
    }
}
