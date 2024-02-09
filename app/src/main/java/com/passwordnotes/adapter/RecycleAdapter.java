package com.passwordnotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.passwordnotes.R;
import com.passwordnotes.dao.Account;

import java.util.List;

/**
 * 设置回收账户多选的适配器
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private List<Account> list;

    Context activityContext;


    /**
     * 初始化回收数据
     *
     * @param context context
     * @param list    数据
     */
    public RecycleAdapter(Context context, List<Account> list, RecyclerView recyclerView) {
        this.activityContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_recycle, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.card_tag.setText(list.get(position).getTag());
        holder.card_name.setText(list.get(position).getName());
        holder.card_password.setText(list.get(position).getPassword());
        holder.card_remark.setText(list.get(position).getRemark());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //    添加点击和长按事件接口
    public interface OnItemClickListener {
        void onItemClick(int position, int id);

        void onItemLongClick(int position, int id);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onClickListener) {
        onItemClickListener = onClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View item;
        View card;
        TextView card_tag;
        TextView card_name;
        TextView card_password;
        TextView card_remark;

        public ViewHolder(@NonNull View view) {
            super(view);
            item = view;
            card = item.findViewById(R.id.item_recycle_list_card);
            card_tag = item.findViewById(R.id.item_recycle_list_card_header_tag);
            card_name = item.findViewById(R.id.item_recycle_list_card_body_name);
            card_password = item.findViewById(R.id.item_recycle_list_card_body_password);
            card_remark = item.findViewById(R.id.item_recycle_list_card_footer_remark);

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
                            onItemClickListener.onItemLongClick(getAdapterPosition(), list.get(getAdapterPosition()).getId());
                        }
                        return true;
                    }
            );

        }

    }

}
