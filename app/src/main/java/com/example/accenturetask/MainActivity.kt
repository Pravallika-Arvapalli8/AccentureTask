package com.example.accenturetask

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.accenturetask.ApiInterface.Companion.BASE_URL
import com.example.accenturetask.adapter.RecyclerViewAdapter
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var progress_bar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        progress_bar = findViewById(R.id.progressBar1)

        val layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        //Pull down to refresh
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            toCheckInternet()
            swipeRefreshLayout.isRefreshing = false
        }
        toCheckInternet()

    }

    //if internet is there will get response if not will get alert dialog
    private fun toCheckInternet(){
        if (checkInternetConnection())
            getStateResponse()
        else
            showNoInternetDialog()
    }
    //To display data when we get response from api

    private fun getStateResponse() {

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create()).build()

        val api = retrofit.create(ApiInterface::class.java)
        val call = api.string
        progress_bar.visibility = View.VISIBLE
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.v("onResponse", response.body().toString())
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        Log.v("onSuccess", response.body().toString())
                        val jsonResponse = response.body().toString()

                        val finalStateList = writeStateDataToList(jsonResponse)
                        val recyclerViewAdapter = RecyclerViewAdapter(finalStateList)
                        recyclerView.adapter = recyclerViewAdapter
                        progress_bar.visibility = View.GONE
                    } else {
                        Log.i("onEmptyResponse", "Returned empty response")
                        progress_bar.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                progress_bar.visibility = View.GONE
            }
        })
    }

    private fun writeStateDataToList(response: String): ArrayList<StateList> {
        val responseStateList = ArrayList<StateList>()
        try {
            //getting the whole json object from the response
            val obj = JSONObject(response)
            val mainTitle: String = obj.get("title").toString()
            supportActionBar?.title = mainTitle
            val dataArray = obj.getJSONArray("rows")
            Log.v("writeStateDataToList", dataArray.toString())
            responseStateList.clear()
            if (dataArray.length() > 0) {
                for (i in 0 until dataArray.length()) {
                    val dataObj = dataArray.getJSONObject(i)

                    val itemTitle = dataObj.getString("title")
                    val itemDes = dataObj.getString("description")
                    val itemRefImage = dataObj.getString("imageHref")

                    responseStateList.add(StateList(mainTitle, itemTitle, itemDes, itemRefImage))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return responseStateList
    }
    //To check internet connection
    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    //when their is no internet connection dialog display
    private fun showNoInternetDialog() {
        AlertDialog.Builder(this).setTitle(applicationContext.getString(R.string.app_name))
            .setMessage("No Internet Connection").setPositiveButton("OK") { _, _ ->
                finish()
            }.setCancelable(false).create().show()
    }
}