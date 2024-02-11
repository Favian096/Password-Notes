package com.passwordnotes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.passwordnotes.dao.Account;
import com.passwordnotes.dao.AccountMapper;
import com.passwordnotes.ui.Dialog;
import com.passwordnotes.utils.toaster.Toaster;

import java.util.ArrayList;
import java.util.List;

public class TextParseActivity extends AppCompatActivity {
    private AccountMapper accountMapper;
    private List<Account> accounts;
    private EditText editText;
    private Button promptBtn;
    private Button submitBtn;
    String msg;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_parse);
        initData();
        initLayout();
        basicOnclickHandler();

    }

    @SuppressLint("ResourceAsColor")
    private void basicOnclickHandler() {
        //    提示
        this.promptBtn.setOnClickListener(
                v -> {
                    editText.setText(getResources().getString(R.string.text_parse_prompt_text));
                }
        );
        //    解析
        this.submitBtn.setOnClickListener(
                v -> {
                    accounts.clear();
                    editText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(InputMethodManager.class);
                    if (imm.isActive()) // 隐藏键盘
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    String inputText = editText.getText().toString();
                    if (!inputText.isEmpty()) {
                        String[] textLines = inputText.split("\\r?\\n");
                        for (int lineIndex = 0; lineIndex < textLines.length; lineIndex++) {
                            Account account = new Account(3, System.currentTimeMillis(), 0, 1);
                            if (!textLines[lineIndex].isEmpty()) {
                                account.setTag(textLines[lineIndex++]);
                            } else {
                                continue;
                            }
                            if (lineIndex < textLines.length)
                                if (!textLines[lineIndex].isEmpty()) {
                                    account.setName(textLines[lineIndex++]);
                                } else {
                                    this.accounts.add(account);
                                    continue;
                                }
                            if (lineIndex < textLines.length)
                                if (!textLines[lineIndex].isEmpty()) {
                                    account.setPassword(textLines[lineIndex]);
                                }
                            this.accounts.add(account);
                        }
                        msg = "标签如下: ";
                        accounts.forEach(account -> {
                            if (account.equals(accounts.get(accounts.size() - 1))) {
                                msg += account.getTag();
                            } else {
                                msg += account.getTag() + "、";
                            }
                        });

                        Dialog.show(this,
                                "内容解析共" + accounts.size() + "组数据",
                                msg
                                , "提交", (dialog, which) -> accountMapper.saveAccountBatch(accounts),
                                "取消", (dialog, which) -> {
                                });
                    } else {
                        Toaster.info("没有内容可解析!");
                    }

                }
        );

    }

    private void initLayout() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Enable 自定义的 View
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            // 绑定自定义的布局
            actionBar.setCustomView(R.layout.custom_bar_text_parse);
            actionBar.setElevation(1);
        }

    }

    private void initData() {
        this.accountMapper = new AccountMapper(this);
        this.accounts = new ArrayList<>();
        this.editText = findViewById(R.id.text_parse_edit_text);
        this.promptBtn = findViewById(R.id.text_parse_prompt_button);
        this.submitBtn = findViewById(R.id.text_parse_submit_button);

    }
}