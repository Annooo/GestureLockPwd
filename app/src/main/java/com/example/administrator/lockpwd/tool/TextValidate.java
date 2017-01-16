package com.example.administrator.lockpwd.tool;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/1/9.
 * 检验内容工具类
 */

public class TextValidate {

    /**
     * 校验输入的密码是否有四位
     * @param inputText
     * @return
     */
    public static boolean checkInputText(String inputText){
        if(TextUtils.isEmpty(inputText) || inputText.getBytes().length < 4){
            return false;
        }
        return true;
    }
}
