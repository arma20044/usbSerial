package com.awesomeproject;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.HashMap;

public class UsbSerialModule extends ReactContextBaseJavaModule {
    private static final String TAG = "UsbSerialModule";
    private final ReactApplicationContext reactContext;

    public UsbSerialModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "UsbSerial";
    }

    @ReactMethod
    public void listDevices(Callback callback) {
        UsbManager usbManager = (UsbManager) reactContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        callback.invoke(deviceList.size());
    }
}
