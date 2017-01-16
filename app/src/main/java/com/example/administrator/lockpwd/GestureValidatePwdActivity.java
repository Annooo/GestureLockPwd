package com.example.administrator.lockpwd;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.lockpwd.view.DrawLine;
import com.example.administrator.lockpwd.view.GestureContentView;

/**
 * Created by Administrator on 2017/1/10.
 * 手势密码校验的页面
 */

public class GestureValidatePwdActivity extends AppCompatActivity {


    //手势密码控件的容器
    private FrameLayout gesture_validate_container ;
    private GestureContentView gestureContentView;
    //从本地取出来的密码
    private String pwd = "";
    //密码提示文字
    private TextView gesture_validate_prompt;

    /**
     * 初始化界面的view
     */
    public void initView(){
        gesture_validate_container = (FrameLayout) findViewById(R.id.gesture_validate_container);
        gesture_validate_prompt = (TextView) findViewById(R.id.gesture_validate_prompt);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_validate);

        //初始化view
        initView();
        //从本地取出密码
        reStorePwd();

        gestureContentView = new GestureContentView(this, new DrawLine.GesturePwdCallBack() {
            @Override
            public void onGesturePwdInput(String inputPwd) {
                Log.i("pwdfesfse"," inputPwd = "+inputPwd +" pwd = "+pwd);
                if(pwd.equals(inputPwd)){
                    //如果密码一致提示用户校验成功，结束本页面
                    Toast.makeText(GestureValidatePwdActivity.this, "校验成功！", Toast.LENGTH_LONG).show();
                    GestureValidatePwdActivity.this.finish();
                }else{
                    //密码错误
                    gesture_validate_prompt.setText(getResources().getString(R.string.pwd_input_error));
                    Animation animation = AnimationUtils.loadAnimation(GestureValidatePwdActivity.this,R.anim.gesture_error_shake);
                    gesture_validate_prompt.startAnimation(animation);
                    gestureContentView.clearDrawLine(1100L);
                }
            }
        });
        gestureContentView.setData(gesture_validate_container);
    }
    /**
     * 取出存储到本地的密码
     */
    public void reStorePwd(){
        SharedPreferences sharedPreferences = getSharedPreferences("gesture_pwd", Activity.MODE_PRIVATE);
        //取出本地的密码
        pwd = sharedPreferences.getString("pwdstr","1");
    }
}
