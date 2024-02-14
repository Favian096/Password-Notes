package com.passwordnotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.passwordnotes.dao.Account;
import com.passwordnotes.dao.AccountMapper;
import com.passwordnotes.utils.toaster.Toaster;

public class FastAddItemActivity extends AppCompatActivity {
    AccountMapper accountMapper;

    /*add_form_控件*/
    private View addFormComponents;
    private RadioGroup weightRadioGroup;
    private EditText tagEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    EditText remarkEditText;
    private Button formCancel;
    private Button formConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_add_item);
        initData();
        basicOnClickHandler();
        // tag获取焦点
        tagEditText.requestFocus();
    }

    /**
     * 处理点击事件
     */
    @SuppressLint("NonConstantResourceId")
    private void basicOnClickHandler() {
        // 取消按钮
        formCancel.setOnClickListener(
                v -> {
                    Toast.makeText(FastAddItemActivity.this, "取消操作", Toast.LENGTH_SHORT).show();
                    clearInputFormMsg();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(InputMethodManager.class);
                    if (null != imm && imm.isActive()) // 隐藏键盘
                        imm.hideSoftInputFromWindow(addFormComponents.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    onBackPressed();
                }
        );
        // 确认按钮
        formConfirm.setOnClickListener(
                v -> {
                    int weight = -1;
                    switch (weightRadioGroup.getCheckedRadioButtonId()) {
                        case R.id.add_form_weight_radio_extreme:
                            weight = 0;
                            break;
                        case R.id.add_form_weight_radio_high:
                            weight = 1;
                            break;
                        case R.id.add_form_weight_radio_medium:
                            weight = 2;
                            break;
                        case R.id.add_form_weight_radio_low:
                            weight = 3;
                            break;
                        default:
                            break;
                    }
                    if (-1 == weight) {
                        Toaster.warm("请选择重要性！");
                        return;
                    }
                    String tag = tagEditText.getText().toString();
                    if (tag.isEmpty()) {
                        Toaster.warm("标签是必填选项！");
                        return;
                    }
                    String name = nameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String remark = remarkEditText.getText().toString();
                    Account newAccount = new Account(
                            tag,
                            name,
                            password,
                            remark,
                            weight,
                            System.currentTimeMillis(),
                            0,
                            1
                    );
                    if (!accountMapper.saveAccount(newAccount)) {
                        Toast.makeText(FastAddItemActivity.this, "ERROR!数据添加失败!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(FastAddItemActivity.this, tag + " 添加成功!", Toast.LENGTH_SHORT).show();
                    clearInputFormMsg();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(InputMethodManager.class);
                    if (null != imm && imm.isActive()) // 隐藏键盘
                        imm.hideSoftInputFromWindow(addFormComponents.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    onBackPressed();
                }
        );

    }

    /**
     * 清空输入和focus
     */
    private void clearInputFormMsg() {
        weightRadioGroup.clearCheck();
        tagEditText.setText(null);
        nameEditText.setText(null);
        passwordEditText.setText(null);
        remarkEditText.setText(null);
        tagEditText.clearFocus();
        nameEditText.clearFocus();
        passwordEditText.clearFocus();
        remarkEditText.clearFocus();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        accountMapper = new AccountMapper(this);
        addFormComponents = findViewById(R.id.addFormComponents);
        weightRadioGroup = findViewById(R.id.add_form_weight_radio_group);
        tagEditText = findViewById(R.id.add_form_tag_edit_text);
        nameEditText = findViewById(R.id.add_form_name_edit_text);
        passwordEditText = findViewById(R.id.add_form_password_edit_text);
        remarkEditText = findViewById(R.id.add_form_remark_edit_text);
        formCancel = findViewById(R.id.add_form_button_cancel);
        formConfirm = findViewById(R.id.add_form_button_confirm);
    }
}