package com.secon.submission;

import java.util.ArrayList;

public class OneMetaData implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	private KAry coordinates;
	private ArrayList<Integer> overlapset;
	
	public OneMetaData(KAry c){
		coordinates = c;
		overlapset = new ArrayList<Integer>();
	}
	
	public OneMetaData(KAry c, ArrayList<Integer> oSet){
		coordinates = c;
		overlapset = oSet;
	}
	
	public void appendOverlapElem(int idx){
		overlapset.add(idx);
	}
	
	public KAry getCoordinates(){
		return coordinates;
	}
	
	public ArrayList<Integer> getOLSet(){
		return overlapset;
	}
	
	public void print(){
		System.out.print("<" + coordinates+">: ");
		for(int i = 0; i<overlapset.size(); i++){
			System.out.print(overlapset.get(i)+", ");
		}
	}
	
	public String toString(){
		String res = "";
		res += "<" + coordinates+">: ";
		for(int i = 0; i<overlapset.size(); i++){
			res += overlapset.get(i) +", ";
		}
		
		return res;
	}
}
