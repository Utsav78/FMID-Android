package com.codewithutsav.fmidandroid


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codewithutsav.fmidandroid.databinding.ActivityMainBinding
import com.codewithutsav.fmidandroid.ml.Model
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.tensorflow.lite.support.image.TensorImage


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    fun pickImage(view: View) {
        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri: Uri = result.uri
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(resultUri))
                outputGenerator(bitmap)
                binding.image.setImageURI(resultUri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.d(TAG, "onActivityResult: ${error.message}")
            }
        }
    }

    private fun outputGenerator(bitmap: Bitmap?) {
        val model = Model.newInstance(this)
        // converting bitmap into tensor flow image
        val newBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        val tfImage = TensorImage.fromBitmap(newBitmap).tensorBuffer

        //process the image using trained model and sort it in descending order
        val outputs = model.process(tfImage)
            .outputFeature0AsTensorBuffer




    }

}