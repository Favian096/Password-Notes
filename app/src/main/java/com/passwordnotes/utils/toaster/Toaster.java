package com.passwordnotes.utils.toaster;

import android.app.Application;
import android.content.res.Resources;
import android.widget.Toast;

import com.passwordnotes.R;
import com.passwordnotes.utils.toaster.config.IToastStrategy;
import com.passwordnotes.utils.toaster.config.IToastStyle;
import com.passwordnotes.utils.toaster.ui.BlackToastStyle;
import com.passwordnotes.utils.toaster.ui.CustomToastStyle;

/**
 * desc   : Toast 框架（专治 Toast 疑难杂症）
 */
@SuppressWarnings("unused")
public final class Toaster {

    /**
     * Application 对象
     */
    private static Application sApplication;

    /**
     * Toast 处理策略
     */
    private static IToastStrategy sToastStrategy;

    /**
     * Toast 样式
     */
    private static IToastStyle<?> sToastStyle;


    /**
     * 调试模式
     */
    private static Boolean sDebugMode;

    /**
     * 不允许被外部实例化
     */
    private Toaster() {
    }

    /**
     * 初始化 Toast，需要在 Application.create 中初始化
     *
     * @param application 应用的上下文
     */
    public static void init(Application application) {
        init(application, sToastStyle);
    }

    public static void init(Application application, IToastStrategy strategy) {
        init(application, strategy, null);
    }

    public static void init(Application application, IToastStyle<?> style) {
        init(application, null, style);
    }

    /**
     * 初始化 Toast
     *
     * @param application 应用的上下文
     * @param strategy    Toast 策略
     * @param style       Toast 样式
     */
    public static void init(Application application, IToastStrategy strategy, IToastStyle<?> style) {
        // 如果当前已经初始化过了，就不要再重复初始化了
        if (isInit()) {
            return;
        }

        sApplication = application;
        ActivityStack.getInstance().register(application);

        // 初始化 Toast 策略
        if (strategy == null) {
            strategy = new ToastStrategy();
        }
        setStrategy(strategy);

        // 设置 Toast 样式
        if (style == null) {
            style = new BlackToastStyle();
        }
        setStyle(style);
    }

    /**
     * 判断当前框架是否已经初始化
     */
    public static boolean isInit() {
        return sApplication != null && sToastStrategy != null && sToastStyle != null;
    }

    /**
     * 显示 Toast
     */

    public static void show(ToastParams params) {
        checkInitStatus();

        // 如果是空对象或者空文本就不显示
        if (params.text == null || params.text.length() == 0) {
            return;
        }

        if (params.strategy == null) {
            params.strategy = sToastStrategy;
        }

        if (params.style == null) {
            params.style = sToastStyle;
        }


        if (params.duration == -1) {
            params.duration = params.text.length() > 20 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        }

        params.strategy.showToast(params);
    }

    /**
     * 取消吐司的显示
     */
    public static void cancel() {
        sToastStrategy.cancelToast();
    }


    /**
     * 初始化全局的 Toast 样式(即默认黑色)
     *
     * @param style 设置黑色样式
     */
    public static void setStyle(IToastStyle<?> style) {
        if (style == null) {
            return;
        }
        sToastStyle = style;
    }

    /**
     * 设置 Toast 显示策略
     */
    public static void setStrategy(IToastStrategy strategy) {
        if (strategy == null) {
            return;
        }
        sToastStrategy = strategy;
        sToastStrategy.registerStrategy(sApplication);
    }

    public static IToastStrategy getStrategy() {
        return sToastStrategy;
    }

    /**
     * 检查初始化状态，如果未初始化请先调用{@link Toaster#init(Application)}
     */
    private static void checkInitStatus() {
        // 框架当前还没有被初始化，必须要先调用 init 方法进行初始化
        if (sApplication == null) {
            throw new IllegalStateException("Toaster has not been initialized");
        }
    }

    private static CharSequence stringIdToCharSequence(int id) {
        checkInitStatus();
        try {
            // 如果这是一个资源 id
            return sApplication.getResources().getText(id);
        } catch (Resources.NotFoundException ignored) {
            // 如果这是一个 int 整数
            return String.valueOf(id);
        }
    }

    private static CharSequence objectToCharSequence(Object object) {
        return object != null ? object.toString() : "null";
    }

    /**
     * 预定义实现Toaster封装效果
     * ii.                                         ;9ABH,
     * SA391,                                    .r9GG35&G
     * &#ii13Gh;                               i3X31i;:,rB1
     * iMs,:,i5895,                         .5G91:,:;:s1:8A
     * 33::::,,;5G5,                     ,58Si,,:::,sHX;iH1
     * Sr.,:;rs13BBX35hh11511h5Shhh5S3GAXS:.,,::,,1AG3i,GG
     * 以下为默认的黑色Toaster显示
     */
    public static void message(int id) {
        message(stringIdToCharSequence(id));
    }

    public static void message(Object object) {
        message(objectToCharSequence(object));
    }

    public static void message(CharSequence text) {
        ToastParams params = new ToastParams();
        params.text = text;
        show(params);
    }

    /**
     * 以下为提示显示
     */
    public static void info(int id) {
        message(stringIdToCharSequence(id));
    }

    public static void info(Object object) {
        message(objectToCharSequence(object));
    }

    public static void info(CharSequence text) {
        ToastParams params = new ToastParams();
        params.text = text;
        params.style = new CustomToastStyle(R.layout.toast_info);
        show(params);
    }

    /**
     * 以下为警告显示
     */
    public static void warm(int id) {
        message(stringIdToCharSequence(id));
    }

    public static void warm(Object object) {
        message(objectToCharSequence(object));
    }

    public static void warm(CharSequence text) {
        ToastParams params = new ToastParams();
        params.text = text;
        params.style = new CustomToastStyle(R.layout.toast_warn);
        show(params);
    }

    /**
     * 以下为成功显示
     */
    public static void success(int id) {
        message(stringIdToCharSequence(id));
    }

    public static void success(Object object) {
        message(objectToCharSequence(object));
    }

    public static void success(CharSequence text) {
        ToastParams params = new ToastParams();
        params.text = text;
        params.style = new CustomToastStyle(R.layout.toast_success);
        show(params);
    }

    /**
     * 以下为错误|失败显示
     */
    public static void error(int id) {
        message(stringIdToCharSequence(id));
    }

    public static void error(Object object) {
        message(objectToCharSequence(object));
    }

    public static void error(CharSequence text) {
        ToastParams params = new ToastParams();
        params.text = text;
        params.style = new CustomToastStyle(R.layout.toast_error);
        show(params);
    }

}