package pl.tkaczyk.walletapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CustomAdapterRecycleView extends RecyclerView.Adapter<CustomAdapterRecycleView.MyViewHolder> {

    private Context mContext;
    private ArrayList arrayCategoriesName;

    public CustomAdapterRecycleView(Context context, ArrayList arrayCategoriesName) {
        this.mContext = context;
        this.arrayCategoriesName = arrayCategoriesName;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CustomAdapterRecycleView.MyViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.categoriesName.setText(String.valueOf(arrayCategoriesName.get(position)));
    }

    @Override
    public int getItemCount() {
        return arrayCategoriesName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView categoriesName;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            categoriesName = itemView.findViewById(R.id.categoriesName);
        }

    }
}
