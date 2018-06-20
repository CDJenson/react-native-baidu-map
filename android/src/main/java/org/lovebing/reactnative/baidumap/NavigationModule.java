package org.lovebing.reactnative.baidumap;


import android.content.Intent;
import android.os.Bundle;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;

import org.lovebing.reactnative.activity.BNDemoGuideActivity;

import java.util.ArrayList;
import java.util.List;

import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.BD09_MC;
import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.GCJ02;
import static com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType.WGS84;


/**
 * Created by 909929 on 2018/5/28.
 */

public class NavigationModule extends BaseModule {


    public NavigationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    @Override
    public String getName() {
        return "BaiduNavigationModule";
    }

    @ReactMethod
    public void routeplanToNavi(String coType, String sName, String eName, double sLat, double sLng, double eLat, double eLng) {
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        BNRoutePlanNode.CoordinateType coordinateType = null;

        switch (coType) {
            case "GCJ02": {
                coordinateType = GCJ02;
                break;
            }
            case "WGS84": {
                coordinateType = WGS84;
                break;
            }
            case "BD09_MC": {
                coordinateType = BD09_MC;
                break;
            }
            default:
                coordinateType = GCJ02;
                break;
        }
        sNode = new BNRoutePlanNode(sLat, sLng,
                sName, null, coordinateType);
        eNode = new BNRoutePlanNode(eLat, eLng,
                eName, null, coordinateType);

        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManager.getInstance().launchNavigator(context.getCurrentActivity(), list, 1, true, new DemoRoutePlanListener(sNode));
    }

    private class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            Intent intent = new Intent(context.getCurrentActivity(),
                    BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("ROUTE_PLAN_NODE",
                    (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub

        }
    }
}
