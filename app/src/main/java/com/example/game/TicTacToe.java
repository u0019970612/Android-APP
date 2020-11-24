package com.example.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TicTacToe extends AppCompatActivity implements View.OnClickListener {
    ImageView I;
    ImageButton back, reset;
    ImageButton btn[][] = new ImageButton[3][3];
    TextView T, N;
    int btn_id[][] = new int[3][3];
    int count;
    private boolean noughtsTurn;
    private char board[][] = new char[3][3];

    @Override
    protected void onRestart() {
        super.onRestart();
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
        setContentView(R.layout.activity_tic_tac_toe);
        T = findViewById(R.id.textView3);
        N = (TextView) findViewById(R.id.textView4);
        I = findViewById(R.id.imageView);
        back = findViewById(R.id.imageButton18);
        back.setOnClickListener(this);
        reset = findViewById(R.id.imageButton19);
        reset.setOnClickListener(this);
        btn_id[0][0] = R.id.imageButton5;
        btn_id[0][1] = R.id.imageButton6;
        btn_id[0][2] = R.id.imageButton7;
        btn_id[1][0] = R.id.imageButton8;
        btn_id[1][1] = R.id.imageButton9;
        btn_id[1][2] = R.id.imageButton10;
        btn_id[2][0] = R.id.imageButton11;
        btn_id[2][1] = R.id.imageButton12;
        btn_id[2][2] = R.id.imageButton13;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++)
                btn[i][j] = findViewById(btn_id[i][j]);
        }
        init();
    }

    private void init() {
        count = 0;
        noughtsTurn = true;
        I.setImageResource(R.drawable.o);
        resetButtons();
        setupOnClickListeners();
        T.setTextColor(Color.BLACK);
        T.setTextSize(24);
        T.setText("目 前 玩 家");
        N.setText("");
    }

    private boolean checkWinner(char[][] board, int size, char player) {
        // check each column
        for (int x = 0; x < size; x++) {
            int total = 0;
            for (int y = 0; y < size; y++) {
                if (board[x][y] == player)
                    total++;
            }
            if (total >= size)
                return true; // they win
        }

        // check each row
        for (int y = 0; y < size; y++) {
            int total = 0;
            for (int x = 0; x < size; x++) {
                if (board[x][y] == player)
                    total++;
            }
            if (total >= size)
                return true; // they win
        }

        // forward diag
        int total = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == y && board[x][y] == player)
                    total++;
            }
        }
        if (total >= size)
            return true; // they win

        // backward diag
        total = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x + y == size - 1 && board[x][y] == player)
                    total++;
            }
        }
        if (total >= size)
            return true; // they win

        return false; // nobody won
    }

    private boolean checkWin() {
        char winner = '\0';
        if (checkWinner(board, 3, 'X')) {
            winner = 'X';
        } else if (checkWinner(board, 3, 'O')) {
            winner = 'O';
        }

        if (winner == '\0') {
            return false; // nobody won
        } else {
            T.setTextColor( Color.parseColor("#3F51B5"));
            T.setTextSize(30);
            T.setText("WINNER");
            return true;
        }
    }

    private void resetButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
                btn[i][j].setImageResource(R.drawable.foreground);
                btn[i][j].setEnabled(true);
            }
        }
    }

    private void disableButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                btn[i][j].setEnabled(false);
            }
        }
    }

    private void setupOnClickListeners() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                btn[i][j].setOnClickListener(new PlayOnClick(i, j));
            }
        }
    }

    private class PlayOnClick implements View.OnClickListener {

        private int x = 0;
        private int y = 0;

        public PlayOnClick(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof ImageButton) {
                ImageButton B = (ImageButton) view;
                board[x][y] = noughtsTurn ? 'O' : 'X';
                B.setImageResource(noughtsTurn ? R.drawable.o : R.drawable.x);
                B.setEnabled(false);
                noughtsTurn = !noughtsTurn;
                count++;
                // check if anyone has won
                if (checkWin()) {
                    disableButtons();
                } else {
                    if (count == 9) {
                        TextView T = (TextView) findViewById(R.id.textView4);
                        T.setText("Nobody wins");
                    } else
                        I.setImageResource(noughtsTurn ? R.drawable.o : R.drawable.x);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == reset) {
            init();
        } else if (v == back) {
            Function.play_music = true;
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return true;
    }
}