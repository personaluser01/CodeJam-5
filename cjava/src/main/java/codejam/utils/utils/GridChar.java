package codejam.utils.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class GridChar {
    
    final static Logger log = LoggerFactory.getLogger(GridChar.class);
    
    private int rows;
    private final int cols;
    
    private final char[] grid;

    private char invalidSquare;

    public static GridChar buildFromScanner(Scanner scanner, int rows, int cols, char invalidSq) {

        GridChar g = new GridChar(rows, cols, invalidSq);

        for (int r = 0; r < rows; ++r) {
            String rowStr = scanner.next();

            while (rowStr.length() < cols) {
                rowStr += scanner.next();
            }
            for (int c = 0; c < cols; ++c) {
                char ch = rowStr.charAt(c);
                g.grid[g.getIndex(r, c)] = ch;
            }

        }

        return g;
    }
    
    public static GridChar buildFromScannerYZeroBottom(Scanner scanner, int rows, int cols, char invalidSq) {

        
        GridChar g = new GridChar(rows, cols, invalidSq);
        g.yZeroOnTop = false;

        for (int y = rows-1; y >= 0; --y) {
            String rowStr = scanner.next();

            while (rowStr.length() < cols) {
                rowStr += scanner.next();
            }
            for (int x = 0; x < cols; ++x) {
                char ch = rowStr.charAt(x);
                g.grid[g.getIndex(y, x)] = ch;
            }

        }

        return g;
    }

    public static GridChar buildFromBufferedReader(BufferedReader br, int rows, int cols, char invalidSq) {

        GridChar g = new GridChar(rows, cols, invalidSq);

        for (int r = 0; r < rows; ++r) {
            try {
                String rowStr = br.readLine();
                // log.debug(rowStr);
                for (int c = 0; c < cols; ++c) {
                    char ch = rowStr.charAt(c);

                    g.grid[g.getIndex(r, c)] = ch;

                }
            } catch (IOException ex) {
                log.error("ex", ex);
            }

        }

        return g;
    }
    
    public static GridChar  buildEmptyGrid( int rows, int cols, char invalidSq) {
        
        
        GridChar g = new GridChar(rows, cols, invalidSq);
        
        
        return g;
    }
 
    public GridChar(GridChar rhs) {
        this.rows = rhs.rows;
        this.cols = rhs.cols;
        this.yZeroOnTop = rhs.yZeroOnTop;
        this.grid = new char[rhs.grid.length];
        System.arraycopy(rhs.grid,0,this.grid,0,rhs.grid.length);
        this.invalidSquare = rhs.invalidSquare;
      
    }
    
    
    
    private GridChar(int rows, int cols, char invalidSq) {
        super();
        this.rows = rows;
        this.cols = cols;
        this.invalidSquare = invalidSq;
       
        
        grid = new char[rows*cols];
        Arrays.fill(grid, invalidSq);
        
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(cols, grid, rows);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GridChar other = (GridChar) obj;
        if (cols != other.cols)
            return false;
        
        return Objects.equal(rows, other.rows)
                && Objects.equal(cols, other.cols) &&
                Arrays.equals(grid, other.grid) &&
                Objects.equal(invalidSquare, other.invalidSquare);
                
    }
    
    public int getSize() {
        return grid.length;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    public int[] getRowCol(int index) {
        return new int[] { index / cols,  index % cols };
    }
    
    public int getIndex(int row, int col) {
        return col + row * cols;
    }
    
    public boolean isAdj8(int idx1, int idx2) {
        int row1 = idx1 / cols;
        int col1 = idx1 % cols;
        
        int row2 = idx2 / cols;
        int col2 = idx2 % cols;
        
        if (Math.abs(row1-row2) <= 1 && Math.abs(col1-col2) <= 1)
            return true;
        
        return false;
    }
    
    public Integer getIndex(int index, Direction dir) {
        int row = index / cols - dir.getDeltaY();
        int col = index % cols + dir.getDeltaX();
        
        if (row < 0 || row >= rows) {
            return null;
        }
        
        if (col < 0 || col >= cols) {
            return null;
        }
        
        return getIndex(row, col);
    }
    
    public Set<Integer> getIndexesOf(char type) {
        Set<Integer> ret = new HashSet<>();
        for(int i = 0; i < grid.length; ++i) {
            if (type == grid[i]) {
                ret.add(i);
            }
        }
        return ret;
    }
    
    public void setRow(int row, String rowStr) {
        Preconditions.checkArgument(rowStr.length() == cols);
        for(int c = 0; c < cols; ++c) {
            char ch = rowStr.charAt(c);
            grid[getIndex(row,c) ] = ch;
        }
    }
    
    public void setEntry(int index, char square) {
       
        grid[index] = square;
    }
    
    public void setEntry(int row, int col, char square) {
        setEntry(getIndex(row, col), square);
    }
    
    public char getEntry(int row, int col, Direction dir) {
        return getEntry(getIndex(row,col), dir);
    }
    
    public char getEntry(final int index) {
        return grid[index];
    }
    
    public char getEntry(final int index, Direction dir) {
        int row = yZeroOnTop ? index / cols - dir.getDeltaY()
        : index / cols + dir.getDeltaY();
        int col = index % cols + dir.getDeltaX();
        
        if (row < 0 || row >= rows) {
            return invalidSquare;
        }
        
        if (col < 0 || col >= cols) {
            return invalidSquare;
        }
        
        return getEntry(row,col);
        
    }
    
    public char getEntry(int row, int col) {
        Preconditions.checkArgument(row >= 0 && row < rows);
        Preconditions.checkArgument(col >= 0 && col < cols);
        
        return grid[getIndex(row,col)];
    }
    
    
    

    public char getInvalidSquare() {
        return invalidSquare;
    }


    public void setInvalidSquare(char invalidSquare) {
        this.invalidSquare = invalidSquare;
    }

    private static int printWidth = 4; 
    private boolean yZeroOnTop = true;
    
    

    public static int getPrintWidth() {
        return printWidth;
    }





    public static void setPrintWidth(int printWidth) {
        GridChar.printWidth = printWidth;
    }





    public boolean isyZeroOnTop() {
        return yZeroOnTop;
    }

    public void setyZeroOnTop(boolean yZeroOnTop) {
        this.yZeroOnTop = yZeroOnTop;
    }

    @Override
    public String toString() {
      //  if (grid != null)
         //   throw new RuntimeException("bah");
        StringBuffer gridStr = new StringBuffer();
        for (int rIdx = 0; rIdx < rows; ++rIdx) {
            int r = rIdx;
            if (!yZeroOnTop) {
                r = rows - rIdx - 1;
            }
            int index = getIndex(r, 0);
            gridStr.append(StringUtils.rightPad("" + index, 5));
            for (int c = 0; c < cols; ++c) {

                String s = "" + getEntry(r, c);

                s = StringUtils.rightPad(s, printWidth);
                gridStr.append(s);

            }
            gridStr.append("\n");
        }
        return "Grid [rows=" + rows + ", cols=" + cols + ", invalidSquare=" + invalidSquare + "]\n\n" + gridStr;

    }

    public String toStringCompact() {
        StringBuffer gridStr = new StringBuffer();
        for (int rIdx = 0; rIdx < rows; ++rIdx) {
            int r = rIdx;
            if (!yZeroOnTop) {
                r = rows - rIdx - 1;
            }

            for (int c = 0; c < cols; ++c) {

                gridStr.append(getEntry(r, c));
            }
            if (rIdx != rows - 1)
                gridStr.append("\n");
        }
        return gridStr.toString();

    }
    
    
}
