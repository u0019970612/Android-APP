package com.example.game;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;

public class Function extends AppCompatActivity implements View.OnClickListener,MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    public static boolean another, play_music, isExit;
    ImageButton ab, tic, hammer, pair, exit;
    public static MediaPlayer mpe;
    Uri uri;

    @Override
    protected void onRestart() {
        super.onRestart();
        play_music = true;
        another = true;
        isExit = true;
        music_play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        another = false;
        isExit = false;
        music_play();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        ab = findViewById(R.id.imageButton14);
        ab.setOnClickListener(this);
        tic = findViewById(R.id.imageButton15);
        tic.setOnClickListener(this);
        hammer = findViewById(R.id.imageButton16);
        hammer.setOnClickListener(this);
        pair = findViewById(R.id.imageButton17);
        pair.setOnClickListener(this);
        exit = findViewById(R.id.imageButton20);
        exit.setOnClickListener(this);
        uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.music);
        mpe = new MediaPlayer();
        mpe.setOnCompletionListener(this);
        mpe.setOnErrorListener(this);
        mpe.setOnPreparedListener(this);
        try {
            mpe.setDataSource(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mpe.prepareAsync();
        another = true;
        isExit = true;
        play_music = true;
        music_play();
    }

    @Override
    public void onClick(View v) {
        if (v == ab) {
            play_music = false;
            Intent it = new Intent();
            it.setClass(this, GuessNumber.class);
            startActivity(it);
        } else if (v == tic) {
            play_music = false;
            Intent it = new Intent();
            it.setClass(this, TicTacToe.class);
            startActivity(it);
        } else if (v == hammer) {
            play_music = false;
            Intent it = new Intent();
            it.setClass(this, Hammer.class);
            startActivity(it);
        } else if (v == pair) {
            play_music = false;
            Intent it = new Intent();
            it.setClass(this, Pair.class);
            startActivity(it);
        } else if (v == exit) {
            new AlertDialog.Builder(this)
                    .setTitle("確認視窗")
                    .setMessage("確定要離開應用程式嗎?")
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(0);
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
        }
    }

    public static void music_play() {
        if ((play_music && !another && !isExit) || (!play_music && !another && isExit)) //畫面暫跳時
            mpe.pause();
        else
            mpe.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mpe.start();
        mpe.setLooping(true);
    }
}