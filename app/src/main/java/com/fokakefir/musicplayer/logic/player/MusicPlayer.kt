package com.fokakefir.musicplayer.logic.player;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.model.Music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class MusicPlayer implements MediaPlayer.OnCompletionListener {


    // region 1. Decl and Init

    private MusicPlayerService service;
    private MusicPlayerListener listener;

    private MediaPlayer mediaPlayer;

    private Music currentMusic;
    private ArrayList<Music> musics;
    private ArrayList<Music> shuffleMusics;
    private ArrayList<Music> queueMusics;
    private boolean shuffle;
    private boolean repeat;
    private int playlistId;

    // endregion

    // region 2. Constructor

    public MusicPlayer(MusicPlayerService service, MusicPlayerListener listener) {
        this.service = service;
        this.listener = listener;
        this.queueMusics = new ArrayList<>();
        this.shuffle = false;
        this.repeat = false;
    }

    // endregion

    // region 3. MediaPlayer

    public void playMusicUri(Uri uri) {
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
            );
            this.mediaPlayer.setOnCompletionListener(this);
        } else {
            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
        }

        try {
            this.mediaPlayer.setDataSource(this.service, uri);
            this.mediaPlayer.prepare();
            this.mediaPlayer.start();

            this.listener.onPreparedMusic(
                    R.drawable.ic_baseline_pause_music,
                    this.currentMusic.title,
                    this.currentMusic.artist,
                    this.currentMusic.length,
                    this.playlistId,
                    this.mediaPlayer.getAudioSessionId()
            );
        } catch (IOException e) {
            e.printStackTrace();
            this.mediaPlayer = null;
        }
    }

    public void playMusicUri(Uri uri, Music music) {
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
            );
            this.mediaPlayer.setOnCompletionListener(this);
        } else {
            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
        }

        try {
            this.mediaPlayer.setDataSource(this.service, uri);
            this.mediaPlayer.prepare();
            this.mediaPlayer.start();

            this.listener.onPreparedMusic(
                    R.drawable.ic_baseline_pause_music,
                    music.title,
                    music.artist,
                    music.length,
                    this.playlistId,
                    this.mediaPlayer.getAudioSessionId()
            );
        } catch (IOException e) {
            e.printStackTrace();
            this.mediaPlayer = null;
        }
    }

    public void playMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.start();

            this.listener.onPlayMusic(R.drawable.ic_baseline_pause_music);
        }
    }

    public void pauseMusic() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.pause();

            this.listener.onPauseMusic(R.drawable.ic_baseline_play_music);
        }
    }

    public void previousMusic() {
        if (this.currentMusic != null) {
            int position = getCurrentMusicPosition() - 1;
            if (position >= 0 || (this.repeat && position == -1)) {
                this.currentMusic = getMusicFromPosition((position + this.musics.size()) % this.musics.size());
                Uri uri = Uri.parse(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                                "/YoutubeMusics/" + this.currentMusic.videoId + MainActivity.AUDIO_FORMAT
                );
                this.playMusicUri(uri);
            }
        }
    }

    public void nextMusic() {
        if (this.queueMusics != null && !this.queueMusics.isEmpty()){
            Music queueMusic = this.queueMusics.get(0);
            Uri uri = Uri.parse(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                            "/YoutubeMusics/" + queueMusic.videoId + MainActivity.AUDIO_FORMAT
            );
            this.playMusicUri(uri, queueMusic);
            this.queueMusics.remove(0);
        } else if (this.currentMusic != null) {
            int position = getCurrentMusicPosition() + 1;
            if (position < this.musics.size() || (this.repeat && position == this.musics.size())) {
                this.currentMusic = getMusicFromPosition(position % this.musics.size());
                Uri uri = Uri.parse(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                                "/YoutubeMusics/" + this.currentMusic.videoId + MainActivity.AUDIO_FORMAT
                );
                this.playMusicUri(uri);
            }
        }
    }

    public void stopMediaPlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;

            this.listener.onStopMediaPlayer(R.drawable.ic_baseline_play_music);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (getCurrentMusicPosition() < this.musics.size() - 1) {
            nextMusic();
        } else {
            if (this.repeat || (this.queueMusics != null && !this.queueMusics.isEmpty())) {
                nextMusic();
            } else {
                stopMediaPlayer();
            }
        }
    }

    // endregion

    // region 4. Getters and Setters

    private Music getMusicFromPosition(int index) {
        if (this.shuffle) {
            return this.shuffleMusics.get(index);
        } else {
            return this.musics.get(index);
        }
    }

    private int getCurrentMusicPosition() {
        if (!this.shuffle) {
            for (int ind = 0; ind < musics.size(); ind++) {
                if (this.currentMusic.id == this.musics.get(ind).id)
                    return ind;
            }
        } else {
            for (int ind = 0; ind < musics.size(); ind++) {
                if (this.currentMusic.id == this.shuffleMusics.get(ind).id)
                    return ind;
            }
        }
        return -1;
    }

    public int getTimePosition() {
        if (this.mediaPlayer != null) {
            return this.mediaPlayer.getCurrentPosition() / 1000;
        }
        return 0;
    }

    public Music getCurrentMusic() {
        return currentMusic;
    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public boolean isPlayable() {
        return (this.mediaPlayer != null);
    }

    public boolean isPlaying() {
        return this.mediaPlayer.isPlaying();
    }

    public void setCurrentMusic(Music currentMusic) {
        this.currentMusic = currentMusic;
    }

    public void setMusics(ArrayList<Music> musics) {
        this.musics = musics;
        this.shuffleMusics = new ArrayList<>(musics);
        Collections.shuffle(this.shuffleMusics);
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        if (this.shuffle && this.shuffleMusics != null)
            Collections.shuffle(this.shuffleMusics);
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setProgress(int progress) {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.seekTo(progress * 1000, MediaPlayer.SEEK_CLOSEST);
        }
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public void insertNewMusic(Music newMusic) {
        this.musics.add(newMusic);

        int index = ThreadLocalRandom.current().nextInt(0, this.shuffleMusics.size() + 1);
        this.shuffleMusics.add(index, newMusic);
    }

    public void addQueueMusic(Music queueMusic) {
        this.queueMusics.add(queueMusic);
    }

    // endregion

    // region 5. Listener

    public interface MusicPlayerListener {
        void onPreparedMusic(int imgResource, String title, String artist, int length, int playlistId, int audioSessionId);
        void onPlayMusic(int imgResource);
        void onPauseMusic(int imgResource);
        void onStopMediaPlayer(int imgResource);
    }

    // endregion

}
