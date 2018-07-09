package com.arcsoft.sudoku;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Handler handler = null;
//    SudokuElement[][] sudokuArray = null;
    //TextView[][] viewList = new TextView[9][9];
    EditText[][] viewList = new EditText[9][9];

    Sudoku sudoku = null;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1)
                {
                    _printInterResult();
                }
            }
        };

        sudoku = new Sudoku(handler);

        initSodokuView();
        initButtons();

    }

    public void initButtons()
    {
        Button btn_start = null;
        Button btn_clear = null;

        btn_start = findViewById(R.id.button_calculate);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < 9; i ++)
                    for (int j = 0; j < 9; j ++)
                    {
                        int val = 0;
                        String inputStr = viewList[i][j].getText().toString();
                        Log.d("jjding","["+i+"]["+j+"] = " + inputStr);
                        if (0 != inputStr.compareTo("")) {
                            val = Integer.parseInt(inputStr);
                            sudoku.setInitValue(i,j, val);
                        }
                        //sudokuArray[i][j] = new SudokuElement(initValueArr[i][j], flag);
                    }

                new Thread(){
                    @Override
                    public void run() {
                        //super.run();
                        sudoku.calulate();
                    }
                }.start();
            }
        });

        btn_clear = findViewById(R.id.button_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sudoku.clear();
                clearSodokuView();
            }
        });

    }

    public void clearSodokuView()
    {
        for (int i = 0; i < 9; i ++)
            for (int j = 0; j < 9; j ++){
                viewList[i][j].setText("");
                viewList[i][j].setTextColor(Color.RED);
                viewList[i][j].setTextSize(15);
            }
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
                        viewList[i][j].setText(String.format("%s",calVal));
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

                    viewList[i][j].setTextSize(8);
                    viewList[i][j].setTextColor(Color.BLACK);
                    viewList[i][j].setText(range);
                    viewList[i][j].invalidate();

                }
            }
    }

    public void initSodokuView()
    {
        sudoku.clear();

        Resources res = getResources();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j ++) {
                int id = res.getIdentifier("textView" + i + j, "id", getPackageName());//R.id.textView36
                viewList[i][j] = findViewById(id);
            }
        }


        for (int i = 0; i < 9; i ++)
            for (int j = 0; j < 9; j ++)
            {
                //设置只能输入数字
                viewList[i][j].setInputType(InputType.TYPE_CLASS_NUMBER);

                //调整view背景色
                viewList[i][j].setBackgroundColor(0xffafafaf);

                //调整view 大小
                ViewGroup.LayoutParams p=viewList[i][j].getLayoutParams();
                p.height = 90;
                p.width = 90;
                viewList[i][j].setLayoutParams(p);

                //调整默认字体大小
                viewList[i][j].setPadding(0,0,0,0);
                //viewList[i][j].setTextSize(8);
                viewList[i][j].setTextColor(Color.RED);
                viewList[i][j].setTextSize(15);

                //调整数值居中显示
                viewList[i][j].setGravity(Gravity.CENTER);

                //填入初始数值
                if (sudoku.getValue(i,j) > 0) {
                    viewList[i][j].setText(String.format("%s", sudoku.getValue(i,j)));
                    viewList[i][j].setTextSize(15);
                    viewList[i][j].setTextColor(Color.RED);
                }
            }
    }
}
