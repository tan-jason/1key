package com.example.d4_3a04.AccountManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d4_3a04.AccountManager.Utils.CreationHandler;
import com.example.d4_3a04.BrowseActiveChats.BrowseActiveChats;
import com.example.d4_3a04.database.Cryptosystem;
import com.example.d4_3a04.databinding.CreateAccountPageProviderBinding;

public class CreateAccountPage extends AppCompatActivity {
    private CreateAccountPageProviderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Cryptosystem.startDB(this);

        binding = com.example.d4_3a04.databinding.CreateAccountPageProviderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.headerToolbar);

        Button create_account_button = binding.submitButton;
        TextView back_link = binding.backLink;

        create_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String company_email = binding.companyEmailInput.getText().toString().trim();
                String password = binding.passwordInput.getText().toString().trim();

                // reset errors
                binding.companyEmailErrorMsg.setVisibility(View.INVISIBLE);
                binding.passwordErrorMsg.setVisibility(View.INVISIBLE);

                // check if email or password contains any harmful characters
                CreationHandler creationHandler = new CreationHandler();
                String validation = creationHandler.validateAccountCredentials(company_email, password);


                if (validation.isEmpty()) {
                    Boolean res = Cryptosystem.addUser(company_email, password);
                    if (!res){
                        binding.companyEmailErrorMsg.setVisibility(View.VISIBLE);
                    }else{
                        Intent new_view = new Intent(CreateAccountPage.this, BrowseActiveChats.class);
                        startActivity(new_view);
                    }
                } else if (validation.equals("invalid company email")){
                    binding.companyEmailErrorMsg.setVisibility(View.VISIBLE);
                } else if (validation.equals("invalid account password")) {
                    binding.passwordErrorMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        back_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
