package com.awesomeproject

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.uimanager.ViewManager
import java.util.Collections

class MyModule(): ReactPackage {
    override fun createNativeModules(reactContext: ReactApplicationContext):List<NativeModule>{
        val modules = ArrayList<NativeModule>()
        modules.add(ToastService(reactContext))
        return modules
    }

    override fun createViewManagers(reactContext: ReactApplicationContext):List<ViewManager<*, *>>{
        return Collections.emptyList<ViewManager<*,*>>()
    }
}
