package com.passwordnotes.adapter;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.passwordnotes.R;
import com.passwordnotes.config.Settings;
import com.passwordnotes.dao.Account;

import java.util.List;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ViewHolder> {
    private final List<Account> list;

    Context activityContext;

    RecyclerView recyclerView;

    /**
     * 初始化显示数据
     *
     * @param context context
     * @param list    数据
     */
    public RecyclerListAdapter(Context context, List<Account> list, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.activityContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.card_menu.setCardBackgroundColor(getDefaultColor(list.get(position).getWeight()));
        holder.card_tag.setText(list.get(position).getTag());
        holder.card_name.setText(list.get(position).getName());
        holder.card_password.setText(list.get(position).getPassword());
        holder.card_remark.setText(list.get(position).getRemark());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 返回定制色
     *
     * @param weight 色彩
     * @return 色彩
     */
    int getDefaultColor(int weight) {
        switch (weight) {
            case 0:
                return ContextCompat.getColor(activityContext, R.color.system_burgundy);
            case 1:
                return ContextCompat.getColor(activityContext, R.color.system_red);
            case 2:
                return ContextCompat.getColor(activityContext, R.color.system_orange);
            default:
                return ContextCompat.getColor(activityContext, R.color.system_blue);
        }
    }

    //    添加点击和长按事件接口
    public interface OnItemClickListener {
        void onItemClick(int position, int id);

        void onItemLongClick(int position, int id, View item);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        onItemClickListener = onClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View item;
        View card;
        CardView card_menu;
        TextView card_tag;
        TextView card_name;
        TextView card_password;
        TextView card_remark;

        public ViewHolder(@NonNull View view) {
            super(view);
            item = view;
            card = item.findViewById(R.id.item_list_card);
            card_menu = item.findViewById(R.id.item_list_card_header_menu);
            card_tag = item.findViewById(R.id.item_list_card_header_tag);
            card_name = item.findViewById(R.id.item_list_card_body_name);
            card_password = item.findViewById(R.id.item_list_card_body_password);
            card_remark = item.findViewById(R.id.item_list_card_footer_remark);
            if (!Settings.showItemListName)
                card_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            if (!Settings.showItemListPassword)
                card_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            card.setOnClickListener(
                    v -> {
                        if (null != onItemClickListener) {
                            onItemClickListener.onItemClick(getAdapterPosition(), list.get(getAdapterPosition()).getId());
                        }
                    }
            );

            card.setOnLongClickListener(
                    v -> {
                        if (null != onItemClickListener) {
                            onItemClickListener.onItemLongClick(getAdapterPosition(), list.get(getAdapterPosition()).getId(), item);
                        }
                        return true;
                    }
            );

        }

    }

}