package com.fickle.inputedittext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fickle.input.annotation.CheckType;
import com.fickle.input.view.InputEditText;

public class MainActivity extends AppCompatActivity {
    private InputEditText code;
    private boolean codeInput=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        code=findViewById(R.id.code);
        code.setOnInputEditxtListener(new InputEditText.OnInputEditTxtListener() {
            @Override
            public void checkResult(String type) {
                switch (type){
                    case CheckType.CODE_ERROR:
                        codeInput=false;
                        break;
                    case CheckType.CODE_SUCCESS:
                        codeInput=true;
                        break;
                }
            }
        });
        code.setOnSendCodeListener(new InputEditText.OnSendCodeListener() {
            @Override
            public boolean send() {
                return codeInput;
            }
        });
    }

    @Override
    protected void onDestroy() {
        code.onDestroy();
        super.onDestroy();
    }
}
