package com.codewithutsav.fmidandroid

import android.R.attr
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codewithutsav.fmidandroid.databinding.ActivityMainBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    companion object{
        val IMAGE_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    fun pickImage(view: View) {
//        val openGalleryIntent =
//            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(openGalleryIntent, IMAGE_CODE)
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK && data != null && data.data != null){
//            binding.image.setImageURI(data.data)
//        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                binding.image.setImageURI(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    fun openCamera(view: View) {
    }
}