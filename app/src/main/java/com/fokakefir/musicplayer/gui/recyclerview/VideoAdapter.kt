package com.fokakefir.musicplayer.gui.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.model.youtube.VideoYT;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    // region 1. Decl and Init

    private List<VideoYT> videos;
    private OnVideoListener onVideoListener;

    // endregion

    // region 2. Constructor

    public VideoAdapter(List<VideoYT> videos, OnVideoListener onVideoListener) {
        this.videos = videos;
        this.onVideoListener = onVideoListener;
    }


    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_video, parent, false);
        VideoViewHolder viewHolder = new VideoViewHolder(v, this.onVideoListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoYT currentVideo = this.videos.get(position);

        holder.txtTitle.setText(currentVideo.snippet.title);
        holder.txtChannel.setText(currentVideo.snippet.channelTitle);

        String imageUrl = currentVideo.snippet.thumbnails.medium.url;
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.imgVideo);

        holder.btnDownload.setVisibility(View.GONE);
        holder.imgVideo.setAlpha((float) 1.0);

        holder.videoId = currentVideo.id.videoId;
        holder.videoChannel = currentVideo.snippet.channelTitle;
        holder.thumbnail = currentVideo.snippet.thumbnails.medium.url;

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    // endregion

    // region 4. Holder class

    public static class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgVideo;
        public TextView txtTitle;
        public TextView txtChannel;
        public ImageButton btnDownload;

        public String videoId;
        public String videoChannel;
        public String thumbnail;
        public boolean selected;

        private View itemView;

        private OnVideoListener onVideoListener;

        public VideoViewHolder(@NonNull View itemView, OnVideoListener onVideoListener) {
            super(itemView);

            this.selected = false;

            this.imgVideo = itemView.findViewById(R.id.img_video);
            this.txtTitle = itemView.findViewById(R.id.txt_video_title);
            this.txtChannel = itemView.findViewById(R.id.txt_video_channel);
            this.btnDownload = itemView.findViewById(R.id.btn_download);

            this.videoId = null;
            this.videoChannel = null;
            this.thumbnail = null;

            this.itemView = itemView;
            itemView.setOnClickListener(this);

            this.btnDownload.setOnClickListener(this);

            this.onVideoListener = onVideoListener;
        }

        @Override
        public void onClick(View view) {
            if (view == this.itemView) {
                if (!this.selected) {

                    this.imgVideo.animate().alpha((float) 0.5).setDuration(100).start();
                    this.btnDownload.setVisibility(View.VISIBLE);
                    this.selected = true;
                } else {
                    this.imgVideo.animate().alpha((float) 1.0).setDuration(100).start();
                    this.btnDownload.setVisibility(View.GONE);
                    this.selected = false;
                }
            } else if (view.getId() == R.id.btn_download) {
                if (this.videoId != null)
                    this.onVideoListener.onVideoDownloadClick(this.videoId, this.videoChannel);
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnVideoListener {
        void onVideoDownloadClick(String videoId, String videoChannel);
    }

    // endregion

}
