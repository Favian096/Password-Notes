package com.passwordnotes.utils.toaster;

import com.passwordnotes.utils.toaster.config.IToastStrategy;
import com.passwordnotes.utils.toaster.config.IToastStyle;

/**
 *    desc   : Toast 参数类
 */
public class ToastParams {

    /** 显示的文本 */
    public CharSequence text;

    /**
     * Toast 显示时长，有两种值可选
     *
     * 短吐司：{@link android.widget.Toast#LENGTH_SHORT}
     * 长吐司：{@link android.widget.Toast#LENGTH_LONG}
     */
    public int duration = -1;

    /** 延迟显示时间 */
    public long delayMillis = 0;

    /** 是否跨页面展示（如果为 true 则优先用系统 Toast 实现） */
    public boolean crossPageShow;

    /** Toast 样式 */
    public IToastStyle<?> style;

    /** Toast 处理策略 */
    public IToastStrategy strategy;

}