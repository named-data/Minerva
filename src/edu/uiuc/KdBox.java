package edu.uiuc;

public class KdBox {
	int _k;
	double[] features;
	
	public KdBox(int k){
		_k = k;
		features = new double[_k];
	}
	
	public void set(int idx, double value){
		assert(idx < _k);
		features[idx] = value;
	}
	
	public double get(int idx){
		assert(idx < _k);
		return features[idx];
	}
	
	public int getNumFeatures(){
		return _k;
	}
	
	
	@Override
	public String toString(){
		String results = "";
		
//		results += "<";
		if(_k > 0){
			results += features[0];
		}
		for(int i = 1; i< _k; i++){
			results += ", " + features[i];
		}
//		results += ">";
		
		return results;
	}
	
	public static KdBox readFromString(String s){
//		String center = s.split("<")[1].split(">")[0];
		String[] features = s.split(", ");
		
		KdBox result = new KdBox(features.length);
		for(int i = 0; i< features.length; i++){
			result.set(i, Double.parseDouble(features[i]));
		}
		
		return result;
	}
	
	
//	public static void main( String[] args){
//		KdBox kb = new KdBox(3);
//		for(int i = 0; i<kb.getNumFeatures(); i++){
//			kb.set(i, i*15);
//		}
//		
//		System.out.println(kb);
//		
//		String s = kb.toString();
//		
//		KdBox kbShadow = KdBox.readFromString(s);
//		System.out.println(kbShadow);
//	}
}
