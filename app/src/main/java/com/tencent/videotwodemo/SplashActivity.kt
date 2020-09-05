package com.tencent.videotwodemo

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.videotwodemo.videoactivity.VideoActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        setContentView(R.layout.activity_splash)
        btn.setOnClickListener {
            RxPermissions(this).request(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).subscribe({
                if (it) {

                    startActivity(Intent(this, VideoActivity::class.java))
                } else {
                    Toast.makeText(this, "请给权限", Toast.LENGTH_LONG).show()
                }
            })





        }


    }


}