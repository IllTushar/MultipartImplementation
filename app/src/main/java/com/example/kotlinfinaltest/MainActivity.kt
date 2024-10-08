package com.example.kotlinfinaltest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinfinaltest.Api.RetrofitClient
import com.example.kotlinfinaltest.Model.ResponseModel
import com.example.kotlinfinaltest.databinding.ActivityMainBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.selectPdf.setOnClickListener {
            pickPDF()
        }
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    data?.data.let { uri ->
                        // Convert URI to File and upload
                        val file = uriToFile(uri)
                        if (file != null) {
                            callApi(file)
                        } else {
                            println("File conversion failed")
                        }
                    }
                }

            }
    }

    private fun uriToFile(uri: Uri?): File? {
        // Get the file name
        val fileName = uri?.let { getFileNameFromUri(it) }

        // Create a temporary file in the internal storage
        val file = File(cacheDir, fileName)

        try {
            // Write content from the URI to the file
            val inputStream = uri?.let { contentResolver.openInputStream(it) }
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // Helper method to get the file name from the URI
    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = "tempfile.pdf" // Default name
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex("_display_name")
            if (cursor.moveToFirst() && nameIndex != -1) {
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }


    private fun pickPDF() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf" // For PDF files
        filePickerLauncher.launch(intent)
    }

    private fun callApi(file: File) {
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        RetrofitClient.apiService.postPDF(body).enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.code() == 200) {
                        val responseData = response.body()
                        val filePath = responseData?.DownloadCompressPDF
                        val fileName = responseData?.filename
                        Toast.makeText(this@MainActivity, filePath, Toast.LENGTH_LONG).show()
                    } else {
                        Log.d("MainActivity", response.code().toString())
                    }
                } else {
                    Log.d("MainActivity", "Unsuccessful")
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Log.d("MainActivity", t.toString())
            }
        })
    }
}


