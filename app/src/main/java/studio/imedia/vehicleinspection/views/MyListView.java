package studio.imedia.vehicleinspection.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by eric on 15/12/9.
 */
public class MyListView extends ListView {

    public MyListView(Context context) {
        super(context);
        this.setVerticalScrollBarEnabled(false); // 隐藏滚动条
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setVerticalScrollBarEnabled(false); // 隐藏滚动条
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setVerticalScrollBarEnabled(false); // 隐藏滚动条
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
