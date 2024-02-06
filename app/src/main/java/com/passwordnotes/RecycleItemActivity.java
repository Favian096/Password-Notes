package com.passwordnotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.passwordnotes.adapter.RecycleAdapter;
import com.passwordnotes.adapter.RecyclerListAdapter;
import com.passwordnotes.dao.Account;
import com.passwordnotes.dao.AccountMapper;

import java.util.List;

public class RecycleItemActivity extends AppCompatActivity {
    private AccountMapper accountMapper;

    private RecyclerView recycleItemView;
    private List<Account> recycleAccounts;
    RecycleAdapter recycleAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_item);
        initData();
        initLayout();
        basicOnclickHandler();

    }

    private void basicOnclickHandler() {
        recycleAdapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int id) {
                if (accountMapper.restoreAccount(id)) {
                    recycleAccounts.remove(position);
                    recycleAdapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onItemLongClick(int position, int id) {
                if (accountMapper.deleteAccount(id)) {
                    recycleAccounts.remove(position);
                    recycleAdapter.notifyItemRemoved(position);
                }
            }
        });
    }

    private void initLayout() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.custom_bar_recycle);
            actionBar.setElevation(1);
        }
        recycleItemView.setLayoutManager(new LinearLayoutManager(this));
        recycleItemView.setAdapter(recycleAdapter);

    }

    private void initData() {
        accountMapper = new AccountMapper(this);
        recycleAccounts = accountMapper.getRecycleAccounts();
        recycleItemView = findViewById(R.id.recycle_item_recycler_view);
        recycleAdapter = new RecycleAdapter(
                RecycleItemActivity.this,
                recycleAccounts, recycleItemView);
    }

}