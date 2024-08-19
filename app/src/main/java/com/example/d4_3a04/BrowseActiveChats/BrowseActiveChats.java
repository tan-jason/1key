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

import androidx.appcompat.app.AppCompatActivity;

import com.example.d4_3a04.AccountManager.LoginPage;
import com.example.d4_3a04.R;
import com.example.d4_3a04.DataTypes.ChatInfo;
import com.example.d4_3a04.SingleChatProvider.SingleChatManager;
import com.example.d4_3a04.database.Cryptosystem;
import com.example.d4_3a04.databinding.BrowseActiveChatBinding;

import java.util.List;

public class BrowseActiveChats extends AppCompatActivity {

    private BrowseActiveChatBinding binding;
    String employee_id;
    List<String> other_employee;

    SingleChatManager provider;

    SearchFor searchProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        this.employee_id = intent.getStringExtra("Employee_id");

        // Starting the database connection.
        Cryptosystem.startDB(BrowseActiveChats.this);

        other_employee = Cryptosystem.getOtherEmployee(this.employee_id);

        binding = BrowseActiveChatBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarActiveChat);

        LinearLayout layout = (LinearLayout) findViewById(R.id.all_chats);


        for (String other_employee: other_employee){
            createButton(other_employee, layout);
        }

        BrowseActiveChatBinding.inflate(getLayoutInflater());

        binding.enterSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BrowseActiveChats.this, SearchFor.class);
                intent.putExtra("this_employee", employee_id);
                BrowseActiveChats.this.startActivity(intent);
            }
        });

        binding.checkRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BrowseActiveChats.this, BrowseRequests.class);
                intent.putExtra("Employee_id", employee_id);
                BrowseActiveChats.this.startActivity(intent);
            }
        });

    }

    public void createButton(String employee_name, LinearLayout layout){
        Button btnTag = new Button(this);

        // A bunch of button layout styling
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        int marginVertical = getResources().getDimensionPixelSize(R.dimen.button_vertical_margin);
        layoutParams.setMargins(0, marginVertical, 0, marginVertical);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        btnTag.setLayoutParams(layoutParams);

        btnTag.setBackgroundResource(R.drawable.chat_bubble);

        int paddingStartEnd = getResources().getDimensionPixelSize(R.dimen.button_horizontal_padding);
        int paddingTopBottom = getResources().getDimensionPixelSize(R.dimen.button_vertical_padding);
        btnTag.setPadding(paddingStartEnd, paddingTopBottom, paddingStartEnd, paddingTopBottom);

        btnTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        btnTag.setTextColor(Color.BLACK);

        // End of button styling.
        btnTag.setText(employee_name);
        btnTag.setTag("enter_convo");

        //add button to the layout
        layout.addView(btnTag);

        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the stored provider for given employee

                List<String> res = Cryptosystem.getProvider(employee_id, employee_name);

                // Only creating a new provider if there is no provider for the employee (res.size()=0).
                if (res.size()>0){
                    provider = SingleChatManager.deserializeFromJson(res.get(0));
                    provider.set_employee(employee_id, employee_name);
                }else{
                    ChatInfo chat_info = new ChatInfo();
                    provider = new SingleChatManager(employee_id, employee_name, chat_info);;
                    Cryptosystem.updateProvider(provider,employee_id, employee_name);
                }

                // Open that conversation using given provider.
                provider.inflate_page_source(BrowseActiveChats.this);

                // Disconnect from the database.
                Cryptosystem.disconnectDB();
            }
        });

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void inflateLogin(){
        Intent intent = new Intent(BrowseActiveChats.this, LoginPage.class);
        this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.

        if (item.getItemId()==R.id.log_out){
            inflateLogin();
            return true;
        }else{
            return false;
        }
    }
}
