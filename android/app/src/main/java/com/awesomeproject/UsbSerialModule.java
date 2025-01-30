package com.awesomeproject;





import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDeviceConnection;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import java.util.HashMap;
import java.util.Iterator;

public class UsbSerialModule extends ReactContextBaseJavaModule {
    private static final String TAG = "UsbSerialModule";
    private final ReactApplicationContext reactContext;
    private UsbSerialDevice serialPort;
    private UsbDeviceConnection connection;
    private UsbManager usbManager;
    private static final String ACTION_USB_PERMISSION = "com.tuapp.USB_PERMISSION";
    private Promise permissionPromise;

    public UsbSerialModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        usbManager = (UsbManager) reactContext.getSystemService(Context.USB_SERVICE);

        // Registrar el BroadcastReceiver para permisos USB
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        reactContext.registerReceiver(usbReceiver, filter);
    }

    @Override
    public String getName() {
        return "UsbSerial";
    }

    // 1️⃣ SOLICITAR PERMISO PARA EL DISPOSITIVO USB
    @ReactMethod
    public void requestUsbPermission(Promise promise) {
        try {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            if (!deviceIterator.hasNext()) {
                promise.reject("NO_DEVICE", "No USB devices found");
                return;
            }

            UsbDevice device = deviceIterator.next();
            if (!usbManager.hasPermission(device)) {
                permissionPromise = promise; // Guardamos la promesa para devolver resultado
                PendingIntent permissionIntent = PendingIntent.getBroadcast(
                        reactContext,
                        0,
                        new Intent(ACTION_USB_PERMISSION),
                        PendingIntent.FLAG_MUTABLE
                );
                usbManager.requestPermission(device, permissionIntent);
            } else {
                promise.resolve("Permission already granted");
            }
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }

    // 2️⃣ RECEIVER PARA MANEJAR LA RESPUESTA DEL PERMISO
//    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
//                synchronized (this) {
//                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
//                        if (device != null && permissionPromise != null) {
//                            permissionPromise.resolve("Permission granted");
//                        }
//                    } else {
//                        if (permissionPromise != null) {
//                            permissionPromise.reject("PERMISSION_DENIED", "User denied USB permission");
//                        }
//                    }
//                }
//            }
//        }
//    };
    // 2️⃣ RECEIVER PARA MANEJAR LA RESPUESTA DEL PERMISO
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);

                    if (permissionPromise != null) {
                        if (granted && device != null) {
                            permissionPromise.resolve("Permission granted");
                        } else {
                            permissionPromise.reject("PERMISSION_DENIED", "User denied USB permission");
                        }
                        permissionPromise = null; // Limpiar la promesa para evitar futuras referencias
                    }
                }
            }
        }
    };


    // 3️⃣ CONECTAR AL DISPOSITIVO USB DESPUÉS DE TENER PERMISO
    @ReactMethod
    public void connectUsb(Promise promise) {
        try {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

            if (!deviceIterator.hasNext()) {
                promise.reject("NO_DEVICE", "No USB serial devices found");
                return;
            }

            UsbDevice device = deviceIterator.next();
            if (!usbManager.hasPermission(device)) {
                promise.reject("NO_PERMISSION", "USB permission not granted");
                return;
            }

            connection = usbManager.openDevice(device);
            if (connection == null) {
                promise.reject("CONNECTION_FAILED", "Cannot open USB connection");
                return;
            }

            serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
            if (serialPort == null) {
                promise.reject("SERIAL_PORT_ERROR", "Cannot create USB serial port");
                return;
            }

            serialPort.open();
            serialPort.setBaudRate(9600);
            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);

            promise.resolve("USB Serial connected");
        } catch (Exception e) {
            promise.reject("ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void sendData(String data, Promise promise) {
        if (serialPort != null) {
            try {
                byte[] buffer = data.getBytes();
                serialPort.write(buffer);
                promise.resolve("Data sent: " + data);
            } catch (Exception e) {
                promise.reject("SEND_ERROR", "Error sending data: " + e.getMessage());
            }
        } else {
            promise.reject("NO_CONNECTION", "USB Serial is not connected");
        }
    }


}
