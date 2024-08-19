package com.example.d4_3a04.SingleChatProvider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d4_3a04.DataTypes.ChatInfo;
import com.example.d4_3a04.DataTypes.LogEntity;
import com.example.d4_3a04.R;

import java.util.List;

// Code is adapted from https://sendbird.com/developer/tutorials/android-chat-tutorial-building-a-messaging-ui
public class MessageListAdapter extends RecyclerView.Adapter{

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private ChatInfo chat_info;
    private Context context;
    private List<LogEntity> messages;
    private String employee_id;

    public MessageListAdapter(Context context, ChatInfo chat_info, String employee_id){
        this.context = context;
        this.chat_info = chat_info;

        this.messages = chat_info.getLog_history();
        this.employee_id = employee_id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_text, parent, false);
            return new SentMessageHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recieved_text, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LogEntity message = this.messages.get(position);


        switch(holder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    public int getItemViewType(int position){
        LogEntity message =this.messages.get(position);

        if (message.employee_id.equals(this.employee_id)){
            return VIEW_TYPE_MESSAGE_SENT;
        }else{
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }
}

class SentMessageHolder extends RecyclerView.ViewHolder{
    TextView messageText, timeText, dateText;

    SentMessageHolder(View itemView){
        super(itemView);

        messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_me);
        timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_me);
        dateText = (TextView) itemView.findViewById(R.id.text_date_me);
    }

    void bind(LogEntity message){
        messageText.setText(message.message);
        timeText.setText(message.time);
        dateText.setText(message.date);
    }
}

class ReceivedMessageHolder extends RecyclerView.ViewHolder{
    TextView messageText, timeText, nameText, dateText;

    ReceivedMessageHolder(View itemView){
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_gchat_message_other);
        timeText = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_other);
        nameText = (TextView) itemView.findViewById(R.id.text_gchat_user_other);
        dateText = (TextView) itemView.findViewById(R.id.text_date_other);
    }

    void bind(LogEntity message){
        messageText.setText(message.message);
        timeText.setText(message.time);
        nameText.setText(message.employee_id);
        dateText.setText(message.date);
    }


}
