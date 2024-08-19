package com.example.d4_3a04.BrowseActiveChats;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d4_3a04.DataTypes.ChatInfo;
import com.example.d4_3a04.R;
import com.example.d4_3a04.SingleChatProvider.SingleChatManager;
import com.example.d4_3a04.database.Cryptosystem;
import com.example.d4_3a04.databinding.SearchForBinding;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class SearchFor extends AppCompatActivity {
    SearchForBinding binding;

    String employee;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Cryptosystem.startDB(this);

        Intent intent = getIntent();


        this.employee=intent.getStringExtra("this_employee");

        binding = SearchForBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SearchView sv = (SearchView) findViewById(R.id.searchView);
        LinearLayout layout = (LinearLayout) findViewById(R.id.ListView);


        List<String> all_employees = Cryptosystem.getNonConvoUsers(this.employee);

        createBox("GPT", layout);
        for (String employee: all_employees){
            createBox(employee, layout);
        }

        binding.backBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchFor.this, BrowseActiveChats.class);
                intent.putExtra("Employee_id", employee);
                SearchFor.this.startActivity(intent);
            }
        });

    }

    protected void createBox(String email, LinearLayout view){
        Button btnTag = new Button(this);
        btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        btnTag.setText(email);
        btnTag.setTag("start-convo");

        view.addView(btnTag);

        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the stored provider for given employee
                if (email.equals("GPT")){
                    createConvo("GPT");
                }else{
                    Cryptosystem.addRequest(employee, email);
                }
            }
        });



    }

    public void createConvo(String other_employee){
        ChatInfo info = new ChatInfo();
        SingleChatManager provider = new SingleChatManager(employee, other_employee, info);

        Cryptosystem.updateProvider(provider, employee, other_employee);
        Cryptosystem.updateProvider(provider, other_employee, employee);

        Intent new_view = new Intent(SearchFor.this, BrowseActiveChats.class);

        new_view.putExtra("Employee_id", employee);
        startActivity(new_view);
    }



}
