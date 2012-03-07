package com.telmomenezes.synthetic;


/**
 * General purpose matrix.
 * 
 * Includes a number of common linear algebra operations. 
 * 
 * @author Telmo Menezes (telmo@telmomenezes.com)
 */
public class Matrix {
	
	protected double[][] data;
	protected int rows;
	protected int columns;
	
	
	public Matrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		data = new double[rows][columns];
	}


    public Matrix(Matrix matrix) {	
    	rows = matrix.getRows();
        columns = matrix.getColumns();
        data = new double[rows][columns];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                    data[i][j] = matrix.data[i][j];
    }
    
    
    public void copy(Matrix matrix) {
    	data = new double[rows][columns];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                    data[i][j] = matrix.data[i][j];
    }
    
    
    public void zero() {
    	for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                data[i][j] = 0;
    }

    
    public void randomize() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                data[i][j] = RandomGenerator.instance().random.nextDouble();
    }
    
    
    public void setDiagonal(double value) {
    	for (int i = 0; i < columns; i++)
    		data[i][i] = value;
    }
    
    
    public void fill(double value) {
    	for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                data[i][j] = value;
    }
    
    
    public void zeroOrOne() {
    	for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                if (data[i][j] > 0)
                	data[i][j] = 1;
                else
                	data[i][j] = 0;
    }
    
    
    public Matrix transpose(Matrix target) {
    	
    	Matrix t;
    	if (target == null) {
    		t = new Matrix(columns, rows);
    	}
    	else {
    		t = target;
    	}
    	
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
                t.data[c][r] = this.data[r][c];
        
        return t;
    }
    
    
    public Matrix add(Matrix b, Matrix target) {	
    	Matrix t;
    	if (target == null) {
    		t = new Matrix(rows, columns);
    	}
    	else {
    		t = target;
    	}
    	
    	for (int i = 0; i < t.rows; i++)
            for (int j = 0; j < t.columns; j++)
                t.data[i][j] = data[i][j] + b.data[i][j];
    	
    	return t;
    }
    
    
    public void mul(double value) {
    	for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                data[i][j] *= value;
    }
    
    
    public Matrix mul(Matrix b, Matrix target) {	
    	Matrix t;
    	if (target == null) {
    		t = new Matrix(rows, b.columns);
    	}
    	else {
    		t = target;
    		t.zero();
    	}
    	
    	for (int k = 0; k < columns; k++)
    		for (int i = 0; i < t.rows; i++)
    			if (data[i][k] > 0)
    				for (int j = 0; j < t.columns; j++)
    					t.data[i][j] += data[i][k] * b.data[k][j];
    	
    	return t;
    }
    
    
    public Matrix pow(int degree, Matrix target) {
    	Matrix t;
    	if (target == null) {
    		t = new Matrix(rows, columns);
    	}
    	else {
    		t = target;
    		t.zero();
    	}
    	
    	// start with identity
    	t.setDiagonal(1);
    	
    	Matrix temp = new Matrix(rows, columns);
    	
    	for (int i = 0; i < degree; i++) {
    		t.mul(this, temp);
    		t.copy(temp);
    	}
    	
    	return t;
    }
    
    
    public Matrix exp(Matrix target, int terms) {
    	Matrix t;
    	if (target == null) {
    		t = new Matrix(rows, columns);
    	}
    	else {
    		t = target;
    		t.zero();
    	}
    	
    	// initialize target matrix to identity
    	t.setDiagonal(1);
    	
    	Matrix temp = new Matrix(rows, columns);
    	Matrix temp2 = new Matrix(rows, columns);
    	
    	// compute the terms
    	for (int i = 0; i < terms; i++) {
    		temp.copy(this);
    		for (int j = 0; j < i; j++) {
    			temp.mul(this, temp2);
    			temp.copy(temp2);
    		}
    	
    		long fact = 1;
    		for (int j = 1; j <= i; j++) {
    			fact *= j;
    		}
    	
    		temp.mul(1 / ((double)fact));
    		t.add(temp, t);
    	}
    	
    	return t;
    }
    
    
    public double trace() {
    	double t = 0.0;
    	for (int i = 0; i < columns; i++)
    		t += data[i][i];
    	return t;
    }
    
    
    public boolean positive() {
    	for (int i = 0; i < rows; i++)
    		for (int j = 0; j < columns; j++)
    			if (data[i][j] < 0)
    				return false;
    		
    	return true;
    }

    
    public String toString() {
    	String str = "";
    	
    	for (int i = 0; i < rows; i++) {
    		for (int j = 0; j < columns; j++)
    			str += data[i][j] + "\t";
    		str += "\n";
    	}
    	
    	return str;
    }
    

	public double[][] getData() {
		return data;
	}


	public int getRows() {
		return rows;
	}


	public int getColumns() {
		return columns;
	}
	
	
	public static void main(String[] args) {
		
		Matrix m = new Matrix(3, 3);
		//m.randomize();
		m.setDiagonal(1.0);
		m.mul(-3);
		System.out.println(m);
		System.out.println(m.trace());
	}
}