package com.example.expensetracker.firebase;


public interface FirebaseObserver<T> {
    void onChanged(T t);
}
