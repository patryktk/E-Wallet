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

public class CustomAdapterRVCategories extends RecyclerView.Adapter<CustomAdapterRVCategories.MyViewHolder> {

    private Context mContext;
    private ArrayList arrayCategoriesName;

    public CustomAdapterRVCategories(Context context, ArrayList arrayCategoriesName) {
        this.mContext = context;
        this.arrayCategoriesName = arrayCategoriesName;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_row, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CustomAdapterRVCategories.MyViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.categoriesName.setText(String.valueOf("•\t" + arrayCategoriesName.get(position)));
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
