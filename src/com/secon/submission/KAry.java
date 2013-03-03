package com.secon.submission;

public class KAry implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private int dim;
	private double[] coordinates;
	
	public KAry(int d){
		dim = d;
		coordinates = new double[dim];
	}
	
	public double get(int i){
		assert(i>=0);
		assert(i<dim);
		return coordinates[i];
	}
	
	public void set(int i, double v){
		assert(i>= 0);
		assert(i<dim);
		coordinates[i] = v;
	}
	
	public String toString(){
		String res = ""+ coordinates[0];
		for(int i = 1; i<dim; i++){
			res += ", "+ coordinates[i];
		}
		
		return res;
	}
	
	public int getDim(){
		return dim;
	}
}
