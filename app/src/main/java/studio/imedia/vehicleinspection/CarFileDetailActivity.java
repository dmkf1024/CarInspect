package studio.imedia.vehicleinspection;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import studio.imedia.vehicleinspection.net.RetrofitUtils;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.retrofitbean.ArchivesBean;
import studio.imedia.vehicleinspection.retrofitbean.OrderBean;
import studio.imedia.vehicleinspection.retrofitbean.response.ArchivesResponse;

public class CarFileDetailActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    @BindView(R.id.tv_station)
    TextView tvStation;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_field1)
    TextView tvField1;
    @BindView(R.id.tv_field2)
    TextView tvField2;
    @BindView(R.id.tv_field3)
    TextView tvField3;
    @BindView(R.id.tv_field4)
    TextView tvField4;
    @BindView(R.id.tv_field5)
    TextView tvField5;
    @BindView(R.id.tv_field6)
    TextView tvField6;

    private OrderBean mCarFile;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_file_detail);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
        getBundle(); // 获取上一界面传值
        getData(); // 获取数据
    }

    @Override
    protected Context initContext() {
        mContext = CarFileDetailActivity.this;
        return mContext;
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText(TITLE_CAR_FILE_DETAIL);
    }

    /**
     * 获取上一界面传值
     */
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mCarFile = bundle.getParcelable(Constant.Key.PARCELABLE_CAR_FILE);
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        String orderId = mCarFile.getId() + "";

        RetrofitUtils.getInstance()
                .baseUrl(Constant.Net.BASE_URL_DEFAULT)
                .getArchivesInfo(orderId, new Subscriber<ArchivesResponse>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                        Toast.makeText(mContext, CONNECT_FAILED, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ArchivesResponse archivesResponse) {
                    int status = archivesResponse.getStatus();
                        if (status == Constant.Status.OK) {
                            ArchivesBean archives = archivesResponse.getArchives();
                            initView(archives);
                        }
                    }
                });
    }

    /**
     * 初始化视图
     */
    private void initView(ArchivesBean archives) {
        String dateTime = mCarFile.getOrderTime();
        String date = dateTime.substring(0, dateTime.indexOf(" "));
        String station = mCarFile.getName();
        int status = mCarFile.getOrderStatus();
        tvDate.setText(date);
        tvStation.setText(station);
        if (status == Constant.Status.ORDER_PASS) {
            tvStatus.setText("状态：未通过");
        } else {
            tvStatus.setText("状态：通过");
        }

        tvField1.setText(archives.getField1());
        tvField2.setText(archives.getField2());
        tvField3.setText(archives.getField3());
        tvField4.setText(archives.getField4());
        tvField5.setText(archives.getField5());
        tvField6.setText(archives.getField6());
    }
}
