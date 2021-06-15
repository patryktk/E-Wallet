package pl.tkaczyk.walletapp.fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import pl.tkaczyk.walletapp.DataBaseHelper;
import pl.tkaczyk.walletapp.R;
import pl.tkaczyk.walletapp.adapter.CustomAdapterRVCategories;
import pl.tkaczyk.walletapp.model.Categories;

public class CategoriesFragment extends Fragment {

    RecyclerView mRecyclerView;
    DataBaseHelper db;
    ArrayList<String> categoriesName;
    CustomAdapterRVCategories mCustomAdapterRVCategories;
    FloatingActionButton floatingRemoveButton, floatingAddButton;
    Button addButton, removeButton;
    EditText editTextCategoryNameAdd, editTextCategoryNameRemove;
    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerViewCategories);
        db = new DataBaseHelper(getContext());

        categoriesName = new ArrayList<>();

        floatingAddButton = view.findViewById(R.id.floatingActionButtonAdd);
        floatingAddButton.setOnClickListener(v -> {
            createNewAddDialog(categoriesName, view);
        });

        floatingRemoveButton = view.findViewById(R.id.floatingActionButtonRemove);
        floatingRemoveButton.setOnClickListener(v -> {
            createNewRemoveDialog(categoriesName, view);
        });

        xxx(categoriesName, view);


        return view;
    }

    public void xxx(ArrayList categoriesName, View view) {
        categoriesName.clear();
        storeDataInArray();
        mCustomAdapterRVCategories = new CustomAdapterRVCategories(getContext(), categoriesName);
        mRecyclerView.setAdapter(mCustomAdapterRVCategories);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.fragment_categories, null);
    }

    void storeDataInArray() {
        Cursor cursor = db.getCategories();
        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                categoriesName.add(cursor.getString(1));
            }
        }
    }

    public void createNewAddDialog(ArrayList categoriesName, View view) {
        dialogBuilder = new AlertDialog.Builder(getView().getContext());
        final View popupView = getLayoutInflater().inflate(R.layout.popup_categories_add, null);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        addButton = popupView.findViewById(R.id.buttonCategoriesPopupAdd);
        editTextCategoryNameAdd = popupView.findViewById(R.id.editTextPopupCategoriesAdd);


        addButton.setOnClickListener(v -> {
            String categoryName = editTextCategoryNameAdd.getText().toString();
            if (categoryName.isEmpty()) {
                Toast.makeText(getContext(), "Uzupełnij pole nazwy", Toast.LENGTH_SHORT).show();
            } else {
                Categories categories = new Categories(-1, categoryName);
                db.addOne(categories);
                Toast.makeText(getContext(), "Pomyślnie dodano", Toast.LENGTH_SHORT).show();
                xxx(categoriesName, view);
                dialog.dismiss();
            }
        });
    }

    public void createNewRemoveDialog(ArrayList categoriesName, View view) {
        dialogBuilder = new AlertDialog.Builder(getView().getContext());
        final View popupView = getLayoutInflater().inflate(R.layout.popup_categories_remove, null);

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        removeButton = popupView.findViewById(R.id.buttonCategoriesPopupRemove);
        editTextCategoryNameRemove = popupView.findViewById(R.id.editTextPopupCategoriesRemove);


        removeButton.setOnClickListener(v -> {
            String categoryName = editTextCategoryNameRemove.getText().toString();
            if(categoryName.isEmpty()){
                Toast.makeText(getContext(), "Uzupełnij pole nazwy", Toast.LENGTH_SHORT).show();
            }else{
                db.removeOneCategory(categoryName);
                Toast.makeText(getContext(), "Pomyślnie usunięto", Toast.LENGTH_SHORT).show();
                xxx(categoriesName, view);
                dialog.dismiss();
            }
        });
    }

}
