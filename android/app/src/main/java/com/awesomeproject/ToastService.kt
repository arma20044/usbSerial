package com.awesomeproject

import android.content.Context
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod



class ToastService(private val reactContext:ReactApplicationContext):ReactContextBaseJavaModule(reactContext){
    override fun getName():String{
        return "ToastModule"
    }

    @ReactMethod
    fun showToast(message:String){
        val toast = Toast.makeText(reactContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0 , 0)
        toast.show()
    }




}
