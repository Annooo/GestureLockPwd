package com.example.administrator.lockpwd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;


/**
 * 自己实现的手势密码锁
 */

public class MainActivity extends AppCompatActivity {

    //
    private MyOnClickListener onClickListener = new MyOnClickListener(this);
    //设置密码的按钮
    private Button activity_main_setgesture_pwd;
    //校验密码的按钮
    private Button activity_main_verfiy_pwd;


    /**
     * 初始化页面的view
     */
    private void initView() {
        activity_main_setgesture_pwd = (Button) findViewById(R.id.activity_main_setgesture_pwd);
        activity_main_verfiy_pwd = (Button) findViewById(R.id.activity_main_verfiy_pwd);
    }

    /**
     * 初始化页面的点击事件
     */
    private void initClickEvent() {
        activity_main_setgesture_pwd.setOnClickListener(onClickListener);
        activity_main_verfiy_pwd.setOnClickListener(onClickListener);
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化页面的view
        initView();

        //设置点击事件
        initClickEvent();

    }


    private static class MyOnClickListener implements View.OnClickListener {

        //使用弱引用 来引用传入的上下文对象，防止MainActivity 要被回收的时候本静态类还持有他的引用，导致MainActivity不能被回收 导致内存泄漏
        private WeakReference<MainActivity> contextWeakReference;

        public MyOnClickListener(MainActivity context) {
            this.contextWeakReference = new WeakReference<MainActivity>(context);
        }

        /**
         * 把内部点击事件监听类设为 静态 防止内存泄漏
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            MainActivity context = contextWeakReference.get();
            if (context != null) {
                switch (v.getId()) {
                    case R.id.activity_main_setgesture_pwd://如果是设置手势密码的事件，那就跳转到设置手势密码的页面
                        intoPageSetGesturePwd(context);
                        break;
                    case R.id.activity_main_verfiy_pwd://如果是校验手势密码 那就执行跳转到校验手势密码的页面
                        intoPageVerfiyGesturePwd(context);
                        break;
                }
            }
        }
    }
    /**
     * 跳转到设置手势密码的页面
     */
    private static void intoPageSetGesturePwd(MainActivity context){
        Intent intent = new Intent(context, GestureInputActivity.class);
        context.startActivity(intent);
    }
    /**
     * 跳转到校验手势密码的页面
     */
    private static void intoPageVerfiyGesturePwd(MainActivity context){
        Intent intent = new Intent(context,GestureValidatePwdActivity.class );
        context.startActivity(intent);
    }
}
