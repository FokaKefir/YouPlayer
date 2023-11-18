package com.fokakefir.musicplayer.gui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.gui.recyclerview.VideoAdapter.VideoViewHolder
import com.fokakefir.musicplayer.model.youtube.VideoYT
import com.squareup.picasso.Picasso

class VideoAdapter(
    private val videos: List<VideoYT>,
    private val onVideoListener: OnVideoListener
) : RecyclerView.Adapter<VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.example_video, parent, false)
        return VideoViewHolder(v, onVideoListener)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val currentVideo = videos[position]
        holder.txtTitle.text = currentVideo.snippet!!.title
        holder.txtChannel.text = currentVideo.snippet!!.channelTitle
        val imageUrl = currentVideo.snippet!!.thumbnails!!.medium!!.url
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .fit()
            .centerCrop()
            .into(holder.imgVideo)
        holder.btnDownload.visibility = View.GONE
        holder.imgVideo.alpha = 1.0.toFloat()
        holder.videoId = currentVideo.id!!.videoId
        holder.videoChannel = currentVideo.snippet!!.channelTitle
        holder.thumbnail = currentVideo.snippet!!.thumbnails!!.medium!!.url
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    class VideoViewHolder(itemView: View, onVideoListener: OnVideoListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var imgVideo: ImageView
        var txtTitle: TextView
        var txtChannel: TextView
        var btnDownload: ImageButton
        var videoId: String? = null
        var videoChannel: String? = null
        var thumbnail: String? = null
        var selected = false
        private val onVideoListener: OnVideoListener

        init {
            imgVideo = itemView.findViewById(R.id.img_video)
            txtTitle = itemView.findViewById(R.id.txt_video_title)
            txtChannel = itemView.findViewById(R.id.txt_video_channel)
            btnDownload = itemView.findViewById(R.id.btn_download)
            itemView.setOnClickListener(this)
            btnDownload.setOnClickListener(this)
            this.onVideoListener = onVideoListener
        }

        override fun onClick(view: View) {
            if (view === this.itemView) {
                if (!selected) {
                    imgVideo.animate().alpha(0.5.toFloat()).setDuration(100).start()
                    btnDownload.visibility = View.VISIBLE
                    selected = true
                } else {
                    imgVideo.animate().alpha(1.0.toFloat()).setDuration(100).start()
                    btnDownload.visibility = View.GONE
                    selected = false
                }
            } else if (view.id == R.id.btn_download) {
                if (videoId != null) {
                    val videoId: String = videoId ?: ""
                    val videoChannel: String = videoChannel ?: ""
                    onVideoListener.onVideoDownloadClick(videoId, videoChannel)
                }
            }
        }
    }

    interface OnVideoListener {
        fun onVideoDownloadClick(videoId: String, videoChannel: String)
    }
}