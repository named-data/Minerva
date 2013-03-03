package com.secon.submission;

import java.util.ArrayList;

public class MetaData implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	private ArrayList<OneMetaData> hiElements;
	private ArrayList<KAry> liElements;
	
	public MetaData(){
		hiElements = new ArrayList<OneMetaData> ();
		liElements = new ArrayList<KAry>();
	}
	
	public void addHi(OneMetaData one){
		hiElements.add(one);
	}
	
	public void addLi(KAry one){
		liElements.add(one);
	}
	
	public OneMetaData getHi(int idx){
		return hiElements.get(idx);
	}
	
	public KAry getLi(int idx){
		return liElements.get(idx);
	}
	
	public ArrayList<OneMetaData> getAllHi(){
		return hiElements;
	}
	
	public ArrayList<KAry> getAllLi(){
		return liElements;
	}
	
	public String toString(){
		String res= "";
		res += "Hi Elements:\n";
		for(int i = 0; i<hiElements.size(); i++){
			res += i+":\t"+ hiElements.get(i) + "\n";
		}
		
		res += "Li Elements:\n";
		for(int i = 0; i<liElements.size(); i++){
			res += "\t" + liElements.get(i) + "\n";
		}
		
		return res;
	}
}
