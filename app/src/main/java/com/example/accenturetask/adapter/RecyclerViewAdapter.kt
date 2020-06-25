package com.example.accenturetask.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.accenturetask.R
import com.example.accenturetask.StateList
import java.net.URL

class RecyclerViewAdapter(private val stateListItemsDta: ArrayList<StateList>) :
    RecyclerView.Adapter<RecyclerViewAdapter.UserHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_list_item, parent, false)
        return UserHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bindItems(stateListItemsDta[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return stateListItemsDta.size
    }
    //to bind data
    class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var refImage: ImageView

        fun bindItems(stateList: StateList?) {
            val title = itemView.findViewById<TextView>(R.id.title_text)
            val desText = itemView.findViewById<TextView>(R.id.sub_text)
            refImage = itemView.findViewById(R.id.refImage)

            title.visibility = View.VISIBLE
            desText.visibility = View.VISIBLE
            refImage.visibility = View.VISIBLE

            if (stateList?.itemTitle != "null") {
                title.text = stateList?.itemTitle
            }
            if (stateList?.itemDes != "null") {
                desText.text = stateList?.itemDes
            }

            //            if (stateList?.itemRefImage!=null) {
            //                LoadRefImage(stateList.itemRefImage).execute()
            //            }

            if (stateList?.itemTitle == "null" && stateList.itemDes == "null" && stateList.itemRefImage == "null") {
                title.visibility = View.GONE
                desText.visibility = View.GONE
                refImage.visibility = View.GONE
            }
        }
        //to bind images
        inner class LoadRefImage(imgURL: String) : AsyncTask<String, String, Bitmap>() {

            private val imageURL = imgURL

            override fun doInBackground(vararg p0: String?): Bitmap? {
                val url = URL(imageURL)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                return bmp
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                refImage.setImageBitmap(result)
            }
        }
    }
}