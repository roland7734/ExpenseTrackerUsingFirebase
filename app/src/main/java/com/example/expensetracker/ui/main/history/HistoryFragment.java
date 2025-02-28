package com.example.expensetracker.ui.main.history;

import static java.util.Calendar.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.datepicker.CalendarConstraints;
import android.widget.TextView;
import com.google.android.material.datepicker.MaterialDatePicker;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.example.expensetracker.R;
import com.example.expensetracker.firebase.viewmodel_factories.WalletEntriesHistoryViewModelFactory;
import com.example.expensetracker.base.BaseFragment;
import com.example.expensetracker.ui.options.OptionsActivity;
import com.google.android.material.datepicker.DateValidatorPointForward;


public class HistoryFragment extends BaseFragment {
    public static final CharSequence TITLE = "History";
    Calendar calendarStart;
    Calendar calendarEnd;
    private RecyclerView historyRecyclerView;
    private WalletEntriesRecyclerViewAdapter historyRecyclerViewAdapter;
    private Menu menu;
    private TextView dividerTextView;

    public static HistoryFragment newInstance() {

        return new HistoryFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        dividerTextView = view.findViewById(R.id.divider_textview);
        dividerTextView.setText("Last 100 elements:");
        historyRecyclerView = view.findViewById(R.id.history_recycler_view);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        historyRecyclerViewAdapter = new WalletEntriesRecyclerViewAdapter(getActivity(), getUid());
        historyRecyclerView.setAdapter(historyRecyclerViewAdapter);

        historyRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                historyRecyclerView.smoothScrollToPosition(0);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.history_fragment_menu, menu);
        this.menu = menu;
        updateCalendarIcon();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_date_range) {
            showSelectDateRangeDialog();
            return true;
        } else if (itemId == R.id.action_options) {
            startActivity(new Intent(getActivity(), OptionsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void updateCalendarIcon() {
        MenuItem calendarIcon = menu.findItem(R.id.action_date_range);
        if (calendarIcon == null) return;
        WalletEntriesHistoryViewModelFactory.Model model = WalletEntriesHistoryViewModelFactory.getModel(getUid(), getActivity());
        if (model.hasDateSet()) {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar_active));

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

            dividerTextView.setText("Date range: " + dateFormat.format(model.getStartDate().getTime())
                    + "  -  " + dateFormat.format(model.getEndDate().getTime()));
        } else {
            calendarIcon.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.icon_calendar));

            dividerTextView.setText("Last 100 elements:");
        }

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
                updateCalendarIcon();
            }
        });

        // Handle Cancellation
        datePicker.addOnNegativeButtonClickListener(dialog -> {
            calendarStart = null;
            calendarEnd = null;
            calendarUpdated();
            updateCalendarIcon();
        });

        // Show the Date Picker
        datePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
    }


    private void calendarUpdated() {
        historyRecyclerViewAdapter.setDateRange(calendarStart, calendarEnd);
    }

}
