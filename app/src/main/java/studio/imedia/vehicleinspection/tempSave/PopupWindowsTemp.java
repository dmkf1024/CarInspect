package studio.imedia.vehicleinspection.tempSave;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import studio.imedia.vehicleinspection.R;

/**
 * Created by eric on 15/10/24.
 */
public class PopupWindowsTemp {
//    private Button btnShow;
//    private View popupView;
//    private PopupWindow popupWindow;
//    private RelativeLayout layoutParent;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_select_car_series);
//
//        findView(); // 关联控件
//        initEvent(); // 监听事件回调
//    }
//
//    /**
//     * 关联控件
//     */
//    private void findView() {
//        btnShow = (Button) findViewById(R.id.btn_show);
//        layoutParent = (RelativeLayout) findViewById(R.id.parent);
//    }
//
//    /**
//     * 监听事件回调
//     */
//    private void initEvent() {
//        btnShow.setOnClickListener(this);
//    }
//
//    /**
//     * 显示popupwindow
//     */
//    private void showPopupWindow(View view) {
//        popupView = LayoutInflater.from(this).inflate(R.layout.item_master_children, null);
//
//        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        popupWindow.setTouchable(true);
//        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//        ColorDrawable cd = new ColorDrawable(getResources().getColor(R.color.color_activity_bg_02));
//        popupWindow.setBackgroundDrawable(cd);
//        popupWindow.setAnimationStyle(R.style.PopupAnimation);
//        popupWindow.showAtLocation(layoutParent, Gravity.BOTTOM|Gravity.CENTER, 0, 0);
//
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha(SelectCarSeriesActivity.this, 1f);
//            }
//        });
//        if (null != popupWindow && popupWindow.isShowing()) {
//            backgroundAlpha(this, 0.4f);
//        } else {
//            backgroundAlpha(this, 1f);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_show:
//                showPopupWindow(v);
//                break;
//        }
//    }
//
//    private void backgroundAlpha(Activity context, float bgAlpha) {
//        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
//        lp.alpha = bgAlpha;
//        context.getWindow().setAttributes(lp);
//    }
}
