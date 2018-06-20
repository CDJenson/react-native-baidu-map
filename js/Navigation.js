/**
 * create by 909929 on 2018/5/28.
 * */
import React, {Component} from 'react';
import {View,DeviceEventEmitter,NativeModules} from 'react-native';

const _module = NativeModules.BaiduNavigationModule;

export default {

    routeplanToNavi(coType, sName, eName, sLat, sLng, eLat, eLng) {
        return new Promise((resolve, reject) => {
            try {
                _module.routeplanToNavi(coType, sName, eName, sLat, sLng, eLat, eLng);
            }
            catch (e) {
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onNavigateRouteResult', resp => {
                resolve(resp);
            });
        });
    }

}