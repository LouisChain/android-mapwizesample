package io.mapwize.mapwizeuicomponents


import android.app.Application
import io.mapwize.mapwizeformapbox.AccountManager

class MapwizeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AccountManager.start(this, "2a3bb1a4061935b0c11cd1cc46ee572c")
    }

}