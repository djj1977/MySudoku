package com.arcsoft.sudoku;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

public class Sudoku {
    SudokuElement[][] sudokuArray = null;
    Handler handler;

    public Sudoku(Handler handler)
    {
        this.handler = handler;
        initSudoku();
    }

    public void initSudoku()
    {
        sudokuArray = new SudokuElement[9][9];
        int[][] initValueArr = {
                {0, 5, 0,   0, 0, 0,    0, 2, 0},
                {4, 0, 0,   2, 0, 6,    0, 0, 7},
                {0, 0, 8,   0, 3, 0,    1, 0, 0},

                {0, 1, 0,   0, 0, 0,    0, 6, 0},
                {0, 0, 9,   0, 0, 0,    5, 0, 0},
                {0, 7, 0,   0, 0, 0,    0, 9, 0},

                {0, 0, 5,   0, 8, 0,    3, 0, 0},
                {7, 0, 0,   9, 0, 1,    0, 0, 4},
                {0, 2, 0,   0, 0, 0,    0, 7, 0}

//                {3, 0, 0, 0, 9, 4, 0, 1, 0},
//                {5, 8, 0, 0, 0, 0, 0, 4, 0},
//                {0, 0, 0, 3, 0, 0, 0, 0, 6},
//                {2, 5, 0, 0, 8, 0, 0, 0, 0},
//                {9, 0, 0, 0, 7, 0, 0, 0, 1},
//                {0, 0, 0, 0, 4, 0, 0, 8, 9},
//                {4, 0, 0, 0, 0, 9, 0, 0, 0},
//                {0, 1, 0, 0, 0, 0, 0, 6, 3},
//                {0, 9, 0, 7, 1, 0, 0, 0, 2}

        };
        for (int i = 0; i < 9; i ++)
            for (int j = 0; j < 9; j ++)
            {
                boolean flag = (initValueArr[i][j] > 0) ? true: false ;

                sudokuArray[i][j] = new SudokuElement(initValueArr[i][j], flag);
            }
    }

    public void clear()
    {
        for (int i = 0; i < 9; i ++)
            for (int j = 0; j < 9; j ++)
            {
                sudokuArray[i][j].setValue(0);
                sudokuArray[i][j].calculated = false;
                sudokuArray[i][j].hasOrignalVal = false;
                sudokuArray[i][j].getValueRange().clear();
            }
    }

    public int getValue(int row, int col)
    {
        return sudokuArray[row][col].getValue();
    }

    public void setInitValue(int row, int col, int val)
    {
        sudokuArray[row][col].setInitValue(val);
    }

    public boolean hasOrignalVal(int row, int col)
    {
        return sudokuArray[row][col].isHasOrignalVal();
    }

    public ArrayList<Integer> getValueRange(int row, int col)
    {
        return sudokuArray[row][col].getValueRange();
    }

    public void calulate()
    {
        boolean continueFilter = false;

        //filter1： 余数法，计算每个空格的初始取值范围
        //某一格可能的取值范围受其所在单元相关的其他20格的牵制（行，列，9宫格），取值不可能是这20格中已经有的数字（已填充或者计算出来）
        filer_fill_possible_value();
        printInterResult();

        do {
            continueFilter = false;

            //filter3：宫摒除法， 某个可能的取值数值只在9宫格中某个空格中有（不能有两个空格都显示这个可能的取值数值）
            if (true == filter_9grid()) {
                continueFilter = true;
                printInterResult();
            }

            //filter4: 行摒除法， 某个可能的取值数在某行中唯一存在， 则对应的空格就是这个数值
            if (true == filter_row()) {
                continueFilter = true;
                printInterResult();
            }

            //filter5: 列摒除法，某个可能的取值数在某列中唯一存在， 则对应的空格就是这个数值
            if (true == filter_column()) {
                continueFilter = true;
                printInterResult();
            }

            //filter： x-wing
            if (true == filter_by_x_wing()) {
                continueFilter = true;
                printInterResult();
            }

        }while (continueFilter);

        printInterResult();
    }

    private void printInterResult()
    {
        Message msg = handler.obtainMessage();
        msg.what = 1;
        handler.sendMessage(msg);

        try {
            Log.d("jjding", "sleep 1s");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void filer_fill_possible_value()
    {
        ArrayList<Integer> rangeList = null;

        for (int i = 0; i < 9; i ++)
            for (int j = 0; j < 9; j ++)
            {
                if (sudokuArray[i][j].getValue() > 0) //已经有初始值或者算出数值就不用再计算取值范围了
                    continue;
                //计算当前行可能的取值范围
                rangeList = sudokuArray[i][j].getValueRange();
                for (int val = 1; val <=9; val ++)
                {
                    if (isValueIn9Grid(val, i, j)) continue;
                    if (isValueInColumn(val, j)) continue;
                    if (isValueInRow(val, i)) continue;
                    if (sudokuArray[i][j].isValueInPossibleRange(val)) continue;

                    rangeList.add(val);
                }

                if (rangeList.size() == 1)//如果取值范围只有1个， 则这个就是算出来的数值，设置value
                {
                    int val = sudokuArray[i][j].getValueRange().get(0);
                    //设置该空格的数值
                    sudokuArray[i][j].setValue(val);
                    sudokuArray[i][j].calculated = true;
                }
            }

        filter_by_new_calculated_value();
    }

    private boolean filter_9grid()
    {
        boolean flag = false;

        if (isAllCaculated()) {
            return false;
        }

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (true == _filter_only_value_on_9grid(i, j)){//第i行，第j个 9宫格
                    flag = true;
                }

        if (true == filter_by_new_calculated_value()) {//再次过滤
            flag = true;
        }

        return flag;
    }


    private boolean filter_column()
    {
        boolean flag = false;

        if (isAllCaculated())
            return false;

        for (int j = 0; j < 9; j ++) {
            if (true == _filter_only_value_on_col(j))
                flag = true;
        }

        if (true == filter_by_new_calculated_value()) {//再次过滤
            flag = true;
            //printInterResult();
        }
        return flag;
    }

    private boolean filter_row()
    {
        boolean flag = false;

        if (isAllCaculated())
            return false;

        for (int i = 0; i < 9; i ++) {
            if (true == _filter_only_value_on_row(i))
                flag = true;
        }

        if (true == filter_by_new_calculated_value()) {//再次过滤
            flag = true;
            //printInterResult();
        }
        return flag;
    }


    private boolean filter_by_x_wing()
    {
        boolean flag = false;
        //关于x-wing 的解释， 查看下面网页的说明
        //http://www.sudokufans.org.cn/forums/topic/8/

        if (isAllCaculated())
            return false;

        LineStatitic[] lineArray = new LineStatitic[9]; //9行
        for (int i = 0; i < 9; i ++)
            lineArray[i] = new LineStatitic();

        //统计每行每个取值数字的分布
        for (int i = 0; i < 9; i ++)
        {
            for (int j = 0; j < 9; j++) {
                SudokuElement element = sudokuArray[i][j];
                if (element.getValue() > 0) continue;

                ArrayList<Integer> rangeList = element.getValueRange();
                for (Integer val: rangeList)
                    lineArray[i].addVal(val, i, j);
            }
        }


        //对每一个数字check 是否存在x-wing
        for (int val = 0; val < 9; val ++)
        {

            for (int lineindex = 0; lineindex < 9; lineindex ++) {
                BlankCoordinates lt, rt, lb,rb; //lefttop, righttop, leftbottom, rightbottom
                int left, right;
                int leftbottom = 0, rightbottom = 0;
                int leftcount = 0;
                int rightcount = 0;
                int paricount = 0;

                lt = new BlankCoordinates();
                rt = new BlankCoordinates();
                lb = new BlankCoordinates();
                rb = new BlankCoordinates();

                if (lineArray[lineindex].numArray[val].list.size() != 2)
                    continue;
                else {
                    left = lineArray[lineindex].numArray[val].list.get(0);
                    right = lineArray[lineindex].numArray[val].list.get(1);

                    lt.row = lineindex;
                    lt.col = left;
                    rt.row = lineindex;
                    rt.col = right;
                }
                for (int otherline = lineindex+1; otherline < 9; otherline ++)
                {
                    if (lineArray[otherline].numArray[val].list.size() != 2)
                        continue;

//                    if (lineArray[otherline].numArray[val].list.get(0) == left) leftcount ++;
//                    if (lineArray[otherline].numArray[val].list.get(1) == right) rightcount ++;

                    if ((lineArray[otherline].numArray[val].list.get(0) == left) &&
                            (lineArray[otherline].numArray[val].list.get(1) == right)) {
                        lb.row = otherline;
                        lb.col = left;
                        rb.row = otherline;
                        rb.col = right;
                        paricount++;
                    }
                }

//                if (leftcount == 1 && rightcount == 1 && paricount == 1) //find x-wing
                if (paricount == 1) //find x-wing
                {
                    flag = true;
                    //left 和 right 所在列 除了数字所在行， 其他空格的取值范围都不能取这个数字

                    for (int rowindex = 0; rowindex < 9; rowindex ++)
                    {
                        if (rowindex == lt.row) continue;
                        if (rowindex == lb.row) continue;

                        if(sudokuArray[rowindex][lt.col].getValue() > 0 &&
                                sudokuArray[rowindex][rt.col].getValue() > 0) //已有数值或者已经算出的数值忽略，
                            continue;

                        ArrayList<Integer> rangeList = null;
                        //去除第lt.col 列的x-wing找的的数字
                        rangeList =  sudokuArray[rowindex][lt.col].getValueRange();
                        for (int k = rangeList.size()-1; k >= 0; k --)
                        {
                            if ((val+1) == rangeList.get(k)) //val +1 是实际数字
                                rangeList.remove(k);
                        }
                        if (rangeList.size() == 1) {
                            sudokuArray[rowindex][lt.col].setValue(rangeList.get(0));
                            sudokuArray[rowindex][lt.col].calculated = true; //触发重计算
                        }

                        //去除第rt.col 列的x-wing找的的数字
                        rangeList =  sudokuArray[rowindex][rt.col].getValueRange();
                        for (int k = rangeList.size()-1; k >= 0; k --)
                        {
                            if ((val+1) == rangeList.get(k)) //val +1 是实际数字
                                rangeList.remove(k);
                        }
                        if (rangeList.size() == 1) {
                            sudokuArray[rowindex][rt.col].setValue(rangeList.get(0));
                            sudokuArray[rowindex][rt.col].calculated = true; //触发重计算
                        }
                    }
                }
            }
        }

        if (true == filter_by_new_calculated_value()) {//再次过滤
            flag = true;
        }
        return flag;
    }



    private boolean _filter_only_value_on_row(int row)
    {
        boolean flag = false;
        int count = 0;
        int index_col = 0;

        for (int k = 1; k <=9; k ++) {
            index_col = 0;
            count  = 0;

            for (int j = 0; j < 9; j++) {
                if (sudokuArray[row][j].getValue() > 0) continue;

                ArrayList<Integer> rangeList = sudokuArray[row][j].getValueRange();
                //if (rangeList.size() <= 0) continue;

                for (Integer val : rangeList) {
                    if (val.equals(k)) //取值范围内有包含K 这个数值
                    {
                        index_col = j;
                        count++;
                    }
                }
            }

            if (1 == count) //在这行中的每个空格的取值范围中仅有这个数值是唯一
            {
                //这个空格的真实值就是这个数值
                sudokuArray[row][index_col].setValue(k);
                sudokuArray[row][index_col].calculated = true;
                flag = true;
            }
        }

        return flag;
    }

    private boolean _filter_only_value_on_col(int col)
    {
        int count = 0;
        int index_row = 0, index_col = 0;
        boolean flag = false;

        for (int k = 1; k <=9; k ++) {
            index_col = 0;
            count = 0;

            for (int i = 0; i < 9; i++) {
                if (sudokuArray[i][col].getValue() > 0) continue;

                ArrayList<Integer> rangeList = sudokuArray[i][col].getValueRange();
                if (rangeList.size() <= 0) continue;

                for (Integer val : rangeList) {
                    if (val.equals(k)) //取值范围内有包含K 这个数值
                    {
                        index_row = i;
                        count++;
                    }
                }
            }
            if (1 == count) //在该列的每个空格的取值范围中仅有这个数值是唯一
            {
                //这个空格的真实值就是这个数值
                sudokuArray[index_row][col].setValue(k);
                sudokuArray[index_row][col].calculated = true;
                flag = true;
            }
        }

        return flag;
    }

     private boolean _filter_only_value_on_9grid(int gridrow, int gridcol)
    {
        int count = 0;
        int index_row = 0, index_col = 0;
        boolean flag= false;

        for (int k = 1; k <=9; k ++) {
            index_row = 0;
            index_col = 0;
            count  = 0;

            for (int i = gridrow * 3; i < gridrow * 3 + 3; i++)
                for (int j = gridcol * 3; j < gridcol * 3 + 3; j++) {
                    if (sudokuArray[i][j].getValue() > 0) continue;
                    ArrayList<Integer> rangeList = sudokuArray[i][j].getValueRange();
                    if (rangeList.size() <= 0) continue;
                    for(Integer val: rangeList)
                        if (val.equals(k)) //取值范围内有包含K 这个数值
                        {
                            index_row = i;
                            index_col = j;
                            count ++;
                        }
                }

            if (1 == count) //在9宫格的每个空格的取值范围中仅有这个数值是唯一
            {
                //这个空格的真实值就是这个数值
                sudokuArray[index_row][index_col].setValue(k);
                sudokuArray[index_row][index_col].calculated = true;
                flag = true;
            }
        }

        return flag;
    }

    //filter：根据某个空格新算出来数值，对其所在的行，列，九宫格内其他空格做取值范围缩减
    private boolean filter_by_new_calculated_value()
    {
        boolean cleared = false;
        //do {
        for (int i = 0; i <9; i ++)
            for (int j = 0; j < 9; j ++)
            {
                if (sudokuArray[i][j].calculated) //在各种filter下也可能算出真实数值
                {
                    //int val = sudokuArray[i][j].getValue();
                    //对应行，列，9宫格 的空格的取值范围去除这个数值
                    clearPossibleValue(i, j);//, val);
                    sudokuArray[i][j].calculated = false; //已经被使用过了， 就当成已存在的数值
                    cleared = true;
                }
            }

        return cleared;
    }

    //缩减该空格对应的20格空格的各自取值范围
    private void clearPossibleValue(int row, int col)//, int val)
    {
        int val = 0;
        SudokuElement orgElement = sudokuArray[row][col];
        val = orgElement.getValue();
        if (0 == val)
            return; //当前空格并没有计算出来的数值， 直接返回

        //clear row
        for (int i = 0; i < 9; i ++)
        {
            SudokuElement element = sudokuArray[row][i];
            if (element.getValue() > 0) continue;

            element.reduceRange(val);
            clearPossibleValue(row, i);

        }

        //clear column
        for (int i = 0; i < 9; i ++)
        {
            SudokuElement element = sudokuArray[i][col];
            if (element.getValue() > 0) continue;

            element.reduceRange(val);
            clearPossibleValue(i, col);
        }

        //clear 9宫格
        for (int i = row/3 * 3; i < row/3*3 + 3; i ++ )
            for (int j = col/3*3; j < col/3*3 + 3; j ++)
            {
                SudokuElement element = sudokuArray[i][j];
                if (element.getValue() > 0) continue;

                element.reduceRange(val);
                clearPossibleValue(i, j);
            }
    }


    private boolean isValueInRow(int value, int row)
    {
        for (int i = 0 ; i < 9; i ++)
            if (sudokuArray[row][i].getValue() == value)
                return true;
        return false;
    }

    private boolean isValueInColumn(int value, int column)
    {
        for (int i = 0 ; i < 9; i ++)
            if (sudokuArray[i][column].getValue() == value)
                return true;
        return false;
    }

    private boolean isValueIn9Grid(int value, int row, int column)
    {
        //先计算出给点的空格在那个9宫格

        int startrow ;
        int startcol;

        startrow = row / 3 * 3;
        startcol = column / 3 * 3;

        for (int i = startrow; i < startrow+3; i ++)
            for (int j = startcol; j < startcol+3; j++)
            {
                if (value == sudokuArray[i][j].getValue())
                    return true;
            }

        return false;
    }

    private boolean isAllCaculated()
    {
        boolean flag = false;
        int calculatedNum = 0;

        for (int i = 0; i < 9; i ++)
            for (int j = 0; j < 9; j ++){
                if (sudokuArray[i][j].getValue() > 0)
                    calculatedNum ++;
            }

        if (81 == calculatedNum)
            flag = true;

        return flag;
    }
}

class LineStatitic{
    NumStatistic[] numArray = null;
    public LineStatitic()
    {
        numArray = new NumStatistic[9];
        for (int i = 0; i < 9; i ++)
            numArray[i] = new NumStatistic(i+1);
    }

    public void addVal(int val, int row, int col)
    {
        numArray[val-1].addVal(val, row, col); //array index from 0 to 8, while value from 1 to 9
    }
}
class NumStatistic
{
    int value; //from 1 to 9
    ArrayList<Integer> list = null; //这个数值分布在哪些空格
    public NumStatistic(int val)
    {
        value = val;
        list = new ArrayList<Integer>();
    }

    public void addVal(int val, int row, int col)
    {
        list.add(col);
    }
}

class BlankCoordinates {
    int row;
    int col;
}