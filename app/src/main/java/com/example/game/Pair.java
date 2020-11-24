package com.example.game;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

public class Pair extends AppCompatActivity implements View.OnClickListener {
    ImageButton back, reset;
    private Chronometer chronometer;
    private static int rowCount = 4;
    private static int columeCount = 4;
    private static int items;
    private Context context;
    private Drawable backImage;
    private int[][] cards;
    private List<Drawable> images;
    private Card firstCard;
    private Card seconedCard;
    private ButtonListener buttonListener;
    private static Object lock = new Object();
    int pairCount;
    private TableLayout mainTable;
    private UpdateCardsHandler handler;

    @Override
    protected void onRestart() {
        super.onRestart();
        initilizeGame();
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
        handler = new UpdateCardsHandler();
        loadImages();
        setContentView(R.layout.activity_pair);
        back = findViewById(R.id.imageButton22);
        back.setOnClickListener(this);
        reset = findViewById(R.id.imageButton23);
        reset.setOnClickListener(this);
        backImage = getResources().getDrawable(R.drawable.empty);
        buttonListener = new ButtonListener();
        mainTable = (TableLayout) findViewById(R.id.MyTableLayout);
        context = mainTable.getContext();
        chronometer = (Chronometer) findViewById(R.id.MyChronometer);
        chronometer.setFormat("遊 戲 時 間  %s");
        initilizeGame();
    }

    private void initilizeGame() {
        cards = new int[columeCount][rowCount];
        items = (rowCount * columeCount) / 2; // 記錄可配對個數

        mainTable.removeAllViews();

        for (int y = 0; y < rowCount; y++) {
            mainTable.addView(createRow(y));
        }

        firstCard = null;
        loadCards(); // 產生卡片
        pairCount = 0;

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    private void loadImages() {
        images = new ArrayList<Drawable>();

        images.add(getResources().getDrawable(R.drawable.item01));
        images.add(getResources().getDrawable(R.drawable.item02));
        images.add(getResources().getDrawable(R.drawable.item03));
        images.add(getResources().getDrawable(R.drawable.item04));
        images.add(getResources().getDrawable(R.drawable.item05));
        images.add(getResources().getDrawable(R.drawable.item06));
        images.add(getResources().getDrawable(R.drawable.item07));
        images.add(getResources().getDrawable(R.drawable.item08));
    }

    private void loadCards() {
        try {

            int size = rowCount * columeCount;
            ArrayList<Integer> list = new ArrayList<Integer>();

            for (int i = 0; i < size; i++) {
                list.add(new Integer(i)); // 加入所有卡片編號
            }

            Random r = new Random();

            for (int i = size - 1; i >= 0; i--) {
                int t = 0;

                if (i > 0) {
                    t = r.nextInt(i); // 隨機取得編號
                }

                t = list.remove(t).intValue(); // 從 list 中取出編號
                cards[i % columeCount][i / columeCount] = t % (size / 2); // 將編號放入指定位置
            }

            // 再次洗牌
            for (int i = 0; i < rowCount; i++)
                for (int j = 0; j < columeCount; j++) {
                    int rc = r.nextInt(rowCount);
                    int cc = r.nextInt(columeCount);
                    int temp;

                    temp = cards[i][j];
                    cards[i][j] = cards[rc][cc];
                    cards[rc][cc] = temp;
                }
        } catch (Exception e) {
        }

    }

    private TableRow createRow(int y) {
        TableRow row = new TableRow(context);
        row.setHorizontalGravity(Gravity.CENTER);

        for (int x = 0; x < columeCount; x++) {
            row.addView(createImageButton(x, y));
        }
        return row;
    }

    private View createImageButton(int x, int y) {
        Button button = new Button(context);
        button.setBackground(backImage);
        button.setId(100 * x + y);
        button.setOnClickListener(buttonListener);
        return button;
    }

    class ButtonListener implements OnClickListener {
        public void onClick(View v) {
            synchronized (lock) {
                if (firstCard != null && seconedCard != null) {
                    return;
                }
                int id = v.getId();
                int x = id / 100;
                int y = id % 100;
                turnCard((Button) v, x, y);
            }
        }

        private void turnCard(Button button, int x, int y) {
            button.setBackground(images.get(cards[x][y]));

            if (firstCard == null) {
                firstCard = new Card(button, x, y);
            } else {
                if (firstCard.x == x && firstCard.y == y) {
                    return; // 選到相同的卡片則不動作
                }

                seconedCard = new Card(button, x, y);

                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            synchronized (lock) {
                                handler.sendEmptyMessage(0);
                            }
                        } catch (Exception e) {
                        }
                    }
                };

                Timer t = new Timer(false);
                t.schedule(tt, 500); // 停頓0.5秒
            }
        }
    }

    class UpdateCardsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            synchronized (lock) {
                checkCards();
            }
        }

        public void checkCards() {
            if (cards[seconedCard.x][seconedCard.y] == cards[firstCard.x][firstCard.y]) {
                firstCard.button.setEnabled(false);
                seconedCard.button.setEnabled(false);
                pairCount++;
                if (pairCount >= items) {
                    chronometer.stop();
                    new AlertDialog.Builder(Pair.this)
                            .setTitle("過關提示")
                            .setMessage("恭喜你完成所有配對！\n是否重新開始？")
                            .setIcon(R.drawable.ic_launcher_foreground)
                            .setPositiveButton("是",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            initilizeGame();
                                        }
                                    })
                            .setNegativeButton("否",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).show();
                }
            } else {
                seconedCard.button.setBackground(backImage);
                firstCard.button.setBackground(backImage);
            }
            firstCard = null;
            seconedCard = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == reset) {
            initilizeGame();
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