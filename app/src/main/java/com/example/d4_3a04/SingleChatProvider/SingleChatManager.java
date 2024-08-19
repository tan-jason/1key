package com.example.d4_3a04.SingleChatProvider;

import android.content.Intent;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d4_3a04.DataTypes.ChatInfo;
import com.example.d4_3a04.DataTypes.LogEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SingleChatManager implements Serializable {
    // Necessary for correct serialization/deserialization.
    private static final long serialVersionUID = -3727220539856468472L;
    String this_employee;
    String other_employee;
    public ChatInfo chat_info;

    public SingleChatManager(String this_employee, String other_employee, ChatInfo chat_info){
        this.this_employee = this_employee;
        this.other_employee=other_employee;

        this.chat_info = chat_info;
    }

    public void set_employee(String this_employee, String other_employee){
        this.this_employee = this_employee;
        this.other_employee = other_employee;
    }

    // Updates the chat log with message and outputs how many logs there are in total.
    public int updateChatLog(String message, String employee){
        Date date_time = Calendar.getInstance().getTime();

        LogEntity entity = new LogEntity(employee, message, date_time, "2921902");
        chat_info.addLog(entity);

        return chat_info.getLog_history().size();
    }

    // Starts the MessageListActivity page.
    public void inflate_page_source(AppCompatActivity activity){
        Intent intent = new Intent(activity, MessageListActivity.class);

        // Sends the employee id and SCM object to the activity object.
        intent.putExtra("Employee_id", this.this_employee);
        intent.putExtra("SCM", SingleChatManager.serializeToJson(this));

        activity.startActivity(intent);
    }


    // Serializes the SCM object to be stored in the database or to be sent to the activity.
    public static String serializeToJson(SingleChatManager object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.close();
            return Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    // Deserializes the SCM object to get the SCM object back from database or activity intent.
    public static SingleChatManager deserializeFromJson(String jsonString) {
        try {
            byte[] data = Base64.decode(jsonString, Base64.DEFAULT);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            SingleChatManager object = (SingleChatManager) ois.readObject();
            ois.close();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}
