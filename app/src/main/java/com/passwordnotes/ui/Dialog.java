package com.passwordnotes.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.passwordnotes.R;

public class Dialog {

    @SuppressLint("ResourceAsColor")
    public static void show(Context context,
                            String titleText,
                            String msgText,
                            String positiveText,
                            DialogInterface.OnClickListener positiveClick,
                            String negativeText,
                            DialogInterface.OnClickListener negativeClick) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 自定义 title样式
        TextView dialogTitle = new TextView(context);
        dialogTitle.setText(titleText);
        dialogTitle.setTypeface(Typeface.DEFAULT_BOLD);
        dialogTitle.setTextColor(ContextCompat.getColor(context, R.color.system_gray_deep));
        dialogTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.system_gray_little));
        // dialogTitle.setTextColor(Color.parseColor("#3c3f41"));
        // dialogTitle.setBackgroundColor(Color.parseColor("#fafafa"));
        dialogTitle.setTextSize(20);
        dialogTitle.setGravity(Gravity.CENTER);
        dialogTitle.setPadding(0, 20, 0, 20);
        builder.setCustomTitle(dialogTitle);

        // 中间的信息以一个view的形式设置进去
        TextView dialogBody = new TextView(context);
        dialogBody.setText(msgText);
        dialogBody.setTextSize(18);
        dialogBody.setTextColor(ContextCompat.getColor(context, R.color.system_gray_deep));
        // dialogBody.setTextColor(Color.parseColor("#3c3f41"));
        dialogBody.setGravity(Gravity.CENTER);
        dialogBody.setPadding(0, 20, 0, 20);
        builder.setView(dialogBody);

        // 设置按钮
        builder.setPositiveButton(positiveText, positiveClick)
                .setNegativeButton(negativeText, negativeClick);

        // show()
        builder.setCancelable(false);
        AlertDialog dialog = builder.show();

        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        final Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        // 安卓下面有三个位置的按钮，默认权重为 1,设置成 500或更大才能让两个按钮看起来均分
        LinearLayout.LayoutParams positiveParams = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveParams.gravity = Gravity.CENTER;
        positiveParams.setMargins(10, 10, 10, 10);
        positiveParams.width = 0;
        positiveParams.weight = 500;
        positiveParams.setMarginStart(50);
        positiveButton.setLayoutParams(positiveParams);
        positiveButton.setBackgroundColor(ContextCompat.getColor(context, R.color.system_gray_middle));
        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.system_green));

        LinearLayout.LayoutParams negativeParams = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
        negativeParams.gravity = Gravity.CENTER;
        negativeParams.setMargins(10, 10, 10, 10);
        negativeParams.width = 0;
        negativeParams.weight = 500;

        negativeButton.setLayoutParams(negativeParams);
        negativeButton.setBackgroundColor(ContextCompat.getColor(context, R.color.system_gray_middle));
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.system_gray_middle_up));
    }
}
