package com.example.game;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class GuessNumber extends AppCompatActivity implements View.OnClickListener {

    TextView result, txv;
    ImageButton submit, back, reset, hint, help;
    EditText input;
    int[] answer;
    int times;
    String string;
    boolean end;

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
        setContentView(R.layout.activity_guess_number);
        txv = findViewById(R.id.textView);
        result = findViewById(R.id.textView2);
        result.setMovementMethod(ScrollingMovementMethod.getInstance());
        submit = findViewById(R.id.imageButton21);
        submit.setOnClickListener(this);
        back = findViewById(R.id.imageButton);
        back.setOnClickListener(this);
        reset = findViewById(R.id.imageButton2);
        reset.setOnClickListener(this);
        hint = findViewById(R.id.imageButton3);
        hint.setOnClickListener(this);
        help = findViewById(R.id.imageButton4);
        help.setOnClickListener(this);
        input = findViewById(R.id.editText);
        init();
    }

    private void init() {
        answer = new int[4];
        times = 0;
        string = new String();
        end = false;
        input.setEnabled(true);
        submit.setEnabled(true);
        hint.setBackgroundResource(R.drawable.hint);
        hint.setEnabled(true);
        result.setText("");
        txv.setText("剩 " + (10 - times) + " 次");
        GenerateAnswer();
    }

    private void GenerateAnswer() {
        boolean breakflag = true;
        for (int i = 0; i < 4; i++) {
            do {
                breakflag = true;
                answer[i] = (int) (Math.random() * 10);//產生0~9的亂數存入答案陣列中
                for (int j = 0; j < i; j++) {
                    if (answer[i] == answer[j]) //判斷是否跟前面重複
                    {
                        breakflag = false;
                        break;
                    }
                }
            } while (!breakflag);
            string += answer[i];
        }

    }

    private boolean Compare() {
        String input_str = input.getText().toString();
        input.setText("");
        String str = new String();
        int guess_str = Integer.parseInt(input_str);
        int[] guess = new int[4];
        guess[0] = guess_str / 1000;
        guess[1] = (guess_str % 1000) / 100;
        guess[2] = (guess_str % 100) / 10;
        guess[3] = guess_str % 10;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < i; j++) {
                if (guess[i] == guess[j]) //判斷是否重複
                {
                    Toast.makeText(this, "請輸入四位不重複數字", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        str = result.getText().toString() + "\n";
        int acount = 0, bcount = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (guess[i] == answer[j] && i == j)//當數字相同且位置相同
                {
                    acount++;//A計數器+1
                    break;//跳出內迴圈
                }
                if (guess[i] == answer[j] && i != j)//當數字相同但位置不同
                {
                    bcount++;//B計數器+1
                    break;//跳出內迴圈
                }
            }
            str += guess[i] + " ";
        }
        str += "-> " + acount + " A " + bcount + " B ";
        result.setText(str);
        times++;
        txv.setText("剩 " + (10 - times) + " 次");
        if (acount == 4) {
            return true;
        } else
            return false;
    }

    @Override
    public void onClick(View v) {
        if (v == submit) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            if (input.getText().toString().length() != 4) {
                Toast.makeText(this, "請輸入四位不重複數字", Toast.LENGTH_SHORT).show();
                return;
            }
            if (times == 0)
                result.setText("你 的 答 案\n");
            end = Compare();
            if (end) {
                input.setEnabled(false);
                submit.setEnabled(false);
                new AlertDialog.Builder(this)
                        .setTitle("過關提示")
                        .setMessage("恭喜過關，你一共猜了" + times + "次！\n是否重新開始？")
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        init();
                                    }
                                })
                        .setNegativeButton("否",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        hint.setBackgroundResource(R.drawable.foreground);
                                        hint.setEnabled(false);
                                    }
                                }).show();
            } else if (times == 10) {
                hint.setBackgroundResource(R.drawable.foreground);
                hint.setEnabled(false);
                input.setEnabled(false);
                submit.setEnabled(false);
                Toast.makeText(GuessNumber.this, "遊戲失敗，正確答案為" + string, Toast.LENGTH_SHORT).show();
            }
        } else if (v == reset) {
            init();
        } else if (v == hint) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("確定要觀看答案？")
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    hint.setBackgroundResource(R.drawable.foreground);
                                    hint.setEnabled(false);
                                    Toast.makeText(GuessNumber.this, string, Toast.LENGTH_SHORT).show();
                                }
                            })
                    .setNegativeButton("否",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
        } else if (v == help) {
            new AlertDialog.Builder(this)
                    .setTitle("遊戲規則")
                    .setMessage("請猜四個數字，0~9不得重複\n輸入錯誤次數不計入猜的次數\nA代表數字及位置正確，B代表數字正確，但位置錯誤\n最多只能猜10次，若超過10次還未猜出即算輸，會公布正確答案")
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
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