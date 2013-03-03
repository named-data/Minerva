package com.secon.submission;

import java.util.ArrayList;
import java.util.Random;

/**
 * In this class, we generate data. We assume that the data is
 * located in a k-d plane with its location uniformly randomly
 * generated. We unify the side length of each dimension to 1.
 *
 * This class merely generates data!
 * 
 * by shiguang*/

public class Data {
	private int dataDim; // the k in the k-d plane.
	private int dataSize; // the total number of data points.
	private int method; // 0:uniform, 1:gaussian
	
	private double mean; // used for gaussian generator
	private double std;
	
	private Random generator;
	
	public Data(int dim, int size){
		dataDim = dim;
		dataSize = size;
		this.method = 0; // the uniform method constructor
		
		generator = new Random();
	}
	
	public Data(int dim, int size, double mean, double std){
		dataDim = dim;
		dataSize = size;
		this.mean = mean;
		this.std = std;
		this.method = 1; // the gaussian method constructor
		
		generator = new Random();
	}
	
	public ArrayList<KAry> gen(){
		ArrayList<KAry> results = new ArrayList<KAry>();
		
		for(int i = 0; i<dataSize; i++){
			KAry item = new KAry(dataDim);
			for(int j = 0; j<dataDim; j++){
				item.set(j, getOneRandomNumber()); 
			}
			results.add(item);
		}
		
		return results;
	}
	
	private double getOneRandomNumber(){
		/* This function generate one random number in 0~1*/
		switch(method){
		case 0: // uniform
			return generator.nextDouble();
		case 1: // gaussian
			double res = generator.nextGaussian()*std + mean;
			while(res < 0 || res >1){
				res = generator.nextGaussian()*std+mean;
			}
			return res;
			default:
				return 0;
		}
	}

//	public static void main(String[] args) {
//		Data d = new Data(3, 10000, 0.5, 2);
//		ArrayList<KAry> l = d.gen();
//
//		for (int i = 0; i < l.size(); i++) {
//			System.out.println(i+": "+l.get(i));
//		}
//
//	}
}
