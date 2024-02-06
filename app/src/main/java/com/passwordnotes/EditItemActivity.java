package com.passwordnotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.passwordnotes.dao.Account;
import com.passwordnotes.dao.AccountMapper;

import java.text.SimpleDateFormat;
import java.util.Date;


public class EditItemActivity extends AppCompatActivity {
    private AccountMapper accountMapper;
    private Account account;
    private int id;
    private int position;
    private TextView id_text;
    private TextView time_text;
    private RadioGroup edit_radio_group;
    private RadioButton edit_radio_extreme;
    private RadioButton edit_radio_high;
    private RadioButton edit_radio_medium;
    private RadioButton edit_radio_low;
    private EditText edit_tag;
    private EditText edit_name;
    private EditText edit_password;
    private EditText edit_remark;
    private EditText edit_priority;
    private Button submitEdit;
    private Button recyclerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        initData();
        initLayout();
        basicOnClickHandler();
    }

    /**
     * 处理点击事件
     */
    @SuppressLint("NonConstantResourceId")
    private void basicOnClickHandler() {
        //    提交修改
        submitEdit.setOnClickListener(
                v -> {
                    clearEditFormFocus();
                    if (0 == id) {
                        Toast.makeText(this, "管理员数据无法修改!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int weight = account.getWeight();
                    switch (edit_radio_group.getCheckedRadioButtonId()) {
                        case R.id.edit_form_weight_radio_extreme:
                            weight = 0;
                            break;
                        case R.id.edit_form_weight_radio_high:
                            weight = 1;
                            break;
                        case R.id.edit_form_weight_radio_medium:
                            weight = 2;
                            break;
                        default:
                            weight = 3;
                    }
                    String tag = edit_tag.getText().toString();
                    if (tag.isEmpty()) {
                        Toast.makeText(this, "标签是必填选项！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String name = edit_name.getText().toString();
                    String password = edit_password.getText().toString();
                    String remark = edit_remark.getText().toString();
                    int priority = Integer.parseInt(edit_priority.getText().toString());
                    if (priority > 1024 || priority < 0) {
                        Toast.makeText(this, "优先级必须是0~1024之间的整数！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    accountMapper.updateAccount(new Account(
                            this.id,
                            tag,
                            name,
                            password,
                            remark,
                            weight,
                            System.currentTimeMillis(),
                            0,
                            priority
                    ));
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                }
        );

        //    执行回收
        recyclerBtn.setOnClickListener(
                v -> {
                    clearEditFormFocus();
                    if (0 == id) {
                        Toast.makeText(this, "管理员数据不允许删除!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    recyclerBtn.setTextColor(getColor(R.color.system_burgundy));
                    recyclerBtn.setText("确认删除");
                    recyclerBtn.setOnClickListener(
                            v2 -> {
                                accountMapper.recyclerAccount(id);
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.putExtra("id", id);
                                intent.putExtra("position", position);
                                setResult(RESULT_FIRST_USER, intent);
                                onBackPressed();
                            }
                    );
                }
        );
    }

    /**
     * 清除编辑页的输入focus
     */
    private void clearEditFormFocus() {
        edit_tag.clearFocus();
        edit_name.clearFocus();
        edit_password.clearFocus();
        edit_remark.clearFocus();
        edit_priority.clearFocus();
    }

    /**
     * 初始化页面数据
     */
    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void initData() {
        accountMapper = new AccountMapper(this);
        this.id = getIntent().getIntExtra("id", 0);
        this.position = getIntent().getIntExtra("position", 0);
        account = accountMapper.getAccount(id);
        id_text = findViewById(R.id.edit_form_id_text);
        time_text = findViewById(R.id.edit_form_time_text);
        edit_radio_group = findViewById(R.id.edit_form_weight_radio_group);
        edit_radio_extreme = findViewById(R.id.edit_form_weight_radio_extreme);
        edit_radio_high = findViewById(R.id.edit_form_weight_radio_high);
        edit_radio_medium = findViewById(R.id.edit_form_weight_radio_medium);
        edit_radio_low = findViewById(R.id.edit_form_weight_radio_low);
        edit_tag = findViewById(R.id.edit_form_tag_text);
        edit_name = findViewById(R.id.edit_form_name_text);
        edit_password = findViewById(R.id.edit_form_password_text);
        edit_remark = findViewById(R.id.edit_form_remark_text);
        edit_priority = findViewById(R.id.edit_form_priority_text);
        submitEdit = findViewById(R.id.edit_form_submit_button);
        recyclerBtn = findViewById(R.id.edit_form_recycler_button);
    }

    /**
     * 初始化页面布局效果
     */
    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    private void initLayout() {
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_bar_edit_item);
        }

        id_text.setText(Integer.toString(account.getId()));
        time_text.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(account.getTime())));
        switch (account.getWeight()) {
            case 0:
                edit_radio_group.check(edit_radio_extreme.getId());
                break;
            case 1:
                edit_radio_group.check(edit_radio_high.getId());
                break;
            case 2:
                edit_radio_group.check(edit_radio_medium.getId());
                break;
            default:
                edit_radio_group.check(edit_radio_low.getId());
        }
        edit_tag.setText(account.getTag());
        edit_name.setText(account.getName());
        edit_password.setText(account.getPassword());
        edit_remark.setText(account.getRemark());
        edit_priority.setText(Integer.toString(account.getPriority()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}