package ching.android_googlecalendar_outlookcalender_sync.Utils;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by book871181 on 16/8/7.
 */
public class SavingSharedPreferences {

    SharedPreferences sp;
    SharedPreferences.Editor editor ;

    public SavingSharedPreferences(SharedPreferences sp){
        this.sp = sp;
        editor = sp.edit();
    }
    public void setOutlookPassword(String password){
        editor.putString("outlookPassword", password);
        editor.apply();
    }

    public void setOutlookAccount(String account){
        editor.putString("outlookAccount", account);
        editor.apply();

    }
    public void setGoogleAccount(String account){
        editor.putString("googleAccount", account);
        editor.apply();
    }
    public void setLeastMessage(List<String> list){

        Set<String> set = new HashSet<String>();
        set.addAll(list);
        editor.putStringSet("oldMessage", set);
        editor.apply();

    }

    public String getOutlookPassword(){
        String password = sp.getString("outlookPassword", null);


        return password;
    }
    public String getOutlookAccount(){
        String account = sp.getString("outlookAccount", null);


        return account;
    }
    public String getGoogleAccount(){
        String account = sp.getString("googleAccount", null);
        return account;

    }
    public List<String> getAllMessage(){

        Set<String> set = sp.getStringSet("oldMessage", null);
        List<String> message = new ArrayList<String>();

        if(set != null) {
            message.addAll(set);
        }else{
            return null;
        }
        return  message;
    }


}
