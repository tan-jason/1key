package com.example.d4_3a04.SingleChatProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d4_3a04.BrowseActiveChats.BrowseActiveChats;
import com.example.d4_3a04.DataTypes.ChatInfo;
import com.example.d4_3a04.DataTypes.LogEntity;
import com.example.d4_3a04.GPT;
import com.example.d4_3a04.R;
import com.example.d4_3a04.database.Cryptosystem;
import com.example.d4_3a04.databinding.SingleChatProviderBinding;


// Code is adapted from https://sendbird.com/developer/tutorials/android-chat-tutorial-building-a-messaging-ui
public class MessageListActivity extends AppCompatActivity {

    // SCM instance for the page.
    private SingleChatManager provider;
    private RecyclerView MessageRecycler;
    private MessageListAdapter MessageAdapter;
    private SingleChatProviderBinding binding;

    // Chat info object for the page. Derived from the SCM.
    private ChatInfo chat_info;

    // Chat session logged in employee id.
    // Don't need the other employee's id since LogEntity object only takes the id of the person sending it.
    private String employee_id;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Starts the database connection.
        Cryptosystem.startDB(MessageListActivity.this);

        binding = SingleChatProviderBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Intent intent = getIntent();

        this.employee_id = intent.getStringExtra("Employee_id");

        this.provider = SingleChatManager.deserializeFromJson(intent.getStringExtra("SCM"));
        this.chat_info = provider.chat_info;


        this.MessageRecycler = (RecyclerView) findViewById(R.id.recycler_gchat);
        this.MessageAdapter = new MessageListAdapter(this, chat_info, this.employee_id);

        this.MessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        this.MessageRecycler.setAdapter(this.MessageAdapter);

        // Load all the chat messages.
        loadChat();


        // On click handler for when user wants to send the message.
        binding.buttonGchatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText t = (EditText) findViewById(R.id.edit_gchat_message);

                String message = t.getText().toString();

                // Update chat log with provided message.
                int size = provider.updateChatLog(message, employee_id);

                MessageAdapter.onBindViewHolder(MessageAdapter.onCreateViewHolder(findViewById(R.id.recycler_gchat), 1), size-1);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(MessageRecycler.getWindowToken(), 0);

                t.getText().clear();

                if (provider.other_employee.equals("GPT")){
                    try{
                        String res = GPT.chatGPT(message);
                        int other_size = provider.updateChatLog(res, provider.other_employee);

                        MessageAdapter.onBindViewHolder(MessageAdapter.onCreateViewHolder(findViewById(R.id.recycler_gchat), 2), other_size-1);
                    } catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }


            }
        });

        // On Click handler for when user decides to go back.
        binding.toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cryptosystem.updateProvider(provider, employee_id, provider.other_employee);
                Cryptosystem.updateProvider(provider, provider.other_employee, employee_id);
                Intent intent = new Intent(MessageListActivity.this, BrowseActiveChats.class);

                intent.putExtra("Employee_id", employee_id);
                MessageListActivity.this.startActivity(intent);
                Cryptosystem.disconnectDB();
            }
        });

    }

    public void loadChat(){
        for (int i=0; i<this.chat_info.getLog_history().size(); i++){
            LogEntity entity = this.chat_info.getLog_history().get(i);
            if (entity.employee_id.equals(this.employee_id)){
                MessageAdapter.onBindViewHolder(MessageAdapter.onCreateViewHolder(findViewById(R.id.recycler_gchat), 1), i);
            } else {
                MessageAdapter.onBindViewHolder(MessageAdapter.onCreateViewHolder(findViewById(R.id.recycler_gchat), 2), i);
            }
        }

    }




}
