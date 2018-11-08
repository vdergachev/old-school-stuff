package com.riskmatch;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Владимир on 22.08.2016.
 */
public class Table implements ITable {

    private Map<Integer, Row> rows = new HashMap();

    public void set(Cell cell, long value) {
        int aRow = cell.getRow();
        int aCol = cell.getColumn();
        if(rows.containsKey(aRow)){
            Row row = rows.get(aRow);
            row.put(aCol, value);
        }else{
            Row row = new Row();
            row.put(aCol, value);
            rows.put(aRow, row);
        }

    }

    public long get(Cell cell) {
        int aRow = cell.getRow();
        int aCol = cell.getColumn();
        if(rows.containsKey(aRow)){
            Row row = rows.get(aRow);
            if(row.containsKey(aCol))
                return row.get(aCol);
        }
        return 0;
    }

    public long sum(Cell fromCell, Cell toCell) {
        BigInteger result = BigInteger.valueOf(0);

        int aRow1 = fromCell.getRow();
        int aCol1 = fromCell.getColumn();

        int aRow2 = toCell.getRow();
        int aCol2 = toCell.getColumn();

        for(int r = aRow1; r < aRow2; r++){
            for(int c = aCol1; c < aCol2; c++){
                Cell cell = new Cell();
                cell.setColumn(c);
                cell.setRow(r);
                long val = get(cell);
                result = result.add(BigInteger.valueOf(val));
            }
        }
        return result.longValue();
    }

    long avg(Cell fromCell, Cell toCell){
        int aRow1 = fromCell.getRow();
        int aCol1 = fromCell.getColumn();

        int aRow2 = toCell.getRow();
        int aCol2 = toCell.getColumn();

        long aSum = sum(fromCell, toCell);

        return (long)aSum/((aRow2 - aRow1)*(aCol2 - aCol1));
    }

    //Col -> Val
    private class Row extends HashMap<Integer, Long> {
    }
}
