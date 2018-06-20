package org.lovebing.reactnative.baidumap;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.baidu.navisdk.adapter.PackageUtil.getSdcardDir;


/**
 * Created by lovebing on 12/20/2015.
 */
public class BaiduMapViewManager extends ViewGroupManager<MapView> {

    private static final String REACT_CLASS = "RCTBaiduMapView";

    private ThemedReactContext mReactContext;

    //导航相关
    private String mSDCardPath = null;
    private static final String APP_FOLDER_NAME = "inspector";

    private OnSetBaiduMapListener onSetBaiduMapListener;

    private ReadableArray childrenPoints;
    private HashMap<String, Marker> mMarkerMap = new HashMap<>();
    private HashMap<String, List<Marker>> mMarkersMap = new HashMap<>();
    private TextView mMarkerText;
    private int iconType = 0;

    public String getName() {
        return REACT_CLASS;
    }


    public void initSDK(Context context) {
        SDKInitializer.initialize(context);
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    // 导航 初始化引擎
    private void initNavi() {
//      BaiduNaviManager.getInstance().setNativeLibraryPath(
//              mSDCardPath + "/BaiduNaviSDK_SO");
        BaiduNaviManager.getInstance().init(mReactContext.getCurrentActivity(), mSDCardPath, APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            Log.d("onAuthResult", "onAuthResult: key校验成功!");
                        } else {
                            Log.d("onAuthResult", "onAuthResult: key校验失败!");
                        }
                    }

                    public void initSuccess() {
                        Log.d("initSuccess", "initSuccess: 百度导航引擎初始化成功!");
                    }

                    public void initStart() {
                        Log.d("initStart", "initStart: 百度导航引擎初始化开始!");
                    }

                    public void initFailed() {
                        Log.d("initFailed", "initFailed: 百度导航引擎初始化失败!");
                    }
                }, mTTSCallback);
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub

        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub

        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub

        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub

        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub

        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub

        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub

        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            return 0;
        }
    };

    public MapView createViewInstance(ThemedReactContext context) {
        mReactContext = context;
        if (initDirs()) {
            initNavi();
        }
        MapView mapView = new MapView(context);
        if (onSetBaiduMapListener != null) {
            onSetBaiduMapListener.addMap(mapView.getMap());
        }

        /*
        * 隐藏Baidu地图Logo
        * */
        View child = mapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }

        setListeners(mapView);
        return mapView;
    }

    public interface OnSetBaiduMapListener {
        void addMap(BaiduMap baiduMap);
    }

    public void addOnSetBauduMapListener(OnSetBaiduMapListener l) {
        this.onSetBaiduMapListener = l;
    }

    @Override
    public void addView(MapView parent, View child, int index) {
        if (childrenPoints != null) {
            Point point = new Point();
            ReadableArray item = childrenPoints.getArray(index);
            if (item != null) {
                point.set(item.getInt(0), item.getInt(1));
                MapViewLayoutParams mapViewLayoutParams = new MapViewLayoutParams
                        .Builder()
                        .layoutMode(MapViewLayoutParams.ELayoutMode.absoluteMode)
                        .point(point)
                        .build();
                parent.addView(child, mapViewLayoutParams);
            }
        }

    }

    @ReactProp(name = "zoomControlsVisible")
    public void setZoomControlsVisible(MapView mapView, boolean zoomControlsVisible) {
        mapView.showZoomControls(zoomControlsVisible);
    }

    @ReactProp(name = "trafficEnabled")
    public void setTrafficEnabled(MapView mapView, boolean trafficEnabled) {
        mapView.getMap().setTrafficEnabled(trafficEnabled);
    }

    @ReactProp(name = "baiduHeatMapEnabled")
    public void setBaiduHeatMapEnabled(MapView mapView, boolean baiduHeatMapEnabled) {
        mapView.getMap().setBaiduHeatMapEnabled(baiduHeatMapEnabled);
    }

    @ReactProp(name = "mapType")
    public void setMapType(MapView mapView, int mapType) {
        mapView.getMap().setMapType(mapType);
    }

    @ReactProp(name = "zoom")
    public void setZoom(MapView mapView, float zoom) {
        MapStatus mapStatus = new MapStatus.Builder().zoom(zoom).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mapView.getMap().setMapStatus(mapStatusUpdate);
    }

    @ReactProp(name = "center")
    public void setCenter(MapView mapView, ReadableMap position) {
        if (position != null) {
            double latitude = position.getDouble("latitude");
            double longitude = position.getDouble("longitude");
            LatLng point = new LatLng(latitude, longitude);
            MapStatus mapStatus = new MapStatus.Builder()
                    .target(point)
                    .build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
            mapView.getMap().setMapStatus(mapStatusUpdate);
        }
    }

    @ReactProp(name = "marker")
    public void setMarker(MapView mapView, ReadableMap option) {
        if (option != null) {
            String key = "marker_" + mapView.getId();
            Marker marker = mMarkerMap.get(key);
            if (marker != null) {
                MarkerUtil.updateMaker(marker, option);
            } else {
                marker = MarkerUtil.addMarker(mapView, option);
                mMarkerMap.put(key, marker);
            }
        }
    }

    @ReactProp(name = "markers")
    public void setMarkers(MapView mapView, ReadableArray options) {
        String key = "markers_" + mapView.getId();
        List<Marker> markers = mMarkersMap.get(key);
        if (markers == null) {
            markers = new ArrayList<>();
        }
        for (int i = 0; i < options.size(); i++) {
            ReadableMap option = options.getMap(i);
            if (markers.size() > i + 1 && markers.get(i) != null) {
                MarkerUtil.updateMaker(markers.get(i), option);
            } else {
                markers.add(i, MarkerUtil.addMarker(mapView, option));
            }
        }
        if (options.size() < markers.size()) {
            int start = markers.size() - 1;
            int end = options.size();
            for (int i = start; i >= end; i--) {
                markers.get(i).remove();
                markers.remove(i);
            }
        }
        mMarkersMap.put(key, markers);
    }

    @ReactProp(name = "childrenPoints")
    public void setChildrenPoints(MapView mapView, ReadableArray childrenPoints) {
        this.childrenPoints = childrenPoints;
    }

    /**
     * @param mapView
     */
    private void setListeners(final MapView mapView) {
        BaiduMap map = mapView.getMap();

        if (mMarkerText == null) {
            mMarkerText = new TextView(mapView.getContext());
            mMarkerText.setBackgroundResource(R.drawable.popup);
            mMarkerText.setPadding(32, 32, 32, 32);
        }
        map.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            private WritableMap getEventParams(MapStatus mapStatus) {
                WritableMap writableMap = Arguments.createMap();
                WritableMap target = Arguments.createMap();
                target.putDouble("latitude", mapStatus.target.latitude);
                target.putDouble("longitude", mapStatus.target.longitude);
                writableMap.putMap("target", target);
                writableMap.putDouble("zoom", mapStatus.zoom);
                writableMap.putDouble("overlook", mapStatus.overlook);
                return writableMap;
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                sendEvent(mapView, "onMapStatusChangeStart", getEventParams(mapStatus));
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                sendEvent(mapView, "onMapStatusChange", getEventParams(mapStatus));
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (mMarkerText.getVisibility() != View.GONE) {
                    mMarkerText.setVisibility(View.GONE);
                }
                sendEvent(mapView, "onMapStatusChangeFinish", getEventParams(mapStatus));
            }
        });

        map.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                sendEvent(mapView, "onMapLoaded", null);
            }
        });

        map.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mapView.getMap().hideInfoWindow();
                WritableMap writableMap = Arguments.createMap();
                writableMap.putDouble("latitude", latLng.latitude);
                writableMap.putDouble("longitude", latLng.longitude);
                sendEvent(mapView, "onMapClick", writableMap);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                WritableMap writableMap = Arguments.createMap();
                writableMap.putString("name", mapPoi.getName());
                writableMap.putString("uid", mapPoi.getUid());
                writableMap.putDouble("latitude", mapPoi.getPosition().latitude);
                writableMap.putDouble("longitude", mapPoi.getPosition().longitude);
                sendEvent(mapView, "onMapPoiClick", writableMap);
                return true;
            }
        });
        map.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            @Override
            public void onMapDoubleClick(LatLng latLng) {
                WritableMap writableMap = Arguments.createMap();
                writableMap.putDouble("latitude", latLng.latitude);
                writableMap.putDouble("longitude", latLng.longitude);
                sendEvent(mapView, "onMapDoubleClick", writableMap);
            }
        });

        map.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().length() > 0) {
                    mMarkerText.setText(marker.getTitle());
                    InfoWindow infoWindow = new InfoWindow(mMarkerText, marker.getPosition(), -80);
                    mMarkerText.setVisibility(View.GONE);
                    mapView.getMap().showInfoWindow(infoWindow);
                } else {
                    mapView.getMap().hideInfoWindow();
                }
                WritableMap writableMap = Arguments.createMap();
                WritableMap position = Arguments.createMap();
                position.putDouble("latitude", marker.getPosition().latitude);
                position.putDouble("longitude", marker.getPosition().longitude);
                writableMap.putMap("position", position);
                writableMap.putString("title", marker.getTitle());
                sendEvent(mapView, "onMarkerClick", writableMap);
                return true;
            }
        });

    }

    /**
     * @param eventName
     * @param params
     */
    private void sendEvent(MapView mapView, String eventName, @Nullable WritableMap params) {
        WritableMap event = Arguments.createMap();
        event.putMap("params", params);
        event.putString("type", eventName);
        mReactContext
                .getJSModule(RCTEventEmitter.class)
                .receiveEvent(mapView.getId(),
                        "topChange",
                        event);
    }
}
