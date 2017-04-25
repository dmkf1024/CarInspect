package studio.imedia.vehicleinspection.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import studio.imedia.vehicleinspection.FeedBackActivity;
import studio.imedia.vehicleinspection.LoginActivity;
import studio.imedia.vehicleinspection.MyCouponActivity;
import studio.imedia.vehicleinspection.MyOrderActivity;
import studio.imedia.vehicleinspection.PersonalInfoActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.SettingActivity;
import studio.imedia.vehicleinspection.WalletActivity;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.views.RoundImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OwnerInfoFragment extends Fragment {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView mRightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.img_avatar)
    RoundImageView imgAvatar;
    @BindView(R.id.layout_login)
    LinearLayout layoutLogin;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.img_gender)
    ImageView imgGender;
    @BindView(R.id.tv_score)
    TextView tvScore;
    @BindView(R.id.layout_user_info)
    LinearLayout layoutUserInfo;
    @BindView(R.id.img_next)
    ImageView imgNext;
    @BindView(R.id.layout_login_box)
    RelativeLayout layoutLoginBox;
    @BindView(R.id.layout_coupon)
    LinearLayout layoutCoupon;
    @BindView(R.id.layout_wallet)
    LinearLayout layoutWallet;
    @BindView(R.id.img_my_order)
    ImageView imgMyOrder;
    @BindView(R.id.layout_my_order)
    RelativeLayout layoutMyOrder;
    @BindView(R.id.img_app)
    ImageView imgApp;
    @BindView(R.id.img_suggestion)
    ImageView imgSuggestion;
    @BindView(R.id.layout_feed_back)
    RelativeLayout layoutFeedBack;

    public OwnerInfoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_info, container, false);
        ButterKnife.bind(this, view);
        initToolbar(view); // 初始化toolbar
        return view;
    }

    private void initToolbar(View view) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        mTitle.setText(getString(R.string.title_owner_info));

        mRightIcon.setVisibility(View.VISIBLE);
        mRightIcon.setImageResource(R.drawable.icon_set);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @OnClick({R.id.layout_login_box, R.id.right_icon, R.id.layout_coupon, R.id.layout_wallet, R.id.layout_my_order, R.id.layout_feed_back})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_login_box:
                boolean isLogin = (boolean) SPUtil.get(getActivity(),
                        Constant.Key.LOGIN_STATE, Constant.Type.BOOLEAN);
                if (isLogin) {
                    startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.right_icon:
                boolean isLogin01 = (boolean) SPUtil.get(getActivity(),
                        Constant.Key.LOGIN_STATE, Constant.Type.BOOLEAN);
                if (isLogin01) {
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_coupon:
                boolean isLogin02 = (boolean) SPUtil.get(getActivity(),
                        Constant.Key.LOGIN_STATE, Constant.Type.BOOLEAN);
                if (isLogin02) {
                    startActivity(new Intent(getActivity(), MyCouponActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_wallet:
                boolean isLogin03 = (boolean) SPUtil.get(getActivity(),
                        Constant.Key.LOGIN_STATE, Constant.Type.BOOLEAN);
                if (isLogin03) {
                    startActivity(new Intent(getActivity(), WalletActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_my_order:
                boolean isLogin04 = (boolean) SPUtil.get(getActivity(),
                        Constant.Key.LOGIN_STATE, Constant.Type.BOOLEAN);
                if (isLogin04) {
                    startActivity(new Intent(getActivity(), MyOrderActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_feed_back:
                boolean isLogin05 = (boolean) SPUtil.get(getActivity(),
                        Constant.Key.LOGIN_STATE, Constant.Type.BOOLEAN);
                if (isLogin05) {
                    startActivity(new Intent(getActivity(), FeedBackActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isLogin = (boolean) SPUtil.get(getActivity(),
                Constant.Key.LOGIN_STATE, Constant.Type.BOOLEAN);
        if (isLogin) {
            updateUserInfo(); // 更新用户信息
            showUserInfo(); // 显示用户信息
        } else {
            hideUserInfo(); // 隐藏用户信息
        }
    }

    /**
     * 更新用户信息
     */
    private void updateUserInfo() {
        String avatar = (String) SPUtil.get(getActivity(), Constant.Key.USER_AVATAR,
                Constant.Type.STRING);
        String username = (String) SPUtil.get(getActivity(), Constant.Key.USER_NAME,
                Constant.Type.STRING);
        int gender = (int) SPUtil.get(getActivity(), Constant.Key.USER_GENDER,
                Constant.Type.INTEGER);
        int score = (int) SPUtil.get(getActivity(), Constant.Key.USER_SCORE,
                Constant.Type.INTEGER);

        // TODO 根据头像url设置头像

        tvUsername.setText(username);
        if (gender == 0)
            imgGender.setImageResource(R.drawable.icon_male);
        else
            imgGender.setImageResource(R.drawable.icon_female);
        tvScore.setText("积分：" + score);
    }

    /**
     * 显示用户姓名、性别、积分
     * 显示右箭头图标
     * 隐藏“立即登录”字样
     */
    private void showUserInfo() {
        layoutUserInfo.setVisibility(View.VISIBLE);
        imgNext.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.GONE);
    }

    /**
     * 显示“立即登录”字样
     * 隐藏右箭头图标
     * 隐藏用户姓名、性别、积分
     */
    private void hideUserInfo() {
        layoutUserInfo.setVisibility(View.GONE);
        imgNext.setVisibility(View.GONE);
        layoutLogin.setVisibility(View.VISIBLE);
    }
}
