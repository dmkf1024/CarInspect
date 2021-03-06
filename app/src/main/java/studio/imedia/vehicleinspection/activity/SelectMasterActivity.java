package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.adapters.MyMasterEXLVAdapter;
import studio.imedia.vehicleinspection.bean.Master;

public class SelectMasterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.layout_back)
    LinearLayout layoutBack;
    @BindView(R.id.exlv_masters)
    ExpandableListView exlvMasters;

    private List<Master> masterList;
    private MyMasterEXLVAdapter myMasterEXLVAdapter;

    private Context mContext = SelectMasterActivity.this;

    private static final String[] names = {"张师傅", "李师傅", "王师傅"};
    private static final int[] serviceAmount = {123, 221, 231};
    private static final int[] prices = {60, 80, 70};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_master);
        ButterKnife.bind(this);

        initEvent(); // 初始化监听事件
        initData(); // 初始化数据
        putIntoAdapter(); // 放入适配器中
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        layoutBack.setOnClickListener(this);
    }

    private void initData() {
        if (null == masterList) {
            masterList = new ArrayList<>();
        }
        for (int i = 0; i < names.length; i++) {
            Master master = new Master();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_head);
            master.setAvatar(bitmap);
            master.setName(names[i]);
            master.setStartNum(i + 3);
            master.setServiceAmount(serviceAmount[i]);
            master.setPrice(prices[i]);

            masterList.add(master);
        }
    }

    /**
     * 放入适配器中
     */
    private void putIntoAdapter() {
        if (null == myMasterEXLVAdapter) {
            myMasterEXLVAdapter = new MyMasterEXLVAdapter(mContext, masterList, exlvMasters, getIntent().getExtras());
        }
        exlvMasters.setGroupIndicator(null); // 将控件默认的左边箭头去掉
        exlvMasters.setAdapter(myMasterEXLVAdapter);
        exlvMasters.expandGroup(0);
        exlvMasters.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // 展开一项，折叠其他所有项
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < masterList.size(); i++) {
                    if (groupPosition != i) {
                        exlvMasters.collapseGroup(i);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        putIntoAdapter();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                startActivity(new Intent(mContext, SelectTimeWayActivity.class));
                finish();
                break;
        }
    }
}
