package com.example.d4_3a04.ErrorHandler;

public class ErrorHandler {
    private String[] errors = new String[]{"invalid company email", "invalid account password", "invalid login"};

    public String getErrorMessage(String error) {
        for (String e : errors) {
            if (e.equals(error)) {
                return e;
            }
        }
        return "Unidentified error";
    }
}
