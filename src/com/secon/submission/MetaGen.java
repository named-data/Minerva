package com.secon.submission;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MetaGen {
	private ArrayList<KAry> data;
	private double interval; // the coverage interval for each dim
	private double hiRatio; // the neglectable distance compared with interval.
	
	public MetaGen(ArrayList<KAry> d, double interval, double hiRatio){
		data = d;
		this.interval = interval;
		this.hiRatio = hiRatio;
	}
	
	public MetaData generate(){
		MetaData metadata = new MetaData();
		
		for(int i = 0; i<data.size(); i++){
			KAry theData = data.get(i); // we get the coordinates of data i
			
			int hiCnt = metadata.getAllHi().size(); // how many of hi elements
			
			ArrayList<Integer> overlapsetOfNewData = new ArrayList<Integer>(); // the overlap set of new element
			
			boolean isNegalectable = false;
			
			for(int j = 0; j< hiCnt; j++){

				isNegalectable = true;
				boolean isOverlap = true;

				KAry existingData = metadata.getHi(j).getCoordinates();
				int datadim = existingData.getDim();

				for(int k = 0; k<datadim; k++){
					// if the distance between the two coordinates are smaller than the hiRatio*interval on all dimensions, then the new element is an li element.
					if(Math.abs(theData.get(k) - existingData.get(k)) >= interval) {
						isOverlap = false;
						isNegalectable = false;
						break;
					}
					 
					if(Math.abs(theData.get(k) - existingData.get(k))*10000 > interval*10000*hiRatio){
						isNegalectable = false;
					}
				}
				
				if(isNegalectable){
					metadata.addLi(theData);
					break;
				}
				
				if(isOverlap){
					overlapsetOfNewData.add(j);
				}
			}
			
			if(!isNegalectable){
				OneMetaData theMetaData = new OneMetaData(theData, overlapsetOfNewData);
				metadata.addHi(theMetaData);
				int newestIdx = metadata.getAllHi().size()-1;
				
				for(int ii = 0; ii<overlapsetOfNewData.size(); ii++){
					metadata.getHi(overlapsetOfNewData.get(ii)).getOLSet().add(newestIdx);
				}
			}
		}
		
		return metadata;
	}
	
	public static void main(String[] args){
		Data d = new Data(2, 1000);
		ArrayList<KAry> data = d.gen();    
		
//		ArrayList<KAry> data = new ArrayList<KAry>();
//		
//		try {
//			FileInputStream dfis = new FileInputStream("/Users/shiguang/Desktop/data.dat");
//			ObjectInputStream dois = new ObjectInputStream(dfis);
//			data = (ArrayList<KAry>) dois.readObject();
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		MetaGen gen = new MetaGen(data, 0.1, 0.2);
		
		MetaData meta = gen.generate();
		
		try {
			FileOutputStream fos = new FileOutputStream("/Users/shiguang/Desktop/output.dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(meta);
			
			FileInputStream fis = new FileInputStream("/Users/shiguang/Desktop/output.dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			MetaData m2 = (MetaData) ois.readObject();
			
			FileWriter fw = new FileWriter("/Users/shiguang/Desktop/output.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(m2+"");
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
