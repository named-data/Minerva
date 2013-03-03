package edu.uiuc;

public class Data implements Comparable<Data>{
	String _name;
	int _k;
	KdBox _features;
	KdBox _intervals;
	
	public Data(String name, KdBox features, KdBox intervals){
		assert(features.getNumFeatures() == intervals.getNumFeatures());
		
		_name = name;
		_features = features;
		_k = features.getNumFeatures();
		_intervals = intervals;
	}
	
	public String getName(){
		return _name;
	}
	
	public int getNumFeatures(){
		return _k;
	}
	
	public KdBox getFeatures(){
		return _features;
	}
	
	public KdBox getIntervals(){
		return _intervals;
	}
	
	public static boolean isOverlap(Data d1, Data d2){
		
		assert(d1.getNumFeatures() == d2.getNumFeatures());
		
		int k = d1.getNumFeatures();

		KdBox features1 = d1.getFeatures();
		KdBox intervals1 = d1.getIntervals();
		KdBox features2 = d2.getFeatures();
		KdBox intervals2 = d2.getIntervals();
		
		for(int i = 0; i<k; i++){
			if(features1.get(i) <= features2.get(i) - intervals2.get(i)/2 - intervals1.get(i)/2)
				return false;
			else if (features1.get(i) >= features2.get(i) + intervals2.get(i)/2 + intervals1.get(i)/2)
				return false;
		}
		return true;
	}
	
	public static boolean isClose(Data d1, Data d2, double beta){
		assert(d1.getNumFeatures() == d2.getNumFeatures());
		assert(beta >= 1);
		
		int k = d1.getNumFeatures();
		
		KdBox features1 = d1.getFeatures();
		KdBox intervals1 = d1.getIntervals();
		KdBox features2 = d2.getFeatures();
		KdBox intervals2 = d2.getIntervals();
		
		for(int i = 0; i<k; i++){
			if(Math.abs(features1.get(i)-features2.get(i)) >= (intervals1.get(i) + intervals2.get(i))/2/beta)
				return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		String results = "";
//		results += "Data name = "+ _name + "; features = " + _features + "; intervals = "+ _intervals;
		results += _name + "; "+_features + "; "+ _intervals;
		return results;
	}
	
	public static Data readFromString(String s){
		String[] segments = s.split("; ");
		
		String dataName = segments[0];//.split("Data name = ")[1];
		KdBox features = KdBox.readFromString(segments[1]);
		KdBox intervals = KdBox.readFromString(segments[2]);
		
		return new Data(dataName, features, intervals);
	}

	@Override
	public int compareTo(Data d) {
		// compare with data d in the lexicalgraphic order of features.
		KdBox localFeatures = this.getFeatures();
		KdBox remoteFeatures = d.getFeatures();
		for(int i = 0; i<_k; i++){
			if(localFeatures.get(i) < remoteFeatures.get(i))
				return -1;
			if(localFeatures.get(i) > remoteFeatures.get(i))
				return 1;
		}
		return 0;
	}
	
	public static int compareTwo(Data d1, Data d2){
		return d1.compareTo(d2);
	}
	
//	public static void main(String[] args){
//		KdBox f1 = new KdBox(2);
//		f1.set(0, 1);
//		f1.set(1, 3);
//		KdBox i1 = new KdBox(2);
//		i1.set(0, 2);
//		i1.set(1, 2);
//		
////		KdBox f2 = new KdBox(1);
////		f2.set(0,  2);
////		KdBox i2 = new KdBox(1);
////		i2.set(0, 2);
//		
//		Data d1 = new Data("d1", f1, i1);
////		Data d2 = new Data("d2", f2, i2);
//		
//		System.out.println(d1);
//		//System.out.println(d2);
//		
//		//System.out.println(Data.isClose(d1, d2, 1.5));
//		
//		Data d3 = Data.readFromString(d1.toString());
//		System.out.println(d3);
//		
//	}
}
