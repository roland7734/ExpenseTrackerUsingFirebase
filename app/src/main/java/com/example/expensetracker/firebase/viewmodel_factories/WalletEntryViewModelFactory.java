package com.example.expensetracker.firebase.viewmodel_factories;


import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

import com.example.expensetracker.firebase.models.User;
import com.example.expensetracker.firebase.viewmodels.UserProfileBaseViewModel;
import com.example.expensetracker.firebase.viewmodels.WalletEntryBaseViewModel;

public class WalletEntryViewModelFactory implements ViewModelProvider.Factory {
    private final String entryId;
    private final String uid;

    private WalletEntryViewModelFactory(String uid, String entryId) {
        this.uid = uid;
        this.entryId = entryId;

    }
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new WalletEntryBaseViewModel(uid, entryId);
    }

    public static WalletEntryBaseViewModel getModel(String uid, String entryId, FragmentActivity activity) {
        return new ViewModelProvider(activity, new WalletEntryViewModelFactory(uid, entryId)).get(WalletEntryBaseViewModel.class);
    }


}