package org.lovebing.reactnative.baidumap;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.List;


/**
 * Created by lovebing on 4/17/16.
 */
public class BaiduMapPackage implements ReactPackage {

    private Context mContext;

    BaiduMapViewManager baiduMapViewManager;

    private RoutePlanModule routePlanModule;

    public BaiduMapPackage(Context context) {
        this.mContext = context;
        baiduMapViewManager = new BaiduMapViewManager();
        baiduMapViewManager.initSDK(context);
        baiduMapViewManager.addOnSetBauduMapListener(new BaiduMapViewManager.OnSetBaiduMapListener() {
            @Override
            public void addMap(BaiduMap baiduMap) {
                routePlanModule.setBaiduMap(baiduMap);
            }
        });
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        routePlanModule = new RoutePlanModule(reactContext);
        return Arrays.<NativeModule>asList(
                new BaiduMapModule(reactContext),
                new GeolocationModule(reactContext),
                new NavigationModule(reactContext),
                routePlanModule
        );
    }

    @Override
    public List<ViewManager> createViewManagers(
            ReactApplicationContext reactContext) {
        return Arrays.<ViewManager>asList(
                baiduMapViewManager
        );
    }

}
