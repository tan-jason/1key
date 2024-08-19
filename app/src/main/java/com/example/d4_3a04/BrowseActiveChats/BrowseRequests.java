package com.example.d4_3a04.BrowseActiveChats;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d4_3a04.AccountManager.LoginPage;
import com.example.d4_3a04.DataTypes.ChatInfo;
import com.example.d4_3a04.R;
import com.example.d4_3a04.SingleChatProvider.SingleChatManager;
import com.example.d4_3a04.database.Cryptosystem;
import com.example.d4_3a04.databinding.BrowseActiveChatBinding;
import com.example.d4_3a04.databinding.RequestsBinding;

import java.util.List;

public class BrowseRequests extends AppCompatActivity {

    private RequestsBinding binding;
    String employee_id;

    SingleChatManager provider;

    SearchFor searchProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        this.employee_id = intent.getStringExtra("Employee_id");

        // Starting the database connection.
        Cryptosystem.startDB(BrowseRequests.this);

        List<String> other_employees = Cryptosystem.getRequests(employee_id);


        binding = RequestsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        LinearLayout layout = (LinearLayout) findViewById(R.id.browse_requests);


        for (String other_employee: other_employees){
            createButton(other_employee, layout);
        }

        BrowseActiveChatBinding.inflate(getLayoutInflater());

        binding.requestsBackBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BrowseRequests.this, BrowseActiveChats.class);
                intent.putExtra("Employee_id", employee_id);
                BrowseRequests.this.startActivity(intent);
            }
        });

    }

    public void createButton(String other_employee, LinearLayout layout){

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(32, 32, 32, 32);

        LinearLayout topRowLayout = new LinearLayout(this);
        topRowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        topRowLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        ));

        textView.setText(other_employee);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(Color.BLACK);

        Button accept = new Button(this);
        accept.setText("A");
        accept.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        accept.setTextColor(Color.WHITE);
        accept.setBackgroundColor(Color.BLUE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.leftMargin = 16;
        accept.setLayoutParams(layoutParams);

        Button reject = new Button(this);
        reject.setText("R");
        reject.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        reject.setTextColor(Color.WHITE);
        reject.setBackgroundColor(Color.BLUE);
        reject.setLayoutParams(layoutParams);

        topRowLayout.addView(textView);
        topRowLayout.addView(accept);
        topRowLayout.addView(reject);

        rootLayout.addView(topRowLayout);


        layout.addView(rootLayout);


        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the stored provider for given employee
                createConvo(other_employee);
                Cryptosystem.removeRequest(other_employee, employee_id);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the stored provider for given employee
                Cryptosystem.removeRequest(other_employee, employee_id);

                Intent new_view = new Intent(BrowseRequests.this, BrowseActiveChats.class);

                new_view.putExtra("Employee_id", employee_id);
                startActivity(new_view);
            }
        });




    }

    private void createConvo(String other_employee){
        ChatInfo info = new ChatInfo();
        SingleChatManager provider = new SingleChatManager(employee_id, other_employee, info);

        Cryptosystem.updateProvider(provider, employee_id, other_employee);
        Cryptosystem.updateProvider(provider, other_employee, employee_id);

        Intent new_view = new Intent(BrowseRequests.this, BrowseActiveChats.class);

        new_view.putExtra("Employee_id", employee_id);
        startActivity(new_view);
    }
}
