package com.codewithutsav.fmidandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codewithutsav.fmidandroid.databinding.ActivityInformationBinding

class InformationActivity : AppCompatActivity() {
    private lateinit var binding:ActivityInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
    }
}