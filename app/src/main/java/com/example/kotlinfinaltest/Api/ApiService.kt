package com.example.kotlinfinaltest.Api

import com.example.kotlinfinaltest.Model.ResponseModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @Headers(
        "x-apihub-key: bV0ZGg3MvUzLKfRdJKpJ208oIZD4LsIMr0c7RrqnHM9XWhx4rI",
        "x-apihub-host: PDF-Service-API.allthingsdev.co",
        "x-apihub-endpoint: fd44e66d-0585-42d3-836d-e7910e1a5b77"
    )
    @POST("compress_Angular_Api")
    fun postPDF(@Part file: MultipartBody.Part): Call<ResponseModel>

}