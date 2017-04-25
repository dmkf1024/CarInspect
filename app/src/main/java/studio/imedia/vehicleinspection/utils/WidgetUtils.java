package studio.imedia.vehicleinspection.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import studio.imedia.vehicleinspection.views.MyDatePickerDialog;
import studio.imedia.vehicleinspection.views.MyTimePickerDialog;

/**
 * Created by cooffee on 15/10/15.
 */
public class WidgetUtils {

    private static ProgressDialog mPDialog = null;

    /**
     * 根据textView是否有内容，判断是否使能按钮
     *
     * @param button
     * @param textViews
     */
    public static void enableButtonByTextView(Button button, TextView... textViews) {
        int length = textViews.length;

        for (int i = 0; i < length; i++) {
            String content = textViews[i].getText().toString().trim();
            if (content.isEmpty()) {
                button.setEnabled(false);
                return;
            }
        }
        button.setEnabled(true);
    }

    /**
     * 根据EditText文本内容是否为空判断是否disable按钮
     * 如果有一个EditText文本内容为空，则disable按钮
     *
     * @parambutton
     * @parameditText
     */
    public static void enableButtonByEditText(final Button button, final EditText... editTexts) {
        int length = editTexts.length;// 表示传入的EditText的个数
        final boolean[] flag = new boolean[length];// 表示各个edittext文本内容是否为空，false表示空，true表示非空

        button.setEnabled(true);
        for (int i = 0; i < length; i++) {
            String content = editTexts[i].getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                flag[i] = false;
                button.setEnabled(false);
            } else {
                flag[i] = true;
            }
        }
        for (int i = 0; i < length; i++) {
            final int finalI = i;
            editTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (editTexts[finalI].getText().toString().length() == 0) {
                        flag[finalI] = false;
                    } else {
                        flag[finalI] = true;
                    }

                    if (allNotEmpty(flag)) {
                        button.setEnabled(true);
                    } else {
                        button.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    /**
     * 根据EditText文本内容是否为空判断是否disable按钮
     * 如果有一个EditText文本内容为空，则disable按钮
     *
     * @param button
     * @param editTexts
     */
    public static void btnStateByEditText(final Button button, final EditText... editTexts) {
        final boolean[] state = {true};
        int length = editTexts.length;// 表示传入的EditText的个数
        final boolean[] flag = new boolean[length];// 表示各个edittext文本内容是否为空，false表示空，true表示非空

        for (int i = 0; i < length; i++) {
            String content = editTexts[i].getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                flag[i] = false;
                state[0] = false;
            } else {
                flag[i] = true;
            }
        }
        for (int i = 0; i < length; i++) {
            final int finalI = i;
            editTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (editTexts[finalI].getText().toString().length() == 0) {
                        flag[finalI] = false;
                    } else {
                        flag[finalI] = true;
                    }

                    if (allNotEmpty(flag)) {
                        state[0] = true;
                    } else {
                        state[0] = false;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    /**
     * 判断是不是所有的标记表示的都是非空
     * 如果有一个表示的为空，则返回false
     * 否则，返回true
     *
     * @return
     * @paramflag
     */
    private static boolean allNotEmpty(boolean[] flag) {
        for (int i = 0; i < flag.length; i++) {
            if (false == flag[i])
                return false;
        }
        return true;
    }

    /**
     * 判断TextView内容是否为空
     *
     * @param textView
     * @return
     */
    public static boolean isTextViewEmpty(TextView textView) {
        return textView.getText().toString().isEmpty();
    }

    /**
     * DatePickerDialog/TimePickerDialog中选择日期/时间，显示在TextView中
     *
     * @param textView
     * @param dialogType 选择日期/时间选择对话框
     * @param isFormat   是否对选择的数字进行格式规范，如：9变为09
     */
    public static void pickerDialogToTextView(Context context, final TextView textView, int dialogType, final boolean isFormat) {

        String content = textView.getText().toString();

        // 获取当前日期时间
        Calendar calendar = Calendar.getInstance();
        int yearInit = calendar.get(Calendar.YEAR);
        int monthInit = calendar.get(Calendar.MONTH);
        int dayInit = calendar.get(Calendar.DAY_OF_MONTH);
        int hourInit = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteInit = calendar.get(Calendar.MINUTE);

        switch (dialogType) {
            case Type.DATE_PICKER_DIALOG:

                if (!content.isEmpty() && isDateTime(content, Type.DATE_PICKER_DIALOG)) {
                    yearInit = Integer.parseInt(content.substring(0, content.indexOf("-")));
                    monthInit = Integer.parseInt(content.substring(content.indexOf("-") + 1, content.lastIndexOf("-"))) - 1; // 原来多加的月份1减回来
                    dayInit = Integer.parseInt(content.substring(content.lastIndexOf("-") + 1));
                }

                new MyDatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear++; // 月份从0开始计数
                        String yearSelected = "";
                        String monthSelected = "";
                        String daySelected = "";

                        if (isFormat) {
                            yearSelected = formatNum(year);
                            monthSelected = formatNum(monthOfYear);
                            daySelected = formatNum(dayOfMonth);
                        } else {
                            yearSelected = year + "";
                            monthSelected = monthOfYear + "";
                            daySelected = dayOfMonth + "";
                        }

                        textView.setText(yearSelected + "-" + monthSelected + "-" + daySelected);
                    }
                }, yearInit, monthInit, dayInit).show();
                break;
            case Type.TIME_PICKER_DIALOG:

                if (!content.isEmpty() && isDateTime(content, Type.TIME_PICKER_DIALOG)) {
                    hourInit = Integer.parseInt(content.substring(0, content.indexOf(":")));
                    minuteInit = Integer.parseInt(content.substring(content.indexOf(":") + 1));
                }

                new MyTimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hourSelected = "";
                        String minuteSelected = "";

                        if (isFormat) {
                            hourSelected = formatNum(hourOfDay);
                            minuteSelected = formatNum(minute);
                        } else {
                            hourSelected = hourOfDay + "";
                            minuteSelected = minute + "";
                        }

                        textView.setText(hourOfDay + ":" + minuteSelected);
                    }
                }, hourInit, minuteInit, true).show();
                break;
        }
    }

    /**
     * 规范数字，如果传入的数小于10，则返回两位字符串
     * 如传入7，则返回“07”
     *
     * @param num
     * @return
     */
    private static String formatNum(int num) {
        if (num < 10)
            return "0" + num;
        else
            return "" + num;
    }

    /**
     * 判断是不是为日期 格式为：3015-09-12
     *
     * @param date
     * @return
     */
    private static boolean isDateTime(String date, int type) {
        String format = "";
        switch (type) {
            case Type.DATE_PICKER_DIALOG:
                format = "yyyy-MM-dd";
                break;
            case Type.TIME_PICKER_DIALOG:
                format = "HH:mm";
                break;
        }
        try {
            new SimpleDateFormat(format).parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 传入的参数的选择范围
     */
    public class Type {
        public static final int DATE_PICKER_DIALOG = 0;
        public static final int TIME_PICKER_DIALOG = 1;

        public static final int TYPE_GONE = 0;
        public static final int TYPE_INVISIBLE = 1;
    }

    /**
     * 根据勾选框，选择是否显示视图
     *
     * @param checkBox
     * @param checkedShow true -表示勾选为显示，不勾选为隐藏  false -表示勾选为隐藏，不勾选为显示
     * @param hideType
     * @param views
     */
    public static void displayViewByCheckBox(CheckBox checkBox, boolean checkedShow, int hideType, View... views) {
        boolean state = checkBox.isChecked();
        displayViewByCheckBox(state, checkedShow, hideType, views);
    }

    /**
     * 根据勾选框，选择是否显示视图
     *
     * @param isChecked
     * @param checkedShow true -表示勾选为显示，不勾选为隐藏  false -表示勾选为隐藏，不勾选为显示
     * @param hideType
     * @param views
     */
    public static void displayViewByCheckBox(boolean isChecked, boolean checkedShow, int hideType, View... views) {
        boolean state = isChecked;
        boolean isShow = false;
        if (checkedShow) {
            isShow = state;
        } else {
            isShow = !state;
        }

        if (isShow) {
            for (int i = 0; i < views.length; i++) {
                views[i].setVisibility(View.VISIBLE);
            }
        } else {
            switch (hideType) {
                case Type.TYPE_GONE:
                    for (int i = 0; i < views.length; i++) {
                        views[i].setVisibility(View.GONE);
                    }
                    break;
                case Type.TYPE_INVISIBLE:
                    for (int i = 0; i < views.length; i++) {
                        views[i].setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    }

    /**
     * 通过按钮切换不同视图
     *
     * @param button 开关按钮
     * @param resId1 初始的图片id
     * @param resId2 点击一次后的图片id
     * @param view1  初始显示的控件
     * @param view2  初始隐藏的控件
     */
    public static void switchViewByButon(final Button button, final int resId1, final int resId2, final View view1, final View view2) {
        final int[] curState = {1};
        button.setBackgroundResource(resId1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (1 == curState[0]) {
                    curState[0] = 2;
                    button.setBackgroundResource(resId2);
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                } else {
                    curState[0] = 1;
                    button.setBackgroundResource(resId1);
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 显示进度条对话框
     *
     * @param context
     * @param title
     * @param message
     * @param isCancelable
     */
    public static void showProgressDialog(Context context, String title, String message, boolean isCancelable) {
        if (mPDialog == null)
            mPDialog = new ProgressDialog(context);
        mPDialog.setTitle(title);
        mPDialog.setMessage(message);
        mPDialog.setCancelable(isCancelable);
        mPDialog.show();
        mPDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mPDialog = null;
            }
        });
    }

    /**
     * 显示进度条弹框
     * @param context
     * @param message
     */
    public static void showProgressDialog(Context context, String message) {
        showProgressDialog(context, null, message, false);
    }

    /**
     * 显示进度条弹框
     * @param context
     * @param message
     * @param isCancelable
     */
    public static void showProgressDialog(Context context, String message, boolean isCancelable) {
        showProgressDialog(context, null, message, isCancelable);
    }

    /**
     * 隐藏进度条对话框
     */
    public static void hideProgressDialog() {
        if (mPDialog != null) {
            if (mPDialog.isShowing()) {
                mPDialog.dismiss();
            }
            mPDialog = null;
        }
    }

    /**
     * 显示带输入框的弹出框
     *
     * @param context
     * @param title
     * @param textView
     */
    public static void showDialogWithET(Context context, String title, final TextView textView) {
        final EditText editText = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString().trim();
                        textView.setText(input);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    /**
     * 显示列表弹出框，将选中的项的字符串返回到textview中
     *
     * @param context
     * @param title
     * @param textView
     * @param items
     */
    public static void showDialogWithItems(Context context, String title, final TextView textView, final String[] items) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String content = items[which];
                        textView.setText(content);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 返回字符串数组指定字符串下标
     *
     * @param array
     * @param item
     * @return 指定字符串所在数组的下标
     */
    private static int getIndex(String[] array, String item) {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            if (item.equals(array[i]))
                return i;
        }
        return 0;
    }

    /**
     * 隐藏列表
     *
     * @param list
     * @param textView
     */
    public static void hideList(View list, TextView textView) {
        list.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

    /**
     * 显示列表
     *
     * @param list
     * @param textView
     */
    public static void showList(View list, TextView textView) {
        list.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    public static boolean isEtContentEqual(EditText editText1, EditText editText2) {
        String content1 = editText1.getText().toString();
        String content2 = editText2.getText().toString();
        return content1.equals(content1);
    }
}
