package com.example.expensetracker.firebase.viewmodel_factories;



import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

import com.example.expensetracker.firebase.FirebaseElement;
import com.example.expensetracker.firebase.FirebaseObserver;
import com.example.expensetracker.firebase.FirebaseQueryLiveDataElement;
import com.example.expensetracker.firebase.models.User;
import com.example.expensetracker.firebase.viewmodels.UserProfileBaseViewModel;

public class UserProfileViewModelFactory implements ViewModelProvider.Factory {
    private String uid;

    private UserProfileViewModelFactory(String uid) {
        this.uid = uid;

    }

    public static void saveModel(String uid, User user) {
        FirebaseDatabase.getInstance().getReference()
                .child("users").child(uid).setValue(user);
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new UserProfileBaseViewModel(uid);
    }

    public static UserProfileBaseViewModel getModel(String uid, FragmentActivity activity) {
        return new ViewModelProvider(activity, new UserProfileViewModelFactory(uid)).get(UserProfileBaseViewModel.class);
    }


}