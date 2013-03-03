package edu.uiuc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TransList {
	int k; // k effective elements, that is the elements are not overlapping with each other.
	ArrayList<Data> l; // the data list.

	public TransList(){ // initiate the empty list.
		k = 0;
		l = new ArrayList<Data>(); 
	}
	
	public TransList(TransList t){
		// the hard copy constructor
		k = t.getEffectiveElemNum();
		l = new ArrayList<Data>();
		//l = new ArrayList<Data>(t.getList());
		ArrayList<Data> temp = t.getList();
		int size = temp.size();
		for(int i = 0; i<size; i++){
			l.add(temp.get(i));
		}
	}
	
	public void setEffectiveElemNum(int m){
		k = m;
	}
	
	public int getEffectiveElemNum(){
		return k;
	}
	
	public ArrayList<Data> getList(){
		return l;
	}
	
	public void addData(Data d){
		int pos = getInsertPosition(d);
		l.add(pos, d);
	}
	
	private int getInsertPosition(Data d){
		// calculate the insertion position of data d.
		// if d does not overlap with each of the effective elements, then d is the effective element.
		// otherwise, append d in the end of the list.
		if(isOverlapWithEffectiveElem(d)){
			return l.size();// if data d is overlapping with some data in the effective set, then add it to the end of the list.
		}
		
		k++; // increase the number of effective elements.

		// use binary search to find the right position
		int low = 0; 
		int high = k-1;
		int mid = (low+high)/2;
		
		Data dm;
		while(low < high){
			mid = (low+high)/2;
			
			 dm = l.get(mid); 
			if(Data.compareTwo(dm, d) < 0){
				low = mid +1;
				mid = (low+high)/2;
			} else if( Data.compareTwo(dm, d)> 0){
				high = mid;
				mid = (low+high)/2;
			} else 
				break;
		}
		
		return low;
	}
	
	private boolean isOverlapWithEffectiveElem(Data d){
		// use binary search
		int left = 0;
		int right= k;
		
		int low = 0; 
		int high = k;
		int mid = (low + high)/2;
		
		Data dm;
		while(low < high){
			mid = (low+high)/2;
			dm = l.get(mid);
			if(dm.getFeatures().get(0)+dm.getIntervals().get(0)/2 < d.getFeatures().get(0)-d.getIntervals().get(0)/2) {
				low = mid +1;
			}
			else if(dm.getFeatures().get(0)+dm.getIntervals().get(0)/2 > d.getFeatures().get(0)-d.getIntervals().get(0)/2) {
				high = mid;
			}
			else 
				break;
		}
		
		left = low;
		
		low = 0; 
		high = k;
		mid = (low + high)/2;
		

		while(low < high){
			mid = (low+high)/2;
			dm = l.get(mid);
			if(dm.getFeatures().get(0)-dm.getIntervals().get(0)/2 < d.getFeatures().get(0)+d.getIntervals().get(0)/2) {
				low = mid +1;
			}
			else if(dm.getFeatures().get(0)-dm.getIntervals().get(0)/2 > d.getFeatures().get(0)+d.getIntervals().get(0)/2) {
				high = mid;
			}
			else 
				break;
		}
		
		right = high;		
		
		for(int i = left; i<right; i++){
			if(Data.isOverlap(l.get(i), d))
				return true;
		}
		
		return false;
	}
	
	private void swap(int idx1, int idx2){
		assert(idx1 < l.size());
		assert(idx2 < l.size());
		
		Data tempData = l.get(idx1);
		l.set(idx1, l.get(idx2));
		l.set(idx2, tempData);
	}
	
	@Override
	public String toString(){
		String results = "";
		results += "Effective number = " + k;
		results += ", Total number = " + l.size();
		for(int i = 0; i< l.size(); i++) {
			results += "\n" + l.get(i);
		}
		
		return results;
	}
	
	public TransList genTransList(TransList remoteList){
		/* This function is used to generate the transmission list based 
		 * on the remote list and local list.
		 * 
		 * The returned remoteList is the list based on which to transmit data.*/
		int cnt = remoteList.getEffectiveElemNum();
		
		int i=  0;
		while(i<cnt){
			if(isOverlapWithEffectiveElem(remoteList.getList().get(i))){
				remoteList.swap(i, cnt-1);
				cnt --;
				remoteList.setEffectiveElemNum(cnt);
			}
			else 
				i++;
		}
		
		return remoteList;
	}
	
	public static TransList prioritization(TransList theList){
		TransList result = new TransList(theList);
		
		int numEff = theList.getEffectiveElemNum();
		int numNonEff = theList.getList().size() - numEff;
		
		ArrayList<Integer> top = genRandomPermutation(numEff);
		ArrayList<Integer> bot = genRandomPermutation(numNonEff);
		
		for(int i = 0; i<numEff; i++){
			int target = top.get(i);
			result.swap(i, target);
		}
		
		for(int i = 0; i<numNonEff; i++){
			int target = bot.get(i);
			result.swap(i+numEff, target+numEff);
		}
		
		return result;
	}
	
	public static void toFile(TransList theList, String theFile){
		File f = new File(theFile);
		
		BufferedWriter bw;
		
		try {
			bw = new BufferedWriter(new FileWriter(f));
			bw.write(theList.toString());
			bw.close();
			bw = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} 
	}
	
	public static TransList readFromFile(String theFile){
		TransList result = new TransList(); // initiate an empty list.
		
		File f = new File(theFile);
		BufferedReader br;
		
		try {
			br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			String[] halves = line.split(", ");
			String firsthalf = halves[0];
			int effectiveNum = Integer.parseInt(firsthalf.split("Effective number = ")[1]);
			//result.setEffectiveElemNum(effectiveNum);
			
			String secondhalf = halves[1];
			int totalNum = Integer.parseInt(secondhalf.split("Total number = ")[1]);
			for(int i = 0; i<totalNum; i++){
				Data temp = Data.readFromString(br.readLine());
				result.addData(temp);
			}
			
			br.close();
			assert(result.getEffectiveElemNum() == effectiveNum);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return result;
	}
	
	private static ArrayList<Integer> genRandomPermutation(int length){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i<length; i++){
			list.add(i);
		}
		
		Collections.shuffle(list);
		
		return list;
	}
}
