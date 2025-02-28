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
import com.example.expensetracker.firebase.models.WalletEntryCategory;

public class EditCustomCategoryActivity extends BaseActivity {

    private TextInputEditText selectNameEditText;
    private Button selectColorButton;
    private Button editCustomCategoryButton;
    private ImageView iconImageView;
    private int selectedColor;
    private String categoryID;
    private Button removeCustomCategoryButton;
    private String categoryName;
    private TextInputLayout selectNameInputLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryID = getIntent().getExtras().getString("category-id");
        categoryName = getIntent().getExtras().getString("category-name");
        selectedColor = getIntent().getExtras().getInt("category-color");

        setContentView(R.layout.activity_edit_custom_category);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit custom category");


        iconImageView = findViewById(R.id.icon_imageview);
        iconImageView.setBackgroundTintList(ColorStateList.valueOf(selectedColor));
        selectNameEditText = findViewById(R.id.select_name_edittext);
        selectNameEditText.setText(categoryName);
        selectNameInputLayout = findViewById(R.id.select_name_inputlayout);
        selectColorButton = findViewById(R.id.select_color_button);
        selectColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker();
            }
        });

        editCustomCategoryButton = findViewById(R.id.edit_custom_category_button);
        editCustomCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editCustomCategory(selectNameEditText.getText().toString(), "#" + Integer.toHexString(selectedColor));
                } catch (EmptyStringException e) {
                    selectNameInputLayout.setError(e.getMessage());
                }

            }
        });

        removeCustomCategoryButton = findViewById(R.id.remove_custom_category_button);
        removeCustomCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users").child(getUid()).child("customCategories").child(categoryID).removeValue();
                finish();

            }
        });
    }

    private void editCustomCategory(String categoryName, String categoryHtmlCode) throws EmptyStringException {
        if(categoryName == null || categoryName.length() == 0)
            throw new EmptyStringException("Entry name length should be > 0");

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(getUid()).child("customCategories").child(categoryID).setValue(
                new WalletEntryCategory(categoryName,  categoryHtmlCode));
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }
}
