package com.fickle.input.annotation;

import android.support.annotation.StringDef;

@StringDef({
        CheckType.PWD_ERROR,
        CheckType.PWD_SUCCESS,
        CheckType.CODE_ERROR,
        CheckType.CODE_SUCCESS,
        CheckType.PHONE_ERROR,
        CheckType.PHONE_SUCCESS,
        CheckType.NAME_ERROR,
        CheckType.NAME_SUCCESS,
        CheckType.NONE
})

public @interface CheckType {
    String PWD_SUCCESS="pwd_success";
    String PWD_ERROR="pwd_error";
    String CODE_SUCCESS="code_success";
    String CODE_ERROR="code_error";
    String PHONE_SUCCESS="phone_success";
    String PHONE_ERROR="phone_error";
    String NAME_SUCCESS="name_success";
    String NAME_ERROR="name_error";
    String NONE="none";
}