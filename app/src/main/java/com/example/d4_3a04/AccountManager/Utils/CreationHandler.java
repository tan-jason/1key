package com.example.d4_3a04.AccountManager.Utils;

import com.example.d4_3a04.ErrorHandler.ErrorHandler;

import java.util.Arrays;

public class CreationHandler {
    private ErrorHandler errorHandler;

    public CreationHandler() {
        this.errorHandler = new ErrorHandler();
    }

    public String validateAccountCredentials(String username, String password) {
        System.out.println(username.matches(Constants.EMAIL_REGEX));
        if (!username.matches(Constants.EMAIL_REGEX) || username.isEmpty()) {
            return this.errorHandler.getErrorMessage("invalid company email");
        }

        if (password.contains(Constants.INVALID_CHAR) || password.isEmpty()) {
            return this.errorHandler.getErrorMessage("invalid account password");
        }



        return "";
    }
}
