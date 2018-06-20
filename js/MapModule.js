import {
    requireNativeComponent,
    NativeModules,
    Platform,
    DeviceEventEmitter
} from 'react-native';

import React, {
    Component,
    PropTypes
} from 'react';

import Geolocation from './Geolocation';

const _mapview_module = NativeModules.BaiduMapModule;
const _geolocation_module = NativeModules.BaiduGeolocationModule;
const _navigation_module = NativeModules.BaiduNavigationModule;
const _routePlan_module = NativeModules.BaiduRoutePlannModule;

export default {
    //-----------------------------------MapView Method-------------------------------------------

    setMarker(lat, lng) {
        console.warn('This method was deprecated, please use MapView prop instead');
    },
    setMapType(type) {
        console.warn('This method was deprecated, please use MapView prop instead');
    },
    setZoom(zoom) {
        console.warn('This method was deprecated, please use MapView prop instead');
    },
    moveToCenter(lat, lng, zoom) {
        console.warn('This method was deprecated, please use MapView prop instead');
    },
    geocode(city, addr) {
        console.warn('This method was deprecated, please use Geolocation.geocode instead');
        return Geolocation.geocode(city, addr);
    },
    reverseGeoCode(lat, lng) {
        console.warn('This method was deprecated, please use Geolocation.reverseGeoCode instead');
        return Geolocation.reverseGeoCode(lat, lng);
    },
    reverseGeoCodeGPS(lat, lng) {
        console.warn('This method was deprecated, please use Geolocation.reverseGeoCodeGPS instead');
        return Geolocation.reverseGeoCodeGPS(lat, lng);
    },
    getCurrentPosition() {
        console.warn('This method was deprecated, please use Geolocation.getCurrentPosition instead');
        return Geolocation.getCurrentPosition();
    },


    //-----------------------------------BaiduGeolocationModule-------------------------------------------
    geocode(city, addr) {
        return new Promise((resolve, reject) => {
            try {
                _geolocation_module.geocode(city, addr);
            }
            catch (e) {
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onGetGeoCodeResult', resp => {
                resolve(resp);
            });
        });
    },
    reverseGeoCode(lat, lng) {
        return new Promise((resolve, reject) => {
            try {
                _geolocation_module.reverseGeoCode(lat, lng);
            }
            catch (e) {
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onGetReverseGeoCodeResult', resp => {
                resolve(resp);
            });
        });
    },
    reverseGeoCodeGPS(lat, lng) {
        return new Promise((resolve, reject) => {
            try {
                _geolocation_module.reverseGeoCodeGPS(lat, lng);
            }
            catch (e) {
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onGetReverseGeoCodeResult', resp => {
                resp.latitude = parseFloat(resp.latitude);
                resp.longitude = parseFloat(resp.longitude);
                resolve(resp);
            });
        });
    },
    getCurrentPosition() {
        if (Platform.OS == 'ios') {
            return new Promise((resolve, reject) => {
                navigator.geolocation.getCurrentPosition((position) => {
                    try {
                        _geolocation_module.reverseGeoCodeGPS(position.coords.latitude, position.coords.longitude);
                    }
                    catch (e) {
                        reject(e);
                        return;
                    }
                    DeviceEventEmitter.once('onGetReverseGeoCodeResult', resp => {
                        resp.latitude = parseFloat(resp.latitude);
                        resp.longitude = parseFloat(resp.longitude);
                        resolve(resp);
                    });
                }, (error) => {
                    reject(error);
                }, {
                    enableHighAccuracy: true,
                    timeout: 20000,
                    maximumAge: 1000
                });
            });
        }
        return new Promise((resolve, reject) => {
            try {
                _geolocation_module.getCurrentPosition();
            }
            catch (e) {
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onGetCurrentLocationPosition', resp => {
                resolve(resp);
            });
        });
    },

    
    //-----------------------------------BaiduNavigationModule-------------------------------------------
    routeplanToNavi(coType, sName, eName, sLat, sLng, eLat, eLng) {
        return new Promise((resolve, reject) => {
            try {
                _navigation_module.routeplanToNavi(coType, sName, eName, sLat, sLng, eLat, eLng);
            }
            catch (e) {
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onNavigateRouteResult', resp => {
                resolve(resp);
            });
        });
    },

    //-----------------------------------BaiduRoutePlannModule-------------------------------------------
    routePlan(wayType, city, sLat, sLng, eLat, eLng) {
        return new Promise((resolve, reject) => {
            try {
                _routePlan_module.routePlan(wayType, city, sLat, sLng, eLat, eLng);
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
    
};