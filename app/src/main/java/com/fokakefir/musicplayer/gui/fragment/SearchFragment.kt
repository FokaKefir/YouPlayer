package com.fokakefir.musicplayer.gui.fragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.gui.activity.MainActivity
import com.fokakefir.musicplayer.gui.recyclerview.VideoAdapter
import com.fokakefir.musicplayer.gui.recyclerview.VideoAdapter.OnVideoListener
import com.fokakefir.musicplayer.logic.network.YoutubeAPI
import com.fokakefir.musicplayer.logic.network.YoutubeAPI.request
import com.fokakefir.musicplayer.model.youtube.VideoYT
import com.fokakefir.musicplayer.model.youtube.YoutubeVideos
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment(private val activity: MainActivity) : Fragment(), Callback<YoutubeVideos?>,
    OnVideoListener, View.OnClickListener {
    private lateinit var view: View
    private var txtSearch: EditText? = null
    private lateinit var btnSearch: Button
    private lateinit var recyclerView: RecyclerView
    private var adapter: VideoAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var videos: MutableList<VideoYT>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_search, container, false)
        txtSearch = view.findViewById(R.id.txt_search)
        btnSearch = view.findViewById(R.id.btn_search)
        btnSearch.setOnClickListener(this)
        videos = ArrayList()
        recyclerView = view.findViewById(R.id.recycler_view_videos)
        layoutManager = LinearLayoutManager(context)
        adapter = VideoAdapter(videos as ArrayList<VideoYT>, this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        return view
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btn_search) {
            val query = txtSearch!!.text.toString().trim { it <= ' ' }
            if (!query.isEmpty()) {
                val url = (YoutubeAPI.BASE_URL + YoutubeAPI.SEARCH + YoutubeAPI.KEY
                        + YoutubeAPI.MAX_RESULTS + YoutubeAPI.ORDER + YoutubeAPI.PART
                        + YoutubeAPI.QUERY + query
                        + YoutubeAPI.TYPE)
                val data = request!!.getYT(url)
                data!!.enqueue(this)
            } else {
                Toast.makeText(context, "Input can't be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResponse(call: Call<YoutubeVideos?>, response: Response<YoutubeVideos?>) {
        if (response.errorBody() != null) {
            Log.w(ContentValues.TAG, "onResponse: " + response.errorBody())
        } else {
            val videos = response.body()
            this.videos!!.clear()
            this.videos!!.addAll(videos!!.videos!!)
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onFailure(call: Call<YoutubeVideos?>, t: Throwable) {
        Log.e(ContentValues.TAG, "onFailure: ", t)
    }

    override fun onVideoDownloadClick(videoId: String, videoChannel: String) {
        val url = "https://www.youtube.com/watch?v=$videoId"
        activity.downloadMusic(url, videoId, videoChannel)
    }
}