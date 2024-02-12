package com.passwordnotes;

import static com.passwordnotes.R.drawable.baseline_back_24;
import static com.passwordnotes.R.drawable.baseline_menu_24;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.passwordnotes.adapter.RecyclerListAdapter;
import com.passwordnotes.dao.Account;
import com.passwordnotes.dao.AccountMapper;
import com.passwordnotes.ui.Dialog;
import com.passwordnotes.ui.RecyclerList;
import com.passwordnotes.utils.DataProcess;
import com.passwordnotes.utils.PullDownLayout;
import com.passwordnotes.utils.toaster.Toaster;

import java.util.Date;
import java.util.List;


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
    // 抽屉页面控件
    private View menuPage;
    private View menu_recycle;

    private View menu_update;
    private View menu_textParse;

    private View menu_outputDB;
    private View menu_inputDB;
    private View menu_export;
    private View menu_setting;

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar();
        initData();
        initLayout();
        basicOnclickHandler();
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
        menuPage = findViewById(R.id.menu_page);
        menu_recycle = findViewById(R.id.drawer_recycler_view);
        menu_update = findViewById(R.id.drawer_update_item_list);
        menu_textParse = findViewById(R.id.drawer_text_parse_view);
        menu_inputDB = findViewById(R.id.drawer_input_db_view);
        menu_outputDB = findViewById(R.id.drawer_output_db_view);
        menu_export = findViewById(R.id.drawer_export_view);
        menu_setting = findViewById(R.id.drawer_setting_view);
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
                    clearInputFlingFromFocus();
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
                            new Date().getTime(),
                            0,
                            1
                    );
                    if (!accountMapper.saveAccount(newAccount)) {
                        return;
                    }
                    resetItemListData();
                    clearInputFormMsg();
                    pullDownLayout.returnMainPage();
                    clearInputFlingFromFocus();
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
     * 清除新增填写表单focus
     */
    private void clearInputFlingFromFocus() {
        tagEditText.clearFocus();
        nameEditText.clearFocus();
        passwordEditText.clearFocus();
        remarkEditText.clearFocus();
    }

    /**
     * 处理页面按钮点击事件
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void basicOnclickHandler() {
        inputFlingFromHandler();
        actionBarSearchHandler();
        itemClickHandler();

        // 标题栏菜单按钮
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

        // 回收页
        menu_recycle.setOnClickListener(
                v -> {
                    Intent recycleIntent = new Intent(MainActivity.this, RecycleItemActivity.class);

                    startActivity(recycleIntent,
                            ActivityOptions.makeSceneTransitionAnimation(
                                    MainActivity.this,
                                    menuPage,
                                    "anim_transition_layout"
                            ).toBundle());
                }
        );

        // 更新(重置)列表数据
        menu_update.setOnClickListener(
                v -> {
                    Toaster.success("列表数据已更新! 共" + resetItemListData() + "条!");
                }
        );

        // 文本解析页
        menu_textParse.setOnClickListener(
                v -> {
                    Intent recycleIntent = new Intent(MainActivity.this, TextParseActivity.class);

                    startActivity(recycleIntent,
                            ActivityOptions.makeSceneTransitionAnimation(
                                    MainActivity.this,
                                    menuPage,
                                    "anim_transition_layout"
                            ).toBundle());
                }
        );

        // 数据备份
        menu_outputDB.setOnClickListener(
                v -> {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        Dialog.show(MainActivity.this,
                                "备份提示",
                                "授予权限后,\n选择一个文件夹使用, 作为数据文件的写入位置",
                                "选择", (dialog, which) -> dbOutputLauncher.launch(intent),
                                "取消", (dialog, which) -> {
                                }
                        );
                    }
                }
        );

        // 数据恢复
        menu_inputDB.setOnClickListener(
                v -> {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/octet-stream");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        Dialog.show(MainActivity.this,
                                "恢复提示",
                                "授予权限后,\n点击备份的数据文件, 数据将会自动写入\n(!注:App原有数据将会被覆盖!)",
                                "选择", (dialog, which) -> dbInputLauncher.launch(intent),
                                "取消", (dialog, which) -> {
                                }
                        );
                    }
                }
        );

        // 数据导出
        menu_export.setOnClickListener(
                v -> {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        Dialog.show(MainActivity.this,
                                "导出提示",
                                "授予权限后,\n选择导出的文件夹, 列表数据将自动生成txt文档\n(!注: 导出数据只有标签、账号、密码)",
                                "选择", (dialog, which) -> exportLauncher.launch(intent),
                                "取消", (dialog, which) -> {
                                }
                        );
                    }
                }
        );

        // 设置页
        menu_setting.setOnClickListener(
                v -> {
                    Toaster.info("暂时还没有设置功能");
                }
        );

    }

    /*响应编辑页的数据改变*/
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

    /*响应选择文件夹输出备份文件*/
    ActivityResultLauncher<Intent> dbOutputLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (null != data) {
                        DataProcess dataProcess = new DataProcess(MainActivity.this);
                        dataProcess.dataBaseBackup(data.getData());
                    } else {
                        Toaster.info("你没有选取文件夹!");
                    }
                }
            }
    );

    /*响应选择数据库文件恢复数据*/
    ActivityResultLauncher<Intent> dbInputLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (null != data) {
                        DataProcess dataProcess = new DataProcess(MainActivity.this);
                        dataProcess.dataBaseRestore(data.getData());
                    } else {
                        Toaster.info("你没有选取数据文件!");
                    }
                }
            }
    );

    /*响应全部账户导出的文件夹*/
    ActivityResultLauncher<Intent> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (null != data) {
                        DataProcess dataProcess = new DataProcess(MainActivity.this);
                        dataProcess.exportAccountsData(data.getData());
                    } else {
                        Toaster.info("你没有选取文件夹!");
                    }
                }
            }
    );


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
                Toaster.success("账号和密码已依次复制到剪贴板!");
            }

            @Override
            public void onItemLongClick(int position, int id, View item) {
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
                // getChildAt(position)获取的View方法在RecycleView未满一屏的时候是没有问题的，但是在满一屏地情况下，是null。
                intentActivityResultLauncher.launch(editIntent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                MainActivity.this,
                                // recyclerView.getChildAt(position),
                                // recyclerView.getLayoutManager().findViewByPosition(position),
                                item,
                                "item_translation_anim"));
            }
        });
    }

    /**
     * 重置一次列表数据
     */
    public int resetItemListData() {
        int numOfList = allAccounts.size();
        allAccounts.clear();
        itemAdapter.notifyItemRangeRemoved(0, numOfList);
        allAccounts.addAll(accountMapper.getAllAccounts());
        itemAdapter.notifyItemRangeInserted(0, allAccounts.size());
        return allAccounts.size();
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

        action_bar_search_view.setOnCloseListener(() -> {
            action_bar_search_view.clearFocus();
            resetItemListData();
            return false;
        });

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