package com.example.d4_3a04.AccountManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d4_3a04.BrowseActiveChats.BrowseActiveChats;
import com.example.d4_3a04.database.Cryptosystem;
import com.example.d4_3a04.databinding.LoginPageProviderBinding;

public class LoginPage extends AppCompatActivity {

    private LoginPageProviderBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Cryptosystem.startDB(this);

        binding = LoginPageProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.headerToolbar);

        Button login_button = binding.submitButton;
        TextView create_account_link = binding.createAccountLink;

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String company_email = binding.companyEmailInput.getText().toString().trim();
                String password = binding.passwordInput.getText().toString().trim();

                // TODO: validate company email and password
                if (!Cryptosystem.authenticateUser(company_email, password)) {
                    binding.loginError.setVisibility(View.VISIBLE);
                } else {
                    // navigate to BrowseActiveChats page
                    Intent new_view = new Intent(LoginPage.this, BrowseActiveChats.class);
                    new_view.putExtra("Employee_id", company_email);
                    startActivity(new_view);
                }
            }
        });

        create_account_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent create_account_view = new Intent(LoginPage.this, CreateAccountPage.class);
                startActivity(create_account_view);
            }
        });
    }
}
