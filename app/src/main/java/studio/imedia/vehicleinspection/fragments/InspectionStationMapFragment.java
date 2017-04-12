package studio.imedia.vehicleinspection.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.MyLocationUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class InspectionStationMapFragment extends Fragment implements LocationSource,
        AMapLocationListener, View.OnClickListener, AMap.OnMarkerClickListener,
        TextWatcher, PoiSearch.OnPoiSearchListener,
        InfoWindowAdapter, OnInfoWindowClickListener {

    protected AMap aMap;
    protected MapView mapView;
    protected OnLocationChangedListener mListener;
    protected LocationManagerProxy mAMapLocationManager;
    protected Marker marker;// 定位雷达小图标
    protected Marker[] markers;
    protected CameraUpdateFactory cameraUpdate;
    protected ProgressDialog progDialog = null;// 搜索时进度条
    protected AutoCompleteTextView searchPoi;
    protected PoiSearch.Query query;
    protected int currentPage = 0;
    protected PoiResult poiResult; // poi返回的结果

    protected float mZoom;//缩放级别
    protected float mBearing;//方向
    protected float mTilt;//倾斜角度
    protected String[] distanceOrder;//该字符串组存储车检店距离定位点距离
    protected LatLng location;
    public int[] index;

    protected Button btnLocate;

    public InspectionStationMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inspection_station_map, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
        //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
//        MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) getActivity().findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init(); // 初始化
    }

    /**
     * 初始化
     */
    protected void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
            // 获取当前地图的缩放级别
            mZoom = aMap.getCameraPosition().zoom;
            // 获取当前地图的旋转角度
            mBearing = aMap.getCameraPosition().bearing;
            // 获取当前地图的倾斜角度
            mTilt = aMap.getCameraPosition().tilt;
        }

        btnLocate = (Button) getActivity().findViewById(R.id.btn_locate);
        btnLocate.setOnClickListener(this);//定位按钮点击实现

        searchPoi = (AutoCompleteTextView) getActivity().findViewById(R.id.search_map);
        searchPoi.addTextChangedListener(this);// 添加文本输入框监听事件
        searchPoi.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    doSearchQuery();
                    Log.d("msg", "已点击搜索");

                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                return false;
            }
        });
        index = new int[Constant.Location.NUM_VI];


    }

    /**
     * 设置一些amap的属性
     */
    protected void setUpMap() {
        ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point1));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point2));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point3));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point4));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point5));
        giflist.add(BitmapDescriptorFactory.fromResource(R.drawable.point6));
        marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .icons(giflist).period(50));
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationRotateAngle(180);
        aMap.setLocationSource(this);// 设置定位监听
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种

        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        marker.setFlat(true);

        addMarkersToMap();//地图上添加marker
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12));//设置地图缩放级别
    }

    /**
     * 当前fragment被隐藏或被显示回调函数
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mapView.onPause();
//            deactivate();
        } else {
            mapView.onResume();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 激活定位
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
            /*
             * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, -1, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }

    /**
     * 定位成功后回调函数
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            marker.setPosition(new LatLng(aMapLocation.getLatitude(), aMapLocation
                    .getLongitude()));// 定位雷达小图标
            location = marker.getPosition();
            Log.d("msg","定位的Latlng为："+location);
            getOrder();
            float bearing = aMap.getCameraPosition().bearing;
            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_locate:
                changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        marker.getPosition(), mZoom, mTilt, mBearing)), null);
                break;
        }
    }

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    protected void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
//        调用moveCamera方法
        aMap.moveCamera(update);
//        不调用CancelableCallback方法
//        aMap.animateCamera(update,1000,callback);
    }

    /**
     * 在地图上添加marker
     */
    protected void addMarkersToMap() {
        markers = new Marker[Constant.Location.NUM_VI];
        for (int i = 0; i < Constant.Location.NUM_VI; i++) {
            markers[i] = aMap.addMarker(new MarkerOptions()
                    .position(Constant.Location.VI[i])
                    .title(Constant.Location.NAME_VI[i])
                    .snippet("这是contents")
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .draggable(true));
            Log.d("msg", "已添加Markers：" + Constant.Location.VI[i]);
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        for (int i = 0; i < markers.length; i++) {
            if (marker.equals(markers[i])) {
                if (aMap != null) {
                    Log.d("msg", "使用了onMarkerClick方法");
                    markers[i].showInfoWindow();
//                    searchRouteResult(markers[i].getPosition(), markers[i].getPosition());
                }
            }
        }
        return false;
    }


    /**
     * 显示进度框
     */
    protected void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getActivity());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    protected void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        Inputtips inputTips = new Inputtips(getActivity(),
                new Inputtips.InputtipsListener() {

                    @Override
                    public void onGetInputtips(List<Tip> tipList, int rCode) {
                        if (rCode == 0) {// 正确返回
                            final List<String> listString = new ArrayList<>();
                            for (int i = 0; i < tipList.size(); i++) {
                                listString.add(tipList.get(i).getName());
                            }
                            final ArrayAdapter<String> aAdapter = new ArrayAdapter<>(
                                    getActivity(),
                                    R.layout.item_search_poi, listString);
                            searchPoi.setAdapter(aAdapter);
                            aAdapter.notifyDataSetChanged();
                        }
                    }
                });
        try {
            inputTips.requestInputtips(newText, "杭州");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号

        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * POI信息查询回调方法
     */
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        dissmissProgressDialog();// 隐藏对话框
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(getActivity(),
                                R.string.no_result, Toast.LENGTH_SHORT);
                    }
                }
            } else {
                Toast.makeText(getActivity(),
                        R.string.no_result, Toast.LENGTH_SHORT);
            }
        } else if (rCode == 27) {
            Toast.makeText(getActivity(),
                    R.string.error_network, Toast.LENGTH_SHORT);
        } else if (rCode == 32) {
            Toast.makeText(getActivity(),
                    R.string.error_key, Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(getActivity(),
                    R.string.error_other, Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int i) {

    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        showProgressDialog();// 显示进度框
        currentPage = 0;
        query = new PoiSearch.Query(searchPoi.getText().toString(), "", "杭州");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页uery

        PoiSearch poiSearch = new PoiSearch(getActivity(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        Toast.makeText(getActivity(), infomation, Toast.LENGTH_SHORT);

    }


    /**
     * 调起高德地图导航功能，如果没安装高德地图，会进入异常，可以在异常中处理，调起高德地图app的下载页面
     */
    public void startAMapNavi(Marker marker) {
        //构造导航参数
        NaviPara naviPara = new NaviPara();
        //设置终点位置
        naviPara.setTargetPoint(marker.getPosition());
        //设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
        try {
            //调起高德地图导航
            AMapUtils.openAMapNavi(naviPara, getActivity().getApplicationContext());
        } catch (com.amap.api.maps.AMapException e) {
            //如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(getActivity().getApplicationContext());
        }


    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_infowindow,
                null);

        render(marker, view);
        return view;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(final Marker marker, View view) {
        ImageView imgVI = (ImageView) view.findViewById(R.id.info_imgVI);
        TextView title = (TextView) view.findViewById(R.id.info_title);
        TextView contents = (TextView) view.findViewById(R.id.info_contents);
        TextView distance = (TextView) view.findViewById(R.id.tv_distance);
        Button button = (Button) view.findViewById(R.id.btn_navigation);

        String titleSring = marker.getTitle();
        String contentsString = marker.getSnippet();
        if (titleSring != null) {
            title.setText(titleSring);

        } else {

        }

        if (contentsString != null) {
            contents.setText(contentsString);
        } else {

        }
        // 调起高德地图app导航功能
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAMapNavi(marker);
            }
        });

        Log.d("msg", "已触发info窗口");

    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
    }

    /**
     * 计算每个加油站与定位点之间的距离并进行排序
     */
    protected void getOrder() {
        distanceOrder = new String[Constant.Location.NUM_VI];
        String[] orderList = new String[Constant.Location.NUM_VI];

        for (int i = 0; i < Constant.Location.NUM_VI; i++) {
            BigDecimal bigDecimal = new BigDecimal(AMapUtils.calculateLineDistance(location, Constant.Location.VI[i]));
            distanceOrder[i] = bigDecimal.toPlainString();
            orderList[i] = distanceOrder[i];
            Log.d("msg", "转换的距离为： " + distanceOrder[i] + "米");
        }
        Arrays.sort(distanceOrder);
        for (int i = 0; i < Constant.Location.NUM_VI; i++) {
            MyLocationUtils myLocationUtils = MyLocationUtils.getInstance();
            myLocationUtils.saveIndex(orderList, distanceOrder, Constant.Location.NUM_VI);

//            index[i] = getIndex(orderList, distanceOrder, StaticValues.NUM_VI)[i];
            Log.d("msg", "距离" + i + ": " + distanceOrder[i] + "米");
            Log.d("msg", "找回的下标为：" + index[i]);
        }
    }

    /**
     * 返回排序后的新序列在旧序列的下标
     *
     * @param a   排序前的String
     * @param b   排序后的String
     * @param num a，b的个数
     * @return
     */
    protected int[] getIndex(String[] a, String[] b, int num) {
        int[] o = new int[num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                if (b[i].equals(a[j]))
                    o[i] = j;
            }
        }
        return o;
    }

}