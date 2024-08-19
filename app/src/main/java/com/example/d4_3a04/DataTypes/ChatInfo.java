package com.example.d4_3a04.DataTypes;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatInfo implements InfoEntity, Serializable {
    private static final long serialVersionUID = -3727220539856468472L;
    private List<String> employee_ids;
    private List<LogEntity> log_history;


    public ChatInfo(){
        this.employee_ids = new ArrayList<>();
        this.log_history = new ArrayList<LogEntity>();
    }

    @Override
    public InfoType getInfoType() {
        return InfoType.Log;
    }

    public List<String> getEmployee_ids(){
        return this.employee_ids;
    }

    public List<LogEntity> getLog_history(){
        return this.log_history;
    }

    public void addLog(LogEntity entity){
        log_history.add(entity);
    }

    public void addUser(String UUID){
        employee_ids.add(UUID);
    }

}
