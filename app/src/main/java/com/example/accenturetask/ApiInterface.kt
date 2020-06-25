package com.example.accenturetask


import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiInterface {
    @get:GET("facts.json")
    val string: Call<String>

    companion object {
        val BASE_URL = "https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/"


    }

}