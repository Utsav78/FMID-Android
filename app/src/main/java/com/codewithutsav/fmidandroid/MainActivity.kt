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
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private val TAG = "MainActivity"
    private val instruments = arrayOf("Bansuri","Damaha","Damaru","Madal","Murchuga","Sarangi")
    val imageSize = 224


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

    private fun outputGenerator(image: Bitmap?) {
        try {
            val model = Model.newInstance(this)

            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(6 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            val intValues = intArrayOf(imageSize*imageSize)
            image?.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            var pixel = 0
            for(i in 0 until imageSize){
                for(j in 0 until imageSize){
                    val value = intValues[pixel++] // RGB
//                    byteBuffer.putFloat(((value shr 16).toFloat()) and 0XFF)
//                    byteBuffer.putFloat((((value shr 8).toFloat()) & 0xFF) * (1.f / 255.f))
//                    byteBuffer.putFloat((value and 0xFF) * (1.f/ 255.f))
                    byteBuffer.putFloat((value shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((value shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((value and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0);
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val confidences = outputFeature0.floatArray
            val size = confidences.size
            // find the index of the class with the biggest confidence.
            var maxPos = 0
            var maxConfidence = 0F
            for(i in 0 until size){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            binding.result.text = instruments[maxPos]



            // Releases model resources if no longer used.
            model.close();
        } catch (e: IOException) {
            Log.d(TAG, "outputGenerator: ${e.message}")
        }
    }


}