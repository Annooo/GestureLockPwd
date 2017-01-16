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

import com.example.administrator.lockpwd.tool.TextValidate;
import com.example.administrator.lockpwd.view.DrawLine;
import com.example.administrator.lockpwd.view.GestureContentView;
import com.example.administrator.lockpwd.view.GesturePrompt;

/**
 * Created by Administrator on 2017/1/5.
 * 手势密码设置界面
 */
public class GestureInputActivity extends AppCompatActivity {

    //手势密码的提示
    private TextView gesture_input_prompt;
    //手势密码的标题
    private TextView gesture_input_title;
    //手势密码自定义View
    private GestureContentView gesture_input_contentview;
    //手势密码的容器
    private FrameLayout gesture_container;
    //密码提示的view
    private GesturePrompt gesture_input_pwd_prompt;


    //第一次输入的密码
    private String firstPwd = "";
    //是否是第一次输入手势密码  缺省为ture
    private boolean isFirstInputPwd = true ;

    /**
     * 初始化页面的view
     */
    private void initView(){
        gesture_input_prompt = (TextView) findViewById(R.id.gesture_input_prompt);
        gesture_input_title = (TextView) findViewById(R.id.gesture_input_title);
        gesture_container = (FrameLayout) findViewById(R.id.gesture_container);
        gesture_input_pwd_prompt = (GesturePrompt) findViewById(R.id.gesture_input_pwd_prompt);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_input);
        initView();
        gesture_input_contentview = new GestureContentView(this,new DrawLine.GesturePwdCallBack() {
            @Override
            public void onGesturePwdInput(String inputPwd) {
                //校验密码的长度 是否够4位数
                if( !TextValidate.checkInputText(inputPwd) ){
                    //执行此处表示长度不够4个字节 就是四个数
                    gesture_input_prompt.setText(getResources().getString(R.string.pwd_length_error));
                    gesture_input_contentview.clearDrawLine(0L);
                    return ;
                }

                // 是否第一次输入手势密码
                if(isFirstInputPwd){
                    /**
                     * 表示是第一次输入手势密码
                     * 需要把第一次输入密码的标识设为false
                     * 记住第一次输入的密码
                     * 清除已经划过的密码的路径
                     * 提示用户重新输入一次密码
                     */
                    isFirstInputPwd = false;
                    firstPwd = inputPwd;
                    //执行密码提示的显示
                    updatePwdPrompt(firstPwd);
                    if( gesture_input_contentview != null){
                        gesture_input_contentview.clearDrawLine(0L);
                    }
                    gesture_input_prompt.setText(getResources().getString(R.string.pwd_reinput));
                }else{
                    /**
                     * 表示不是第一次输入密码
                     * 比较两次输入的密码是否一致
                     */
                    if( firstPwd.equals(inputPwd) ){
                        /**
                         * 两次输入的密码一致的话
                         * 提示设置成功
                         * 清除所有已经划过的线
                         * 结束本页面
                         */
                        Toast.makeText(GestureInputActivity.this, "设置成功！",Toast.LENGTH_LONG).show();
                        gesture_input_contentview.clearDrawLine(0L);
                        //把设置好的密码用sf保存到本地，校验的地方需要用
                        SharedPreferences sharedPreferences = getSharedPreferences("gesture_pwd", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pwdstr",inputPwd);
                        //提交数据保存
                        editor.commit();
                        Log.i("pwdfesfse"," firstPwd = "+firstPwd +" inputPwd = "+inputPwd);
                        GestureInputActivity.this.finish();
                    }else{
                        /**
                         * 两次密码不一样
                         * 提示两次密码不一致信息
                         * 提示信息开启抖动的动画
                         * 设定延迟时间清空所有的线
                         */
                        gesture_input_prompt.setText(getResources().getString(R.string.pwd_reinput_error));
                        Animation animation = AnimationUtils.loadAnimation(GestureInputActivity.this,R.anim.gesture_error_shake);
                        gesture_input_prompt.startAnimation(animation);
                        gesture_input_contentview.clearDrawLine(1100L);
                    }
                }
            }
        });
        gesture_input_contentview.setData(gesture_container);
        //执行密码提示的初始化
        updatePwdPrompt(firstPwd);
    }
    /**
     * 更新密码提示
     */
    public void updatePwdPrompt(String pwdStr){
        gesture_input_pwd_prompt.setPromptPwd(pwdStr);
    }
}
