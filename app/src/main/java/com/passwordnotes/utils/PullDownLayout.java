package com.passwordnotes.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.passwordnotes.R;

public class PullDownLayout extends LinearLayout {
    public Scroller scroller;
    public View customComponent;

    public PullDownLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量子控件的大小
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = getVerticalHeight();
        //设置子控件的位置
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.layout(view.getLeft(), view.getTop() - height, view.getRight(), view.getBottom() - height);
        }
    }

    private void init() {
        scroller = new Scroller(getContext());
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(0, scroller.getCurrY());
            postInvalidateDelayed(10);
        }
    }

    public int getVerticalHeight() {
        this.customComponent = this.findViewById(R.id.inputFormComponents);
        return this.customComponent.getHeight();
    }

    public void returnMainPage() {
        scroller.startScroll(0, getScrollY(), 0, getVerticalHeight(), 800);
        invalidate();
    }

    public void openInputPage() {
        scroller.startScroll(0, getScrollY(), 0, -getVerticalHeight() - getScrollY(), 500);
        invalidate();
    }
}