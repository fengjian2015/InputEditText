package com.fickle.input.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * 账号密码校验规则
 */
public class CheckUtil {
    public static boolean isInputtingMobile(String currInputStr){
        if (TextUtils.isEmpty(currInputStr))return false;
        String regExp="^((13|14|15|16|17|18|19)+\\d{9})$";
        return Pattern.matches(regExp,currInputStr);
    }

    public static boolean isInputtingUsername(String currInputStr){
        if (TextUtils.isEmpty(currInputStr))return false;
        String regExp="^[f|F][a-zA-Z0-9]{4,9}$";
        return Pattern.matches(regExp,currInputStr);
    }

    public static boolean isInputtingPwd(String currInputStr){
        if (TextUtils.isEmpty(currInputStr)|| currInputStr.length()<8)return false;
        Pattern startsWith=Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9]+$");
        Pattern d=Pattern.compile("[0-9]+?");
        Pattern zpat=Pattern.compile("[a-zA-Z]+?");
        Pattern down=Pattern.compile("[_]+?");
        boolean isname=false;
        int num=0;
        if (d.matcher(currInputStr).find()){
            num++;
        }
        if (zpat.matcher(currInputStr).find()){
            num++;
        }
        if (down.matcher(currInputStr).find()){
            num++;
        }
        if (num>1){
            isname=true;
        }

        return startsWith.matcher(currInputStr).matches()&&true;
    }

    public static boolean isInputtingCode(String currInputStr){
        if (TextUtils.isEmpty(currInputStr))return false;
        if (currInputStr.length()==6){
            return true;
        }else {
            return false;
        }
    }
}
