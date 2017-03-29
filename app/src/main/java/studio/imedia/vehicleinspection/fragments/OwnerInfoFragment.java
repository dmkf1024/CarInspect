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

import studio.imedia.vehicleinspection.FeedBackActivity;
import studio.imedia.vehicleinspection.LoginActivity;
import studio.imedia.vehicleinspection.MyCouponActivity;
import studio.imedia.vehicleinspection.MyOrderActivity;
import studio.imedia.vehicleinspection.PersonalInfoActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.SettingActivity;
import studio.imedia.vehicleinspection.WalletActivity;
import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;
import studio.imedia.vehicleinspection.views.RoundImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OwnerInfoFragment extends Fragment implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;
    private ImageView mRightIcon;

    private RelativeLayout layoutLoginBox;
    private LinearLayout layoutCoupon;
    private LinearLayout layoutWallet;
    private RelativeLayout layoutMyOrder;
    private RelativeLayout layoutFeedBack;

    private RoundImageView imgAvatar;
    private TextView tvUsername;
    private ImageView imgGender;
    private TextView tvScore;

    private LinearLayout layoutLogin;
    private LinearLayout layoutUserInfo;
    private ImageView imgNext;

    public OwnerInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_owner_info, container, false);
        initToolbar(view); // 初始化toolbar
        return view;
    }

    private void initToolbar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitle.setText(getString(R.string.title_owner_info));

        mRightIcon = (ImageView) mToolbar.findViewById(R.id.right_icon);
        mRightIcon.setVisibility(View.VISIBLE);
        mRightIcon.setImageResource(R.drawable.icon_set);
        mRightIcon.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findView(); // 关联控件
        initEvent(); // 初始化监听事件
    }

    /**
     * 关联控件
     */
    private void findView() {
        layoutLoginBox = (RelativeLayout) getActivity().findViewById(R.id.layout_login_box);
        layoutCoupon = (LinearLayout) getActivity().findViewById(R.id.layout_coupon);
        layoutWallet = (LinearLayout) getActivity().findViewById(R.id.layout_wallet);
        layoutMyOrder = (RelativeLayout) getActivity().findViewById(R.id.layout_my_order);
        layoutFeedBack = (RelativeLayout) getActivity().findViewById(R.id.layout_feed_back);
        layoutLogin = (LinearLayout) getActivity().findViewById(R.id.layout_login);
        layoutUserInfo = (LinearLayout) getActivity().findViewById(R.id.layout_user_info);

        imgNext = (ImageView) getActivity().findViewById(R.id.img_next);

        imgAvatar = (RoundImageView) getActivity().findViewById(R.id.img_avatar);
        tvUsername = (TextView) getActivity().findViewById(R.id.tv_username);
        imgGender = (ImageView) getActivity().findViewById(R.id.img_gender);
        tvScore = (TextView) getActivity().findViewById(R.id.tv_score);
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        layoutLoginBox.setOnClickListener(this);
        layoutCoupon.setOnClickListener(this);
        layoutWallet.setOnClickListener(this);
        layoutMyOrder.setOnClickListener(this);
        layoutFeedBack.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_login_box:
                boolean isLogin = (boolean) MySharedPreferencesUtils.get(getActivity(),
                        StaticValues.KEY_LOGIN_STATE, StaticValues.TYPE_BOOLEAN);
                if (isLogin) {
                    startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.right_icon:
                boolean isLogin01 = (boolean) MySharedPreferencesUtils.get(getActivity(),
                        StaticValues.KEY_LOGIN_STATE, StaticValues.TYPE_BOOLEAN);
                if (isLogin01) {
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_coupon:
                boolean isLogin02 = (boolean) MySharedPreferencesUtils.get(getActivity(),
                        StaticValues.KEY_LOGIN_STATE, StaticValues.TYPE_BOOLEAN);
                if (isLogin02) {
                    startActivity(new Intent(getActivity(), MyCouponActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_wallet:
                boolean isLogin03 = (boolean) MySharedPreferencesUtils.get(getActivity(),
                        StaticValues.KEY_LOGIN_STATE, StaticValues.TYPE_BOOLEAN);
                if (isLogin03) {
                    startActivity(new Intent(getActivity(), WalletActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_my_order:
                boolean isLogin04 = (boolean) MySharedPreferencesUtils.get(getActivity(),
                        StaticValues.KEY_LOGIN_STATE, StaticValues.TYPE_BOOLEAN);
                if (isLogin04) {
                    startActivity(new Intent(getActivity(), MyOrderActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.layout_feed_back:
                boolean isLogin05 = (boolean) MySharedPreferencesUtils.get(getActivity(),
                        StaticValues.KEY_LOGIN_STATE, StaticValues.TYPE_BOOLEAN);
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
        boolean isLogin = (boolean) MySharedPreferencesUtils.get(getActivity(),
                StaticValues.KEY_LOGIN_STATE, StaticValues.TYPE_BOOLEAN);
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
        String avatar = (String) MySharedPreferencesUtils.get(getActivity(), StaticValues.KEY_USER_AVATAR,
                StaticValues.TYPE_STRING);
        String username = (String) MySharedPreferencesUtils.get(getActivity(), StaticValues.KEY_USER_NAME,
                StaticValues.TYPE_STRING);
        int gender = (int) MySharedPreferencesUtils.get(getActivity(), StaticValues.KEY_USER_GENDER,
                StaticValues.TYPE_INTEGER);
        int score = (int) MySharedPreferencesUtils.get(getActivity(), StaticValues.KEY_USER_SCORE,
                StaticValues.TYPE_INTEGER);

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
