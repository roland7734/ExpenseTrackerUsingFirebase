package com.example.expensetracker.exceptions;

public class ZeroBalanceDifferenceException extends Exception {
    public ZeroBalanceDifferenceException(String text) {
        super(text);
    }
}
