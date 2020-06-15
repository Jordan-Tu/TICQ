package com.tu.curriculumdesign.util;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.tu.curriculumdesign.R;

public class PasswordVisibilityTool {
    public static void addVisibilityListener(final EditText et , final ImageView iv){
        et.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    iv.setVisibility(View.VISIBLE);
                }else{
                    iv.setVisibility(View.INVISIBLE);
                }
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){//判断密码可见性
                    et.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD );
                    iv.setImageResource(R.mipmap.ic_notvisible);
                }else{
                    et.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iv.setImageResource(R.mipmap.ic_browse);
                }
                et.setSelection(et.getText().length()); //焦点移到最后
            }
        });
    }
}
