package com.riskmatch;

/**
 * Created by Владимир on 22.08.2016.
 */
public interface ITable {
    /**
     * Sets the value of the column to specified value.
     *
     * @param cell  string in cell string format
     * @param value positive integer [1, 1_000_000_000]
     */
    void set(Cell cell, long value);

    /**
     * Gets the value from the specified cell. if the cell is empty – return 0. *
     *
     * @param cell cell reference
     * @return cell value, or 0 if cell is empty
     */
    long get(Cell cell);

    /**
     * Gets the sum of values in the range [fromCell:toCell].
     *
     * @param fromCell “from” cell reference
     * @param toCell   “to” cell reference
     * @return sum of values in the range
     */
    long sum(Cell fromCell, Cell toCell);
    /**
     * Gets the average of values in the range [fromCell:toCell].
     * Empty cells should be treated as "0".
     *
     * @param fromCell “from” cell reference
     * @param toCell “to” cell reference
     * @return average of values in the range
     */
}