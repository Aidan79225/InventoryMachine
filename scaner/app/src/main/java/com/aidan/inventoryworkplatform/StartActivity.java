package com.aidan.inventoryworkplatform;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.aidan.inventoryworkplatform.FragmentManager.FragmentManagerActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static com.aidan.inventoryworkplatform.KeyConstants.data;
import static com.aidan.inventoryworkplatform.KeyConstants.isLogin;

/**
 * Created by Aidan on 2017/3/30.
 */

public class StartActivity extends AppCompatActivity {
    private ActivityJumpTimer timer;
    private static final int ACTIVITYJUMP_DELAY = 300;
    private static final int REQUEST_PHONE_STATE = 0x1;
    private SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        timer = new ActivityJumpTimer(this);
        timer.sendEmptyMessageDelayed(ActivityJumpTimer.JUMP_TO_HOMEACTIVITY, ACTIVITYJUMP_DELAY);

    }
    @Override
    public void onResume(){
        super.onResume();
    }
    private void start(){
        int permission = ActivityCompat.checkSelfPermission(this,
                CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{CAMERA},
                    REQUEST_PHONE_STATE
            );
            return;
        }
            //已有權限
        action();
    }
    private void action(){
        if(checkLogin()){
            gotoFragmentManagerActivity();
        } else {
            showLoginDialog();
        }
    }

    private boolean checkLogin(){
        settings = getSharedPreferences(data,0);
        return settings.getBoolean(isLogin,false);
    }

    private void showLoginDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("請輸入認證碼");
        final EditText editText = new EditText(this);
        editText.setGravity(Gravity.CENTER);
        editText.setHint("請輸入認證碼");
        builder.setView(editText);
        builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(KeyConstants.key.equals(editText.getText().toString())){
                    settings = getSharedPreferences(data,0);
                    settings.edit().putBoolean(isLogin,true).apply();
                    gotoFragmentManagerActivity();
                }else{
                    showCancelDialog();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }
    private void showCancelDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("無法開啟");
        builder.setMessage("程式鎖定，請洽詢管理人員");
        builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    private void gotoFragmentManagerActivity(){
        Intent intent = new Intent();
        intent.setClass(this, FragmentManagerActivity.class);
        startActivity(intent);
        finish();
    }
    private class ActivityJumpTimer extends Handler {
        private static final int JUMP_TO_HOMEACTIVITY = 0x0000;
        private StartActivity activity;

        private ActivityJumpTimer(StartActivity activity) {
            this.activity = activity;
        }

        public void handleMessage(Message msg) {
            if (msg.what == JUMP_TO_HOMEACTIVITY) {
                //startVersionCheck(activity);
                start();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //取得權限，進行檔案存取
                    start();
                }else{
                    showCancelDialog();
                }
                return;
        }
    }

}
