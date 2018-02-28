package com.example.vahan.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int c[][];
    private int i, j = 0;
    private Button b[][];
    private TextView textView;
    private AI ai;
    private ImageView locButton;
    private Animation animation2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast t = Toast.makeText(getApplicationContext(),R.string.name, Toast.LENGTH_LONG);
        t.setGravity(Gravity.BOTTOM,0,0);
        t.show();

        sksel();

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        locButton = (ImageView)findViewById(R.id.imageView2);

        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frcnel);
        animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotation);

        if (iv != null) {
            iv.setImageResource(R.drawable.tac);
            iv.startAnimation(animation1);
        }

        if (locButton != null) {
            locButton.setImageResource(R.drawable.buttonr);
        }
    }

    public void clickToPlay(View view) {
        locButton.startAnimation(animation2);
        sksel();
    }

    public void sksel() {
        ai = new AI();
        b = new Button[4][4];
        c = new int[4][4];
        textView = (TextView) findViewById(R.id.textView);
        b[1][3] = (Button) findViewById(R.id.button1);
        b[1][2] = (Button) findViewById(R.id.button2);
        b[1][1] = (Button) findViewById(R.id.button3);
        b[2][3] = (Button) findViewById(R.id.button4);
        b[2][2] = (Button) findViewById(R.id.button5);
        b[2][1] = (Button) findViewById(R.id.button6);
        b[3][3] = (Button) findViewById(R.id.button7);
        b[3][2] = (Button) findViewById(R.id.button8);
        b[3][1] = (Button) findViewById(R.id.button9);
        for (i = 1; i <= 3; i++) {
            for (j = 1; j <= 3; j++)
                c[i][j] = 2;
        }
        textView.setText(R.string.start_text);

        for (i = 1; i <= 3; i++) {
            for (j = 1; j <= 3; j++) {
                b[i][j].setOnClickListener(new MyClickListener(i, j));
                if (!b[i][j].isEnabled()) {
                    b[i][j].setText(" ");
                    b[i][j].setEnabled(true);
                }
            }
        }
    }

    class MyClickListener implements View.OnClickListener {
        int x;
        int y;
        MyClickListener(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public void onClick(View view) {
            if (b[x][y].isEnabled()) {
                b[x][y].setEnabled(false);
                b[x][y].setText("X");
                b[x][y].setTextColor(getResources().getColor(R.color.color));
                c[x][y] = 0;
                textView.setText("");
                if (!checkBoard()) {
                    ai.takeTurn();
                }
            }
        }
    }

    class AI {
        void takeTurn() {
            if (c[1][1] == 2 &&
                    ((c[1][2] == 0 && c[1][3] == 0) ||
                            (c[2][2] == 0 && c[3][3] == 0) ||
                            (c[2][1] == 0 && c[3][1] == 0))) {
                markSquare(1, 1);
            } else if (c[1][2] == 2 &&
                    ((c[2][2] == 0 && c[3][2] == 0) ||
                            (c[1][1] == 0 && c[1][3] == 0))) {
                markSquare(1, 2);
            } else if (c[1][3] == 2 &&
                    ((c[1][1] == 0 && c[1][2] == 0) ||
                            (c[3][1] == 0 && c[2][2] == 0) ||
                            (c[2][3] == 0 && c[3][3] == 0))) {
                markSquare(1, 3);
            } else if (c[2][1] == 2 &&
                    ((c[2][2] == 0 && c[2][3] == 0) ||
                            (c[1][1] == 0 && c[3][1] == 0))) {
                markSquare(2, 1);
            } else if (c[2][2] == 2 &&
                    ((c[1][1] == 0 && c[3][3] == 0) ||
                            (c[1][2] == 0 && c[3][2] == 0) ||
                            (c[3][1] == 0 && c[1][3] == 0) ||
                            (c[2][1] == 0 && c[2][3] == 0))) {
                markSquare(2, 2);
            } else if (c[2][3] == 2 &&
                    ((c[2][1] == 0 && c[2][2] == 0) ||
                            (c[1][3] == 0 && c[3][3] == 0))) {
                markSquare(2, 3);
            } else if (c[3][1] == 2 &&
                    ((c[1][1] == 0 && c[2][1] == 0) ||
                            (c[3][2] == 0 && c[3][3] == 0) ||
                            (c[2][2] == 0 && c[1][3] == 0))) {
                markSquare(3, 1);
            } else if (c[3][2] == 2 &&
                    ((c[1][2] == 0 && c[2][2] == 0) ||
                            (c[3][1] == 0 && c[3][3] == 0))) {
                markSquare(3, 2);
            } else if (c[3][3] == 2 &&
                    ((c[1][1] == 0 && c[2][2] == 0) ||
                            (c[1][3] == 0 && c[2][3] == 0) ||
                            (c[3][1] == 0 && c[3][2] == 0))) {
                markSquare(3, 3);
            } else {
                Random rand = new Random();

                int a = rand.nextInt(4);
                int b = rand.nextInt(4);
                while (a == 0 || b == 0 || c[a][b] != 2) {
                    a = rand.nextInt(4);
                    b = rand.nextInt(4);
                }
                markSquare(a, b);
            }
        }

        void markSquare(int x, int y) {
            b[x][y].setEnabled(false);
            b[x][y].setText("O");
            b[x][y].setTextColor(getResources().getColor(R.color.colorAccent));

            c[x][y] = 1;
            checkBoard();
        }
    }

    private boolean checkBoard() {
        boolean gameOver = false;
        if ((c[1][1] == 0 && c[2][2] == 0 && c[3][3] == 0)
                || (c[1][3] == 0 && c[2][2] == 0 && c[3][1] == 0)
                || (c[1][2] == 0 && c[2][2] == 0 && c[3][2] == 0)
                || (c[1][3] == 0 && c[2][3] == 0 && c[3][3] == 0)
                || (c[1][1] == 0 && c[1][2] == 0 && c[1][3] == 0)
                || (c[2][1] == 0 && c[2][2] == 0 && c[2][3] == 0)
                || (c[3][1] == 0 && c[3][2] == 0 && c[3][3] == 0)
                || (c[1][1] == 0 && c[2][1] == 0 && c[3][1] == 0)) {
            textView.setText(R.string.win_text);
            for (i = 1; i <= 3; i++) {
                for (j = 1; j <= 3; j++) {
                    b[i][j].setOnClickListener(new MyClickListener(i, j));
                    if (b[i][j].isEnabled()) {
                        b[i][j].setEnabled(false);
                    }
                }
            }
            gameOver = true;
        } else if ((c[1][1] == 1 && c[2][2] == 1 && c[3][3] == 1)
                || (c[1][3] == 1 && c[2][2] == 1 && c[3][1] == 1)
                || (c[1][2] == 1 && c[2][2] == 1 && c[3][2] == 1)
                || (c[1][3] == 1 && c[2][3] == 1 && c[3][3] == 1)
                || (c[1][1] == 1 && c[1][2] == 1 && c[1][3] == 1)
                || (c[2][1] == 1 && c[2][2] == 1 && c[2][3] == 1)
                || (c[3][1] == 1 && c[3][2] == 1 && c[3][3] == 1)
                || (c[1][1] == 1 && c[2][1] == 1 && c[3][1] == 1)) {
            textView.setText(R.string.lost_text);
            for (i = 1; i <= 3; i++) {
                for (j = 1; j <= 3; j++) {
                    b[i][j].setOnClickListener(new MyClickListener(i, j));
                    if (b[i][j].isEnabled()) {
                        b[i][j].setEnabled(false);
                    }
                }
            }
            gameOver = true;
        } else {
            boolean empty = false;
            for (i = 1; i <= 3; i++) {
                for (j = 1; j <= 3; j++) {
                    if (c[i][j] == 2) {
                        empty = true;
                        break;
                    }
                }
            }
            if (!empty) {
                gameOver = true;
                textView.setText(R.string.draw_text);
            }
        }
        return gameOver;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }
}