package com.tencent.videotwodemo.app


import android.app.Application



class App : Application() {

    companion object {
        lateinit var ApplicationINSTANCE: App
    }



    override fun onCreate() {
        super.onCreate()

        ApplicationINSTANCE = this;

    }








}
