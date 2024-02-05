package com.passwordnotes;

import static com.passwordnotes.R.drawable.baseline_back_24;
import static com.passwordnotes.R.drawable.baseline_menu_24;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Date;
import java.util.List;

import com.passwordnotes.adapter.RecyclerList;
import com.passwordnotes.adapter.RecyclerListAdapter;
import com.passwordnotes.config.PullDownLayout;
import com.passwordnotes.dao.Account;
import com.passwordnotes.dao.AccountMapper;


public class MainActivity extends AppCompatActivity {
    public RecyclerList recyclerView;
    private RecyclerListAdapter itemAdapter;

    List<Account> allAccounts;
    private PullDownLayout pullDownLayout;
    private LinearLayoutManager linearLayoutManager;
    private AppCompatImageButton baseline_menu;
    private DrawerLayout drawerLayout;
    private AccountMapper accountMapper;
    private View action_bar_title;
    /*input_form_控件*/
    private RadioGroup weightRadioGroup;
    private EditText tagEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    EditText remarkEditText;
    private Button formCancel;
    private Button formConfirm;
    private SearchView action_bar_search_view;
    private View drawer_recycler_view;
    ActivityResultLauncher<Intent> intentActivityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    (ActivityResultCallback<ActivityResult>) result -> {
                        if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                            Account account = accountMapper.getAccount(result.getData().getIntExtra("id", 0));
                            allAccounts.set(result.getData().getIntExtra("position", 0), account);
                            itemAdapter.notifyItemChanged(result.getData().getIntExtra("position", 0));
                        } else if (result.getData() != null && result.getResultCode() == Activity.RESULT_FIRST_USER) {
                            allAccounts.remove(result.getData().getIntExtra("position", 0));
                            itemAdapter.notifyItemRemoved(result.getData().getIntExtra("position", 0));
                        }
                        InputMethodManager imm = (InputMethodManager) this.getSystemService(InputMethodManager.class);
                        if (imm.isActive()) // 隐藏键盘
                            imm.hideSoftInputFromWindow(pullDownLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    });

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar();
        initData();
        initLayout();
        basicOnclickHandler();

        allAccounts.forEach(System.out::println);
    }

    /**
     * 处理标题栏下滑即可滑动出新增填写表单
     */
    @SuppressLint({"ClickableViewAccessibility", "NonConstantResourceId"})
    private void inputFlingFromHandler() {
//        Fling显示表单
        GestureDetector gestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
//                        if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                        pullDownLayout.openInputPage();
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        action_bar_title.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        /*按钮和信息记录*/
        // 取消按钮
        formCancel.setOnClickListener(
                v -> {
                    clearInputFormMsg();
                    pullDownLayout.returnMainPage();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(InputMethodManager.class);
                    if (imm.isActive()) // 隐藏键盘
                        imm.hideSoftInputFromWindow(pullDownLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
        );
        // 确认按钮
        formConfirm.setOnClickListener(
                v -> {
                    int weight = -1;
                    switch (weightRadioGroup.getCheckedRadioButtonId()) {
                        case R.id.input_form_weight_radio_extreme:
                            weight = 0;
                            break;
                        case R.id.input_form_weight_radio_high:
                            weight = 1;
                            break;
                        case R.id.input_form_weight_radio_medium:
                            weight = 2;
                            break;
                        case R.id.input_form_weight_radio_low:
                            weight = 3;
                            break;
                        default:
                            break;
                    }
                    if (-1 == weight) {
                        Toast.makeText(this, "请选择重要性！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String tag = tagEditText.getText().toString();
                    if (tag.isEmpty()) {
                        Toast.makeText(this, "标签是必填选项！", Toast.LENGTH_SHORT).show();
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
                            new Date().getTime(),
                            0,
                            1
                    );
                    if (!accountMapper.saveAccount(newAccount)) {
                        return;
                    }
                    int numOfList = allAccounts.size();
                    allAccounts.clear();
                    itemAdapter.notifyItemRangeRemoved(0, numOfList);
                    allAccounts.addAll(accountMapper.getAllAccounts());
                    itemAdapter.notifyItemRangeInserted(0, allAccounts.size());
                    clearInputFormMsg();
                    pullDownLayout.returnMainPage();
                    InputMethodManager imm = (InputMethodManager) this.getSystemService(InputMethodManager.class);
                    if (imm.isActive()) // 隐藏键盘
                        imm.hideSoftInputFromWindow(pullDownLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
        );

    }

    /**
     * 清空输入信息
     */
    private void clearInputFormMsg() {
        weightRadioGroup.clearCheck();
        tagEditText.setText(null);
        nameEditText.setText(null);
        passwordEditText.setText(null);
        remarkEditText.setText(null);
    }

    /**
     * 处理页面按钮点击事件
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void basicOnclickHandler() {
        inputFlingFromHandler();
        actionBarSearchHandler();
        itemClickHandler();

        baseline_menu.setOnClickListener(
                v -> {
                    if (!drawerLayout.isOpen()) {
                        baseline_menu.setImageDrawable(getDrawable(baseline_back_24));
                        drawerLayout.open();
                    } else {
                        drawerLayout.close();
                    }
                }
        );

    }

    /**
     * 处理recyclerView列表项目事件
     */
    private void itemClickHandler() {
        itemAdapter.setOnItemClickListener(new RecyclerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int id) {
                // 写入系统剪贴板
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipAccountData = ClipData.newPlainText("账户", allAccounts.get(position).getName());
                clipboard.setPrimaryClip(clipAccountData);
                // 使用 Handler 添加延迟, 保证第三方输入法可以连续读取到
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    ClipData clipPasswordData = ClipData.newPlainText("密码", allAccounts.get(position).getPassword());
                    clipboard.setPrimaryClip(clipPasswordData);
                }, 1000);
            }

            @Override
            public void onItemLongClick(int position, int id) {
                Intent editIntent = new Intent(MainActivity.this, EditItemActivity.class);
                editIntent.putExtra("id", id);
                editIntent.putExtra("position", position);
                // startActivity(editIntent,
                //         ActivityOptions
                //                 .makeSceneTransitionAnimation(
                //                         MainActivity.this,
                //                         recyclerView.getChildAt(position),
                //                         "item_translation_anim")
                //                 .toBundle());
                // System.out.println(accountMapper.getAccount(id).toString());
                intentActivityResultLauncher.launch(editIntent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                MainActivity.this,
                                recyclerView.getChildAt(position),
                                "item_translation_anim"));
            }
        });
    }

    /**
     * 处理搜索事件
     */
    private void actionBarSearchHandler() {
        action_bar_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Account> accountsQuery = accountMapper.getAccountsByTag(newText, newText);
                int numOfList = allAccounts.size();
                allAccounts.clear();
                itemAdapter.notifyItemRangeRemoved(0, numOfList);
                allAccounts.addAll(accountsQuery);
                itemAdapter.notifyItemRangeInserted(0, accountsQuery.size());
                return false;
            }
        });

        action_bar_search_view.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                int numOfList = allAccounts.size();
                allAccounts.clear();
                itemAdapter.notifyItemRangeRemoved(0, numOfList);
                allAccounts.addAll(accountMapper.getAllAccounts());
                itemAdapter.notifyItemRangeInserted(0, allAccounts.size());
                return false;
            }
        });


    }

    /**
     * 初始化标题栏, 在中间嵌入自定义样式View, 优化阴影效果
     */
    @SuppressLint("ResourceAsColor")
    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Enable 自定义的 View
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            // 绑定自定义的布局
            actionBar.setCustomView(R.layout.custom_bar);
            actionBar.setElevation(1);
        }
        action_bar_search_view = findViewById(R.id.action_bar_search_view);
        action_bar_search_view.setQueryHint(Html.fromHtml("<font color = #717171>" + getResources().getString(R.string.action_bar_search_view_text) + "</font>"));
    }

    /**
     * 初始化控件
     * <a>绑定按钮, 文本框, 编辑框...</a>
     */
    private void initData() {
        action_bar_title = findViewById(R.id.action_bar_title);
        baseline_menu = findViewById(R.id.baseline_menu);
        pullDownLayout = findViewById(R.id.pull_down_layout);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recyclerview);
        accountMapper = new AccountMapper(this);
        weightRadioGroup = findViewById(R.id.input_form_weight_radio_group);
        tagEditText = findViewById(R.id.input_form_tag_edit_text);
        nameEditText = findViewById(R.id.input_form_name_edit_text);
        passwordEditText = findViewById(R.id.input_form_password_edit_text);
        remarkEditText = findViewById(R.id.input_form_remark_edit_text);
        formCancel = findViewById(R.id.input_form_button_cancel);
        formConfirm = findViewById(R.id.input_form_button_confirm);
        drawer_recycler_view = findViewById(R.id.drawer_recycler_view);
    }

    /**
     * 初始化布局效果
     * <a>定义RecyclerView布局</a>
     * <a>设置drawerLayout抽屉效果</a>
     * <a>初始化pullDownLayout下拉效果</a>
     */
    private void initLayout() {
        // drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                baseline_menu.setImageDrawable(getDrawable(baseline_menu_24));
            }
        });

        allAccounts = accountMapper.getAllAccounts();
        itemAdapter = new RecyclerListAdapter(this, allAccounts, this.recyclerView);
        recyclerView.setAdapter(itemAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    /**
     * 关闭数据库
     */
    @Override
    protected void onDestroy() {
        accountMapper.DB.close();
        accountMapper.accountsReader.close();
        accountMapper.accountsWriter.close();
        super.onDestroy();
    }
}