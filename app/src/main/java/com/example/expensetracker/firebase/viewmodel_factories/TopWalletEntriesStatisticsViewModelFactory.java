package com.example.expensetracker.firebase.viewmodel_factories;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.FragmentActivity;


import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import com.example.expensetracker.firebase.viewmodels.WalletEntriesBaseViewModel;

public class TopWalletEntriesStatisticsViewModelFactory implements ViewModelProvider.Factory {
    private Calendar endDate;
    private Calendar startDate;
    private String uid;

    TopWalletEntriesStatisticsViewModelFactory(String uid) {
        this.uid = uid;


    }
    public void setDate(Calendar startDate, Calendar endDate){
        this.startDate=startDate;
        this.endDate=endDate;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new Model(uid);  // Assuming 'Model' is your ViewModel class
    }

    public static Model getModel(String uid, FragmentActivity activity) {
        // Use ViewModelProvider with the custom Factory
        return new ViewModelProvider(activity, new TopWalletEntriesStatisticsViewModelFactory(uid)).get(Model.class);
    }

    public static class Model extends WalletEntriesBaseViewModel {

        public Model(String uid) {
            super(uid, FirebaseDatabase.getInstance().getReference()
                    .child("wallet-entries").child(uid).child("default").orderByChild("timestamp"));
        }

        public void setDateFilter(Calendar startDate, Calendar endDate) {
            liveData.setQuery(FirebaseDatabase.getInstance().getReference()
                    .child("wallet-entries").child(uid).child("default").orderByChild("timestamp")
                    .startAt(-endDate.getTimeInMillis()).endAt(-startDate.getTimeInMillis()));
        }
    }
}