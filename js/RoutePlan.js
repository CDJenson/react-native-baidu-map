/**
 * create by 909929 on 2018/5/28.
 * */
import React, {Component} from 'react';
import {DeviceEventEmitter, View,NativeModules} from 'react-native';

const _module = NativeModules.BaiduRoutePlannModule;

export default {

    routePlan(wayType, city, sLat, sLng, eLat, eLng) {
        return new Promise((resolve, reject) => {
            try {
                _module.routePlan(wayType, city, sLat, sLng, eLat, eLng);
            }
            catch (e) {
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onRoutePlanResult', resp => {
                resolve(resp);
            });
        });
    }
}