package ching.android_googlecalendar_outlookcalender_sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;


/**
 * Created by book871181 on 16/6/11.
 */
public class PopUpOutLook {
    final static String TAG = "PopUp";
    Context mContext;
    EditText et_account;
    EditText et_password;
    SharedPreferences sp_outlook;
    MainActivity mMainActivity;
    PopupWindow window;

    public PopUpOutLook(Context mContext, MainActivity mMainActivity){
        this.mContext = mContext;
        this.mMainActivity = mMainActivity;
        sp_outlook = mContext.getSharedPreferences(
                "account", Context.MODE_PRIVATE);
    }

    public void PopUp(){

        View view = View.inflate(mContext, R.layout.pouup_outlook, null);
        window = new PopupWindow(mContext);
        window.setContentView(view);
        window.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);


        window.setFocusable(true);
        window.setAnimationStyle(R.style.AnimationPreview);
        window.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 0);



        et_account = (EditText)view.findViewById(R.id.et_account);
        et_password = (EditText)view.findViewById(R.id.et_password);





        String account  = sp_outlook.getString("outlookAccount","");
        et_account.setText(account);
        String password  = sp_outlook.getString("outlookPassword","");
        et_password.setText(password);

        Button button_add = (Button)view.findViewById(R.id.button_save);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Click();

            }
        });

    }
    private void Click(){
        if(!isEmpty(et_account)  && !isEmpty(et_password) ){
            Log.d(TAG,"dismiss:" +  isEmpty(et_password) + "account" + isEmpty(et_account)  );
            String account = et_account.getText().toString();
            String password = et_password.getText().toString();

            SharedPreferences.Editor editor = sp_outlook.edit();
            editor.putString("outlookAccount",account);
            editor.putString("outlookPassword", password);
            editor.commit();

            window.dismiss();


        }else {
            Toast.makeText(mContext.getApplicationContext(),"Please fill your information",Toast.LENGTH_SHORT).show();


        }
    }
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

}
