package com.fokakefir.musicplayer.logic.background;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

public class RequestDownloadThumbnailStream extends AsyncTask<String, Integer, String> {

    private Context context;

    public RequestDownloadThumbnailStream(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        File dir = new File(this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/YoutubeThumbnails");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        DownloadManager manager = (DownloadManager) this.context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(params[0]);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        String fileName = params[1];

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setTitle(fileName + ".jpg")
                .setDestinationInExternalFilesDir(
                        this.context, 
                        this.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/YoutubeThumbnails",
                        fileName + ".jpg"
                );

        manager.enqueue(request);
        return null;
    }

    @Override
    public void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context, "Image downloaded", Toast.LENGTH_SHORT).show();
    }

}
