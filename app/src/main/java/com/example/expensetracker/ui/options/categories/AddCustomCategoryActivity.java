package com.example.expensetracker.ui.options.categories;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import com.example.expensetracker.R;
import com.example.expensetracker.base.BaseActivity;
import com.example.expensetracker.exceptions.EmptyStringException;
import com.example.expensetracker.firebase.FirebaseElement;
import com.example.expensetracker.firebase.FirebaseObserver;
import com.example.expensetracker.firebase.models.User;
import com.example.expensetracker.firebase.models.WalletEntryCategory;
import com.example.expensetracker.firebase.viewmodel_factories.UserProfileViewModelFactory;

public class AddCustomCategoryActivity extends BaseActivity {

    private TextInputEditText selectNameEditText;
    private Button selectColorButton;
    private Button addCustomCategoryButton;
    private User user;
    private ImageView iconImageView;
    private int selectedColor = Color.parseColor("#000000");
    private TextInputLayout selectNameInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_custom_category);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add custom category");

        UserProfileViewModelFactory.getModel(getUid(), this).observe(this, new FirebaseObserver<FirebaseElement<User>>() {
            @Override
            public void onChanged(FirebaseElement<User> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    AddCustomCategoryActivity.this.user = firebaseElement.getElement();
                    dataUpdated();
                }
            }
        });


    }

    public void showColorPicker() {
        // Define available colors
        int[] colors = {
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
                Color.CYAN, Color.MAGENTA, Color.BLACK, Color.WHITE
        };

        String[] colorNames = {"Red", "Blue", "Green", "Yellow", "Cyan", "Magenta", "Black", "White"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Choose a Color")
                .setItems(colorNames, (dialog, which) -> {
                    selectedColor = colors[which];
                    iconImageView.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
                })
                .show();
    }
    private void dataUpdated() {
        if (user == null) return;
        iconImageView = findViewById(R.id.icon_imageview);
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectNameInputLayout = findViewById(R.id.select_name_inputlayout);
        selectColorButton = findViewById(R.id.select_color_button);
        selectColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker();
            }
        });

        addCustomCategoryButton = findViewById(R.id.add_custom_category_button);
        addCustomCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addCustomCategory(selectNameEditText.getText().toString(), "#" + Integer.toHexString(selectedColor));
                } catch (EmptyStringException e) {
                    selectNameInputLayout.setError(e.getMessage());
                }


            }
        });
    }

    private void addCustomCategory(String categoryName, String categoryHtmlCode) throws EmptyStringException {
        if(categoryName == null || categoryName.length() == 0)
            throw new EmptyStringException("Entry name length should be > 0");

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).child("customCategories").push().setValue(
                new WalletEntryCategory(categoryName,  categoryHtmlCode));
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
