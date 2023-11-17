package com.fokakefir.musicplayer.gui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.gui.recyclerview.VideoAdapter;
import com.fokakefir.musicplayer.model.youtube.VideoYT;
import com.fokakefir.musicplayer.model.youtube.YoutubeVideos;
import com.fokakefir.musicplayer.logic.network.YoutubeAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class SearchFragment extends Fragment implements Callback<YoutubeVideos>, VideoAdapter.OnVideoListener, View.OnClickListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private MainActivity activity;

    private EditText txtSearch;
    private Button btnSearch;

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<VideoYT> videos;

    // endregion

    // region 2. Lifecycle and Constructor

    public SearchFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_search, container, false);

        this.txtSearch = this.view.findViewById(R.id.txt_search);
        this.btnSearch = this.view.findViewById(R.id.btn_search);
        this.btnSearch.setOnClickListener(this);

        this.videos = new ArrayList<>();

        this.recyclerView = this.view.findViewById(R.id.recycler_view_videos);
        this.layoutManager = new LinearLayoutManager(getContext());
        this.adapter = new VideoAdapter(this.videos, this);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. Button listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_search) {
            String query = this.txtSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                String url = YoutubeAPI.BASE_URL + YoutubeAPI.SEARCH + YoutubeAPI.KEY
                        + YoutubeAPI.MAX_RESULTS + YoutubeAPI.ORDER + YoutubeAPI.PART
                        + YoutubeAPI.QUERY + query
                        + YoutubeAPI.TYPE;

                Call<YoutubeVideos> data = YoutubeAPI.getRequest().getYT(url);
                data.enqueue(this);
            } else {
                Toast.makeText(getContext(), "Input can't be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // endregion

    // region 4. Youtube response

    @Override
    public void onResponse(Call<YoutubeVideos> call, Response<YoutubeVideos> response) {
        if (response.errorBody() != null) {
            Log.w(TAG, "onResponse: " + response.errorBody());
        } else {
            YoutubeVideos videos = response.body();
            this.videos.clear();
            this.videos.addAll(videos.getVideos());
            this.adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(Call<YoutubeVideos> call, Throwable t) {
        Log.e(TAG, "onFailure: ", t);
    }

    // endregion

    // region 5. Video listener

    @Override
    public void onVideoDownloadClick(String videoId, String videoChannel) {
        String url = "https://www.youtube.com/watch?v=" + videoId;
        this.activity.downloadMusic(url, videoId, videoChannel);
    }

    // endregion

}