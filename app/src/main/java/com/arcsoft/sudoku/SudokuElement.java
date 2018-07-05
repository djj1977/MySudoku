package com.arcsoft.sudoku;

import java.util.ArrayList;

public class SudokuElement {
    public int value;
    public boolean calculated = false;
    public boolean hasOrignalVal = false;
    public int row;
    public int col;

    ArrayList<Integer> rangeList = null;

    public SudokuElement(int initvalue, boolean orignalVal)
    {
        value = initvalue;
        hasOrignalVal = orignalVal;
        rangeList = new ArrayList<Integer>();
//        valueRange = new int[9];
//        for(int i = 0;i <9; i ++)
//            valueRange[i] = i+1;
    }
    public SudokuElement(int initvalue)
    {
        value = initvalue;

        rangeList = new ArrayList<Integer>();
//        valueRange = new int[9];
//        for(int i = 0;i <9; i ++)
//            valueRange[i] = i+1;
    }

    public boolean isHasOrignalVal() {
        return hasOrignalVal;
    }

    public boolean isOnlyValue()
    {
        if (value > 0)
            return true;
        else
            return false;

//        int num = 0;
//        for (int i = 0; i < 9; i ++)
//            if (valueRange[i] > 0)
//                num ++;
//
//        if (num > 1) //取值数至少在2个以上
//            return true;
//        else
//            return false;

    }

    public void setValue(int val)
    {
        value = val;
    }

    public int getValue()
    {
//        int value = 0;
//        int num = 0;
//        for (int i = 0; i < 9; i ++) {
//            if (valueRange[i] > 0) {
//                num++;
//                value = valueRange[i];
//            }
//        }
//
//        if (num > 1) //至少有2个取值可能， 所以没有唯一数值
//            value = 0;

        return value;
    }

    ArrayList<Integer> getValueRange()
    {
        return rangeList;
//        ArrayList<Integer> rangeList = new ArrayList<Integer>();
//        for (int i = 0; i < 9; i ++) {
//            if (valueRange[i] > 0) {
//                rangeList.add(valueRange[i]);
//            }
//        }
//        return rangeList;
    }

    public boolean isValueInPossibleRange(int val)
    {
        //SudokuElement element
        int num = getValueRange().size();
        if(num <= 0 ) return false;

        ArrayList rangeList = getValueRange();
        for (int i = 0; i < num; i ++)
            if (rangeList.get(i).equals(val))
                return true;

        return false;
    }

    //返回range的范围， 即还有几个可能的取值
    int reduceRange(int val)
    {
        ArrayList<Integer> rangeList = getValueRange();
        int num = rangeList.size();
        for (int index = num-1; index >= 0; index --)
        {
            if (rangeList.get(index).equals(val))
                rangeList.remove(index);
        }

        if (rangeList.size() == 1){ //only one, means calculated
            calculated = true;
            setValue(rangeList.get(0));
        }

        num = rangeList.size();

        return num;
    }

}
