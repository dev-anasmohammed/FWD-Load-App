package com.devanasmohammed.fwd_load_app

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileName = intent.getStringExtra("fileName").toString()
        val status = intent.getStringExtra("status").toString()

        setFileName(fileName)
        setStatus(status)

        ok.setOnClickListener {
            finish()
        }

    }

    private fun setFileName(fileName: String) {
        when(fileName){
            "Glide"->file_name_tv.text = getString(R.string.glide_image_library_by_bumptech)
            "LoadApp"->file_name_tv.text = getString(R.string.loadapp_current_repository_by_udacity)
            "Retrofit"->file_name_tv.text = getString(R.string.retrofit_type_safe_http_client_for_android_and_java_by_square_inc)
        }
    }

    private fun setStatus(status: String) {
        if(status=="Failed"){
            status_tv.setTextColor(Color.RED)
        }
        status_tv.text = status
    }

}
