package com.secon.submission;

import java.util.ArrayList;

public class Distance {
	
	public static ArrayList<KAry> orderComp(ArrayList<KAry> localData, ArrayList<KAry> remoteData){
		ArrayList<KAry> res = new ArrayList<KAry>();
		
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Double> distances = new ArrayList<Double>();
		
		int dim = remoteData.get(0).getDim();

		for(int i = 0; i<remoteData.size(); i++){
			indices.add(i);
			
			KAry remoteCoordinates = remoteData.get(i);
			
			double dist = dim+1;
			
			// get the minimum distance
			for(int j = 0; j<localData.size(); j++){
				KAry localCoordinates = localData.get(j);
				double temp = 0;
				for(int k = 0; k<dim; k++){
					temp += Math.abs(remoteCoordinates.get(k) - localCoordinates.get(k));
				}
				
				if(temp < dist){
					dist = temp;
				}
			}
			
			distances.add(dist);
		}
		
		// sort in decrease order
		for(int i = 0; i<remoteData.size()-1; i++){
			for(int j = i+1; j<remoteData.size(); j++){
				// if dist equal then the same as fifo
				if(distances.get(j-1) < distances.get(j)){
					// swap
					double temp  = distances.get(j-1);
					distances.set(j-1, distances.get(j));
					distances.set(j, temp);
					
					int t = indices.get(j-1);
					indices.set(j-1, indices.get(j));
					indices.set(j, t);
				}
			}
		}
		
		for(int i = 0; i<remoteData.size(); i++){
			res.add(remoteData.get(indices.get(i)));
			KAry data1 = remoteData.get(indices.get(i));
			for(int j = i+1; j<remoteData.size(); j++){
				double tempdist = 0; 
				KAry data2 = remoteData.get(indices.get(j));
				for(int k = 0; k<dim; k++){
					tempdist += Math.abs(data1.get(k) - data2.get(k));
				}
				
				if(tempdist < distances.get(j)){
					distances.set(j, tempdist);
				}
			}
			
			for(int j = i+1; j<remoteData.size()-1; j++){
				for(int k = j+1; k<remoteData.size(); k++){
					if(distances.get(k-1) < distances.get(k)){
						// swap
						double temp = distances.get(k-1);
						distances.set(k-1, distances.get(k));
						distances.set(k, temp);
						
						int t = indices.get(k-1);
						indices.set(k-1, indices.get(k));
						indices.set(k, t);
					}
				}
			}
		}
		
		return res;
	}
	
	public static ArrayList<KAry> orderCompAppr(ArrayList<KAry> localData, ArrayList<KAry> remoteData, int pivotNum){
		ArrayList<KAry> res = new ArrayList<KAry>();
		
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Double> distances = new ArrayList<Double>();
		
		int dim = remoteData.get(0).getDim();
		
		ArrayList<KAry> tempList;
		
		if(localData.size() <= pivotNum){
			tempList = localData;
		} else{
			tempList = new ArrayList<KAry>();
			for(int ii = 0; ii<pivotNum; ii++){
				int idx = (int) Math.random()*localData.size();
				tempList.add(localData.get(idx));
			}
		}

		for(int i = 0; i<remoteData.size(); i++){
			indices.add(i);
			
			KAry remoteCoordinates = remoteData.get(i);
			
			double dist = dim+1;
			
			for (int j = 0; j < tempList.size(); j++) {
				KAry localCoordinates = tempList.get(j);
				double temp = 0;
				for (int k = 0; k < dim; k++) {
					temp += Math.abs(remoteCoordinates.get(k)
							- localCoordinates.get(k));
				}

				if (temp < dist) {
					dist = temp;
				}
			}

			distances.add(dist);
		}
		
		// sort in decrease order
		for(int i = 0; i<remoteData.size()-1; i++){
			for(int j = i+1; j<remoteData.size(); j++){
				// if dist equal then the same as fifo
				if(distances.get(j-1) < distances.get(j)){
					// swap
					double temp  = distances.get(j-1);
					distances.set(j-1, distances.get(j));
					distances.set(j, temp);
					
					int t = indices.get(j-1);
					indices.set(j-1, indices.get(j));
					indices.set(j, t);
				}
			}
		}
		
		for(int i = 0; i<remoteData.size(); i++){
			res.add(remoteData.get(indices.get(i)));
			KAry data1 = remoteData.get(indices.get(i));
			for(int j = i+1; j<remoteData.size(); j++){
				double tempdist = 0; 
				KAry data2 = remoteData.get(indices.get(j));
				for(int k = 0; k<dim; k++){
					tempdist += Math.abs(data1.get(k) - data2.get(k));
				}
				
				if(tempdist < distances.get(j)){
					distances.set(j, tempdist);
				}
			}
			
			for(int j = i+1; j<remoteData.size()-1; j++){
				for(int k = j+1; k<remoteData.size(); k++){
					if(distances.get(k-1) < distances.get(k)){
						// swap
						double temp = distances.get(k-1);
						distances.set(k-1, distances.get(k));
						distances.set(k, temp);
						
						int t = indices.get(k-1);
						indices.set(k-1, indices.get(k));
						indices.set(k, t);
					}
				}
			}
		}
		
		return res;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Data d = new Data(2, 1000);
		ArrayList<KAry> data = d.gen();
		
		ArrayList<KAry> data2 = d.gen();
		
		ArrayList<KAry> order = Distance.orderCompAppr(data, data2, 2000);
	}

}
