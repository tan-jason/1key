package com.example.d4_3a04.DataTypes;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogEntity implements Serializable {
    private static final long serialVersionUID = -3727220539856468472L;
    public String employee_id;
    public String message;
    public String date;
    public String time;
    public String key;



    public LogEntity(String employee_id, String message, Date date, String key){
        this.employee_id = employee_id;
        this.message = message;
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        SimpleDateFormat tf = new SimpleDateFormat("KK:mm:ss", Locale.getDefault());
        this.date = df.format(date);
        this.time = tf.format(date);
        this.key = key;
    }
}
