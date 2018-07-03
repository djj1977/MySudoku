package com.arcsoft.sudoku;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Handler handler = null;
//    SudokuElement[][] sudokuArray = null;
    TextView[][] viewList = new TextView[9][9];
    Button btn_start = null;
    Sudoku sudoku = null;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.d("jjding", "refresh begin");
                if (msg.what == 1)
                {
                    Log.d("jjding", "receive refresh request");
                    _printInterResult();
                    Log.d("jjding", "refresh finished");
                }
            }
        };

        sudoku = new Sudoku(handler);
        sudoku.initSudoku();
        initElementView();




        btn_start = findViewById(R.id.button_calculate);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(){
                    @Override
                    public void run() {
                        //super.run();
                        sudoku.calulate();
                    }
                }.start();
            }
        });


    }



    //打印结果
    public void _printInterResult()
    {

        for (int i = 0; i < 9; i ++)
            for (int j = 0; j < 9; j ++) {
                if (sudoku.getValue(i,j) > 0 ) {
                    if (sudoku.hasOrignalVal(i,j)) //初始已经填好
                        continue;//不用打印，已经算出
                    else {
                        int calVal = sudoku.getValue(i,j);

                        //调整默认字体大小
                        viewList[i][j].setTextSize(15);
                        viewList[i][j].setTextColor(Color.BLUE);
                        viewList[i][j].setText(Integer.toString(calVal));
                        Log.d("jjding","set textview["+i+"]["+j+"]");
                        viewList[i][j].invalidate();
                    }
                }
                else {
                    ArrayList<Integer> rangeList = sudoku.getValueRange(i,j);
                    int num = rangeList.size();
                    if (num <= 0) continue;
                    String range = "";
                    for (int k = 0; k < num; k++)
                        range += Integer.toString(rangeList.get(k));
                    viewList[i][j].setText(range);
                    viewList[i][j].invalidate();

                }
            }
    }






    public void initElementView()
    {

//        String viewId;
        viewList[0][0] = findViewById(R.id.textView00);
        viewList[0][1] = findViewById(R.id.textView01);
        viewList[0][2] = findViewById(R.id.textView02);
        viewList[0][3] = findViewById(R.id.textView03);
        viewList[0][4] = findViewById(R.id.textView04);
        viewList[0][5] = findViewById(R.id.textView05);
        viewList[0][6] = findViewById(R.id.textView06);
        viewList[0][7] = findViewById(R.id.textView07);
        viewList[0][8] = findViewById(R.id.textView08);

        viewList[1][0] = findViewById(R.id.textView10);
        viewList[1][1] = findViewById(R.id.textView11);
        viewList[1][2] = findViewById(R.id.textView12);
        viewList[1][3] = findViewById(R.id.textView13);
        viewList[1][4] = findViewById(R.id.textView14);
        viewList[1][5] = findViewById(R.id.textView15);
        viewList[1][6] = findViewById(R.id.textView16);
        viewList[1][7] = findViewById(R.id.textView17);
        viewList[1][8] = findViewById(R.id.textView18);

        viewList[2][0] = findViewById(R.id.textView20);
        viewList[2][1] = findViewById(R.id.textView21);
        viewList[2][2] = findViewById(R.id.textView22);
        viewList[2][3] = findViewById(R.id.textView23);
        viewList[2][4] = findViewById(R.id.textView24);
        viewList[2][5] = findViewById(R.id.textView25);
        viewList[2][6] = findViewById(R.id.textView26);
        viewList[2][7] = findViewById(R.id.textView27);
        viewList[2][8] = findViewById(R.id.textView28);

        viewList[3][0] = findViewById(R.id.textView30);
        viewList[3][1] = findViewById(R.id.textView31);
        viewList[3][2] = findViewById(R.id.textView32);
        viewList[3][3] = findViewById(R.id.textView33);
        viewList[3][4] = findViewById(R.id.textView34);
        viewList[3][5] = findViewById(R.id.textView35);
        viewList[3][6] = findViewById(R.id.textView36);
        viewList[3][7] = findViewById(R.id.textView37);
        viewList[3][8] = findViewById(R.id.textView38);

        viewList[4][0] = findViewById(R.id.textView40);
        viewList[4][1] = findViewById(R.id.textView41);
        viewList[4][2] = findViewById(R.id.textView42);
        viewList[4][3] = findViewById(R.id.textView43);
        viewList[4][4] = findViewById(R.id.textView44);
        viewList[4][5] = findViewById(R.id.textView45);
        viewList[4][6] = findViewById(R.id.textView46);
        viewList[4][7] = findViewById(R.id.textView47);
        viewList[4][8] = findViewById(R.id.textView48);

        viewList[5][0] = findViewById(R.id.textView50);
        viewList[5][1] = findViewById(R.id.textView51);
        viewList[5][2] = findViewById(R.id.textView52);
        viewList[5][3] = findViewById(R.id.textView53);
        viewList[5][4] = findViewById(R.id.textView54);
        viewList[5][5] = findViewById(R.id.textView55);
        viewList[5][6] = findViewById(R.id.textView56);
        viewList[5][7] = findViewById(R.id.textView57);
        viewList[5][8] = findViewById(R.id.textView58);

        viewList[6][0] = findViewById(R.id.textView60);
        viewList[6][1] = findViewById(R.id.textView61);
        viewList[6][2] = findViewById(R.id.textView62);
        viewList[6][3] = findViewById(R.id.textView63);
        viewList[6][4] = findViewById(R.id.textView64);
        viewList[6][5] = findViewById(R.id.textView65);
        viewList[6][6] = findViewById(R.id.textView66);
        viewList[6][7] = findViewById(R.id.textView67);
        viewList[6][8] = findViewById(R.id.textView68);

        viewList[7][0] = findViewById(R.id.textView70);
        viewList[7][1] = findViewById(R.id.textView71);
        viewList[7][2] = findViewById(R.id.textView72);
        viewList[7][3] = findViewById(R.id.textView73);
        viewList[7][4] = findViewById(R.id.textView74);
        viewList[7][5] = findViewById(R.id.textView75);
        viewList[7][6] = findViewById(R.id.textView76);
        viewList[7][7] = findViewById(R.id.textView77);
        viewList[7][8] = findViewById(R.id.textView78);

        viewList[8][0] = findViewById(R.id.textView80);
        viewList[8][1] = findViewById(R.id.textView81);
        viewList[8][2] = findViewById(R.id.textView82);
        viewList[8][3] = findViewById(R.id.textView83);
        viewList[8][4] = findViewById(R.id.textView84);
        viewList[8][5] = findViewById(R.id.textView85);
        viewList[8][6] = findViewById(R.id.textView86);
        viewList[8][7] = findViewById(R.id.textView87);
        viewList[8][8] = findViewById(R.id.textView88);

        for (int i = 0; i < 9; i ++)
            for (int j = 0; j < 9; j ++)
            {

                //调整view背景色
                viewList[i][j].setBackgroundColor(0xffafafaf);

                //调整view 大小
                ViewGroup.LayoutParams p=viewList[i][j].getLayoutParams();
                p.height = 90;
                p.width = 90;
                viewList[i][j].setLayoutParams(p);

                //调整默认字体大小
                viewList[i][j].setTextSize(10);

                //调整数值居中显示
                viewList[i][j].setGravity(Gravity.CENTER);
                //填入初始数值
                if (sudoku.getValue(i,j) > 0) {
                    //viewList[i][j].setText(Integer.toString(sudoku.getValue(i,j)));
                    viewList[i][j].setText(String.format("%s", sudoku.getValue(i,j)));
                    viewList[i][j].setTextSize(15);
                    viewList[i][j].setTextColor(Color.RED);
                }
            }
    }
}
