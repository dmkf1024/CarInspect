package studio.imedia.vehicleinspection.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import studio.imedia.vehicleinspection.BaseActivity;
import studio.imedia.vehicleinspection.CarFileDetailActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.adapters.MyCarFileAdapter;
import studio.imedia.vehicleinspection.interfaces.RVListener;
import studio.imedia.vehicleinspection.net.RetrofitUtils;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.retrofitbean.response.OrderListResponse;
import studio.imedia.vehicleinspection.retrofitbean.OrderBean;
import studio.imedia.vehicleinspection.utils.WidgetUtils;
import studio.imedia.vehicleinspection.utils.proxy.SPProxy;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarFileFragment extends BaseFragment implements RVListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.xrv_car_file)
    XRecyclerView xrvCarFile;
    @BindView(R.id.tv_no_login)
    TextView tvNoLogin;

    private List<OrderBean> mFileList = new ArrayList<>();
    private MyCarFileAdapter mAdapter;

    private Context mContext;

    public CarFileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_file, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 初始化toolbar
        initToolbar();
    }

    @Override
    protected Context initContext() {
        mContext = getActivity();
        return mContext;
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        mTitle.setText(TITLE_CAR_FILE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SPProxy.isOnline(getContext())) {
            WidgetUtils.showProgressDialog(getActivity(), LOADING, true);
            WidgetUtils.showList(xrvCarFile, tvNoLogin);
            getData(); // 获取数据
        } else {
            WidgetUtils.hideList(xrvCarFile, tvNoLogin);
        }
    }

    /**
     * 获取数据
     */
    private void getData() {

        String id = SPProxy.getUserId(mContext) + "";
        RetrofitUtils.getInstance()
                .baseUrl(Constant.Net.BASE_URL_DEFAULT)
                .getOrderList(id, new Subscriber<OrderListResponse>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                        WidgetUtils.hideProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                        WidgetUtils.hideProgressDialog();
                        Toast.makeText(mContext, CONNECT_FAILED, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(OrderListResponse orderListResponse) {
                        int status = orderListResponse.getStatus();
                        if (status == Constant.Status.OK) {
                            mFileList = orderListResponse.getOrders();
                        }
                        setAdapter();
                    }
                });
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        if (mFileList == null || mFileList.size() == 0) {
            return;
        }

        if (mAdapter == null) {
            mAdapter = new MyCarFileAdapter(mContext, mFileList);
            xrvCarFile.setPullRefreshEnabled(false);
            xrvCarFile.setLoadingMoreEnabled(false);
            xrvCarFile.setLayoutManager(new LinearLayoutManager(mContext));
            xrvCarFile.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        OrderBean carFile = mFileList.get(position);
        activityJump(CarFileDetailActivity.class, Constant.Key.PARCELABLE_CAR_FILE, carFile);
    }
}


