package com.example.expensetracker.exceptions;

public class EmptyStringException extends Exception {
    public EmptyStringException(String text) {
        super(text);
    }
}
