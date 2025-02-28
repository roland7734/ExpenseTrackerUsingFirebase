package com.example.expensetracker.ui.main.statistics;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.getInstance;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


import com.example.expensetracker.R;
import com.example.expensetracker.base.BaseFragment;
import com.example.expensetracker.firebase.FirebaseElement;
import com.example.expensetracker.firebase.FirebaseObserver;
import com.example.expensetracker.firebase.ListDataSet;
import com.example.expensetracker.firebase.models.User;
import com.example.expensetracker.firebase.models.WalletEntry;
import com.example.expensetracker.firebase.viewmodel_factories.TopWalletEntriesStatisticsViewModelFactory;
import com.example.expensetracker.firebase.viewmodel_factories.UserProfileViewModelFactory;
import com.example.expensetracker.util.CalendarHelper;
import com.example.expensetracker.util.CategoriesHelper;
import com.example.expensetracker.models.Category;
import com.example.expensetracker.ui.options.OptionsActivity;
import com.example.expensetracker.util.CurrencyHelper;
import com.example.expensetracker.R;
import com.example.expensetracker.firebase.viewmodel_factories.WalletEntriesHistoryViewModelFactory;
import com.example.expensetracker.base.BaseFragment;
import com.example.expensetracker.ui.options.OptionsActivity;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import androidx.core.util.Pair;



public class StatisticsFragment extends BaseFragment {
    public static final CharSequence TITLE = "Statistics";

    private Menu menu;
    private Calendar calendarStart;
    private Calendar calendarEnd;
    private User user;
    private ListDataSet<WalletEntry> walletEntryListDataSet;
//    private PieChart pieChart;
    private ArrayList<TopCategoryStatisticsListViewModel> categoryModelsHome;
    private TopCategoriesStatisticsAdapter adapter;
    private TextView dividerTextView;
    private ProgressBar incomesExpensesProgressBar;
    private TextView incomesTextView;
    private TextView expensesTextView;

    public static StatisticsFragment newInstance() {

        return new StatisticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        pieChart = view.findViewById(R.id.pie_chart);
        dividerTextView = view.findViewById(R.id.divider_textview);
        View incomesExpensesView = view.findViewById(R.id.incomes_expenses_view);
        incomesExpensesProgressBar = incomesExpensesView.findViewById(R.id.progress_bar);
        expensesTextView = incomesExpensesView.findViewById(R.id.expenses_textview);
        incomesTextView = incomesExpensesView.findViewById(R.id.incomes_textview);

        categoryModelsHome = new ArrayList<>();
        ListView favoriteListView = view.findViewById(R.id.favourite_categories_list_view);
        adapter = new TopCategoriesStatisticsAdapter(categoryModelsHome, getActivity().getApplicationContext());
        favoriteListView.setAdapter(adapter);

        TopWalletEntriesStatisticsViewModelFactory.getModel(getUid(), getActivity()).observe(this, new FirebaseObserver<FirebaseElement<ListDataSet<WalletEntry>>>() {

            @Override
            public void onChanged(FirebaseElement<ListDataSet<WalletEntry>> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    StatisticsFragment.this.walletEntryListDataSet = firebaseElement.getElement();
                    dataUpdated();
                }
            }

        });


        UserProfileViewModelFactory.getModel(getUid(), getActivity()).observe(this, new FirebaseObserver<FirebaseElement<User>>() {
            @Override
            public void onChanged(FirebaseElement<User> firebaseElement) {
                if (firebaseElement.hasNoError()) {
                    StatisticsFragment.this.user = firebaseElement.getElement();

                    calendarStart = CalendarHelper.getUserPeriodStartDate(user);
                    calendarEnd = CalendarHelper.getUserPeriodEndDate(user);

                    updateCalendarIcon(false);
                    calendarUpdated();
                    dataUpdated();

                }
            }
        });

    }


    private void dataUpdated() {
//        if (calendarStart != null && calendarEnd != null && walletEntryListDataSet != null) {
//            List<WalletEntry> entryList = new ArrayList<>(walletEntryListDataSet.getList());
//
//            long expensesSumInDateRange = 0;
//            long incomesSumInDateRange = 0;
//
//            HashMap<Category, Long> categoryModels = new HashMap<>();
//            for (WalletEntry walletEntry : entryList) {
//                if (walletEntry.balanceDifference > 0) {
//                    incomesSumInDateRange += walletEntry.balanceDifference;
//                    continue;
//                }
//                expensesSumInDateRange += walletEntry.balanceDifference;
//                Category category = CategoriesHelper.searchCategory(user, walletEntry.categoryID);
//                categoryModels.put(category, categoryModels.getOrDefault(category, 0L) + walletEntry.balanceDifference);
//            }
//
//            categoryModelsHome.clear();
//
//            ArrayList<Segment> pieSegments = new ArrayList<>();
//            ArrayList<Integer> pieColors = new ArrayList<>();
//
//            for (Map.Entry<Category, Long> categoryModel : categoryModels.entrySet()) {
//                float percentage = categoryModel.getValue() / (float) expensesSumInDateRange;
//                final float minPercentageToShowLabelOnChart = 0.1f;
//
//                categoryModelsHome.add(new TopCategoryStatisticsListViewModel(
//                        categoryModel.getKey(),
//                        categoryModel.getKey().getCategoryVisibleName(getContext()),
//                        user.currency,
//                        categoryModel.getValue(),
//                        percentage
//                ));
//
//                // Add a segment to the PieChart
//                if (percentage > minPercentageToShowLabelOnChart) {
//                    Segment segment = new Segment(categoryModel.getKey().getCategoryVisibleName(getContext()), categoryModel.getValue());
//                    pieSegments.add(segment);
//                } else {
//                    Segment segment = new Segment("Other", categoryModel.getValue());
//                    pieSegments.add(segment);
//                }
//                pieColors.add(categoryModel.getKey().getIconColor());
//            }
//
//            // Create the PieChart and set up its properties
////            PieChart pieChart = findViewById(R.id.pieChart);
//
////            PieRenderer pieRenderer = (PieRenderer) pieChart.getRenderer();
////            pieRenderer.setSliceSpacing(PixelUtils.dpToPix(2));
////            pieRenderer.setPieBackgroundColor(getContext().getResources().getColor(R.color.backgroundPrimary));
//
//            // Set the data on the PieChart
//            pieChart.setTag(pieSegments);
//
////            // Customize the appearance of the PieChart
////            pieChart.getRenderer(PieRenderer.class).setSliceSpacing(5);
////            pieChart.getRenderer(PieRenderer.class).setSliceThickness(25f);
////
////            // Optionally customize colors
////            pieChart.getRenderer(PieRenderer.class).setSlicePaint(pieColors);
//
//            pieChart.redraw(); // Redraw the chart after setting the data
//
//            Collections.sort(categoryModelsHome, new Comparator<TopCategoryStatisticsListViewModel>() {
//                @Override
//                public int compare(TopCategoryStatisticsListViewModel o1, TopCategoryStatisticsListViewModel o2) {
//                    return Long.compare(o1.getMoney(), o2.getMoney());
//                }
//            });
//
//            adapter.notifyDataSetChanged();
//
//            // Format the date range for display
//            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
//            dividerTextView.setText("Date range: " + dateFormat.format(calendarStart.getTime())
//                    + "  -  " + dateFormat.format(calendarEnd.getTime()));
//
//            expensesTextView.setText(CurrencyHelper.formatCurrency(user.currency, expensesSumInDateRange));
//            incomesTextView.setText(CurrencyHelper.formatCurrency(user.currency, incomesSumInDateRange));
//
//            // Set progress bar based on expenses and incomes
//            float progress = 100 * incomesSumInDateRange / (float) (incomesSumInDateRange - expensesSumInDateRange);
//            incomesExpensesProgressBar.setProgress((int) progress);
//        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_fragment_menu, menu);
        this.menu = menu;
        updateCalendarIcon(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateCalendarIcon(boolean updatedFromUI) {
        if (menu == null) return;
        MenuItem calendarIcon = menu.findItem(R.id.action_date_range);
        if (calendarIcon == null) return;
        if (updatedFromUI) {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar_active));
        } else {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.action_date_range)
        {
            showSelectDateRangeDialog();
            return true;
        } else if(itemId == R.id.action_options) {
            startActivity(new Intent(getActivity(), OptionsActivity.class));
            return true;
        }
            else return super.onOptionsItemSelected(item);
    }

    private void showSelectDateRangeDialog() {
        // Create a Date Range Picker
        long today = Calendar.getInstance().getTimeInMillis();

        MaterialDatePicker<Pair<Long, Long>> datePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select Date Range")
                        .setSelection(new Pair<>(today, today)) // Set default to today
                        .setCalendarConstraints(new CalendarConstraints.Builder()
                                .setValidator(DateValidatorPointForward.now()) // Disable past dates
                                .build())
                        .build();

        // Handle Date Selection
        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection.first != null && selection.second != null) {
                Calendar calendarStart = getInstance(TimeZone.getTimeZone("UTC"));
                calendarStart.setTimeInMillis(selection.first);
                calendarStart.set(HOUR_OF_DAY, 0);
                calendarStart.set(MINUTE, 0);
                calendarStart.set(SECOND, 0);

                Calendar calendarEnd = getInstance(TimeZone.getTimeZone("UTC"));
                calendarEnd.setTimeInMillis(selection.second);
                calendarEnd.set(HOUR_OF_DAY, 23);
                calendarEnd.set(MINUTE, 59);
                calendarEnd.set(SECOND, 59);

                calendarUpdated();
                updateCalendarIcon(true);
            }
        });

        // Handle Cancellation
        datePicker.addOnNegativeButtonClickListener(dialog -> {
            calendarStart = null;
            calendarEnd = null;
            calendarUpdated();
            updateCalendarIcon(true);
        });

        // Show the Date Picker
        datePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
    }


    private void calendarUpdated() {
        TopWalletEntriesStatisticsViewModelFactory.getModel(getUid(), getActivity()).setDateFilter(calendarStart, calendarEnd);

    }


}
