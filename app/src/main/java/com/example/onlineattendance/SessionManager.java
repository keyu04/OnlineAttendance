package com.example.onlineattendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    public static final String PREF_NAME = "LOGIN";
    public static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public SharedPreferences.Editor editor;
    public Context context;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }

    public void createSession(String Name) {
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME,Name);
        editor.apply();
    }
    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }
    public void checkLogin()
    {
        if(!this.isLoggin())
        {
            Intent i=new Intent(context,MainActivity.class);
            context.startActivity(i);
            ((Dashboard)context).finish();
        }
    }
    public HashMap<String,String>  FacultyDetail()
    {
        HashMap<String,String> Faculty=new HashMap<>();
        Faculty.put(NAME,sharedPreferences.getString(NAME,null));
        return Faculty;
    }
    public void logout()
    {
        editor.clear();
        editor.commit();
        Intent i=new Intent(context,MainActivity.class);
        context.startActivity(i);
        ((Dashboard)context).finish();
    }
}
