package com.example.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

public class Hammer extends AppCompatActivity implements View.OnClickListener {
    ImageButton back, reset, btStart;
    Boolean play;
    private ImageButton[][] view = new ImageButton[3][3];
    private TextView tvScore;

    private int time_s;   //難度時間
    private int time;   //地鼠出來時間
    private int score;  //成績，打地鼠個數
    private int lose;   //沒打到次數
    private int temp_i, temp_j;   //紀錄上一次出現的地鼠在數组view中的下標
    private int flag;   //默認為停止狀態，0開始，1結束，2運行

    @Override
    protected void onRestart() {
        super.onRestart();
        init();
        Function.another = false;
        Function.isExit = false;
        Function.play_music = false;
        Function.music_play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Function.isExit = true;
        Function.another = false;
        Function.music_play();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hammer);
        back = findViewById(R.id.imageButton24);
        back.setOnClickListener(this);
        reset = findViewById(R.id.imageButton25);
        reset.setOnClickListener(this);
        btStart = (ImageButton) findViewById(R.id.imageButton26);
        btStart.setOnClickListener(this);
        init();
    }

    private void init() {
        play = false;
        time_s = 1000;
        time = time_s;
        score = 0;
        lose = 0;
        temp_i = 3;
        temp_j = 3;
        flag = 1;
        view[0][0] = (ImageButton) findViewById(R.id.imageButton5);
        view[0][1] = (ImageButton) findViewById(R.id.imageButton6);
        view[0][2] = (ImageButton) findViewById(R.id.imageButton7);
        view[1][0] = (ImageButton) findViewById(R.id.imageButton8);
        view[1][1] = (ImageButton) findViewById(R.id.imageButton9);
        view[1][2] = (ImageButton) findViewById(R.id.imageButton10);
        view[2][0] = (ImageButton) findViewById(R.id.imageButton11);
        view[2][1] = (ImageButton) findViewById(R.id.imageButton12);
        view[2][2] = (ImageButton) findViewById(R.id.imageButton13);
        tvScore = (TextView) findViewById(R.id.textView5);
        tvScore.setText("分 數 ： " + score + " \n沒 打 中 ： " + lose);
        btStart.setImageResource(R.drawable.start);
        btStart.setEnabled(true);
        initbutton();
    }

    public void initbutton() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                view[i][j].setBackgroundResource(R.drawable.b);
                view[i][j].setClickable(false);
            }
        }
    }


    public void whackAMole(View view) {
        ImageButton I = (ImageButton) view;
        I.setBackgroundResource(R.drawable.c);
        view.setClickable(false);
        score += 2;
        lose--;
        if (score > 100)
            time = 600;
        else if (score > 80)
            time = 650;
        else if (score > 60)
            time = 700;
        else if (score > 40)
            time = 800;
        else if (score > 20)
            time = 900;
        else
            time = 1000;
    }

    class MyAsyncTask extends AsyncTask<String, Integer, String> {
        int holei, holej;

        @Override
        protected String doInBackground(String... strings) { //子線程
            //進入運行狀態
            while (flag != 1) {
                flag = 2;
                do {
                    double r = Math.random();
                    holei = ((int) (r * 10)) % 3;
                    r = Math.random();
                    holej = ((int) (r * 10)) % 3;
                    if (!(temp_i == holei && temp_j == holej)) {
                        try {
                            Thread.sleep(time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        publishProgress(holei, holej);
                    }
                } while (temp_i == holei && temp_j == holej);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {   //主線程
            if (flag == 0) {   //開始
                initbutton();
            } else if (flag == 2) {   //運行
                initbutton();
                tvScore.setText("分 數 ： " + score + " \n沒 打 中 ： " + lose);
                if (lose == 5) {
                    flag = 1;
                    btStart.setImageResource(R.drawable.stop);
                    btStart.setEnabled(false);
                    Toast.makeText(Hammer.this, "遊戲结束", Toast.LENGTH_SHORT).show();
                } else {
                    view[holei][holej].setBackgroundResource(R.drawable.a);
                    view[holei][holej].setClickable(true);
                    lose++;
                    temp_i = holei;
                    temp_j = holej;
                }
            } else if (flag == 1) { //結束
                initbutton();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == reset) {
            initbutton();
            init();
        } else if (v == back) {
            flag = 1;
            Function.play_music = true;
            finish();
        } else if (v == btStart) {
            if (!play) {
                btStart.setImageResource(R.drawable.pause);
                play = true;
                score = 0;
                flag = 0;  //開始
                MyAsyncTask MyAsyncTask = new MyAsyncTask();
                MyAsyncTask.execute();
            } else {
                btStart.setImageResource(R.drawable.stop);
                Toast.makeText(Hammer.this, "遊戲结束", Toast.LENGTH_SHORT).show();
                initbutton();
                btStart.setEnabled(false);
                score = 0;
                flag = 1;  //停止
                MyAsyncTask MyAsyncTask1 = new MyAsyncTask();
                MyAsyncTask1.execute();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return true;
    }
}