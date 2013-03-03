package com.secon.submission;

import java.util.ArrayList;

public class Minerva {
	private MetaData localMeta;
	private MetaData remoteMeta;
	private ArrayList<KAry> localData;

	private double interval;
	private double hiRatio;
	
	// private ArrayList<Integer> RLOverlapSet; // the set of data in local
	// which overlaps with some data in remote
	// private ArrayList<Integer> RROverlapSet; // the set of data in remote
	// which is already added in local that overlaps with some data in remote
	//
	// private ArrayList<KAry> LRLiElements; // the remote data that is li
	// element with respect to local data.

	public Minerva(MetaData l, MetaData r, ArrayList<KAry> d, double inter,
			double hiR) {
		localMeta = l;
		remoteMeta = r;
		localData = d;
		interval = inter;
		hiRatio = hiR;
	}

	public ArrayList<KAry> orderComp() {
		ArrayList<KAry> res = new ArrayList<KAry>();

		ArrayList<ArrayList<Integer>> RLOverlapSet = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> RROverlapSet = new ArrayList<ArrayList<Integer>>();

		// 1. compute the overlap set in local in respect to remote data
		ArrayList<OneMetaData> remoteHiData = remoteMeta.getAllHi();
		ArrayList<OneMetaData> localHiData = localMeta.getAllHi();
		
		ArrayList<KAry> LRLiElements = new ArrayList<KAry>();

		for (int i = 0; i < remoteHiData.size(); i++) {
			KAry remoteOneData = remoteHiData.get(i).getCoordinates();

			ArrayList<Integer> oneRLOverlapSet = new ArrayList<Integer>();
			ArrayList<Integer> oneRROverlapSet = new ArrayList<Integer>();

			for (int j = 0; j < localHiData.size(); j++) {
				KAry localOneData = localHiData.get(j).getCoordinates();

				int dim = remoteOneData.getDim();

				boolean isOverlap = true;
				boolean isNegalectable = true;
				for (int k = 0; k < dim; k++) {
					if (Math.abs(remoteOneData.get(k) - localOneData.get(k)) >= interval) {
						isOverlap = false;
						isNegalectable = false;
						break;
					}
					if(Math.abs(remoteOneData.get(k) - localOneData.get(k)) >= interval*hiRatio){
						isNegalectable = false;
					}
				}
				
				if(isNegalectable){
					LRLiElements.add(remoteOneData);
					oneRLOverlapSet = null;
					oneRROverlapSet = null;
					break;
				}

				if (isOverlap) {
					oneRLOverlapSet.add(j);
				}
			}

			RLOverlapSet.add(oneRLOverlapSet);
			RROverlapSet.add(oneRROverlapSet);
		}

		// 2. for each data compute the marginal coverage and put it into a max
		// heap
		MaxIntHeap h = new MaxIntHeap(remoteHiData.size() + 10);
		for (int i = 0; i < remoteHiData.size(); i++) {
			if (RLOverlapSet.get(i) != null) {
				h.addItem(marginalCoverage(i, RLOverlapSet.get(i),
						RROverlapSet.get(i), localHiData, remoteHiData));
			} else {
				h.addItem(-1);
			}
		}

		// 3. Pop heap and send interest to remote site, and update the overlap
		// set in the heap
		for (int i = 0; i < remoteHiData.size(); i++) {
			if(h.peepMaxValue() == -1){
				break;
			}
			int id = h.popMax();
			res.add(remoteHiData.get(id).getCoordinates());
			// we send interest to the remote site

			ArrayList<Integer> oLSet = remoteHiData.get(id).getOLSet();
			for (int j = 0; j < oLSet.size(); j++) {
				int id2 = oLSet.get(j);
				ArrayList<Integer> oneRROverlapSet = RROverlapSet.get(id2);
				if(oneRROverlapSet == null){
					continue;
				}
				oneRROverlapSet.add(id);
				remoteHiData.get(id2).getOLSet().remove(new Integer(id));
				double tempMC = marginalCoverage(id2, RLOverlapSet.get(id2), RROverlapSet.get(id2), localHiData, remoteHiData);
				h.updateValueOnOriginalIndex(id2,tempMC);
			}
		}
		
		// 4. add the li elements
		res.addAll(remoteMeta.getAllLi());
		res.addAll(LRLiElements);
		
//		System.out.println("The size of returned order is "+res.size());
		return res;
	}

	private double marginalCoverage(int idx, ArrayList<Integer> oLSetRofL,
			ArrayList<Integer> oLSetRofR, ArrayList<OneMetaData> localHiData,
			ArrayList<OneMetaData> remoteHiData) {

		double totalCoverage = 1;
		int dim = localHiData.get(0).getCoordinates().getDim();
		for (int i = 0; i < dim; i++) {
			totalCoverage *= interval;
		}

		double overlapCoverage = swipeMarginalCoverage(idx, 0, oLSetRofL,
				oLSetRofR, oLSetRofL, oLSetRofR, localHiData, remoteHiData);

		return totalCoverage - overlapCoverage;
	}

	private double swipeMarginalCoverage(int idx, int dimIdx,
			ArrayList<Integer> leftSet, ArrayList<Integer> rightSet,
			ArrayList<Integer> oLSetRofL, ArrayList<Integer> oLSetRofR,
			ArrayList<OneMetaData> localHiData,
			ArrayList<OneMetaData> remoteHiData) {
		double oc = 0;

		MaxIntHeap h = new MaxIntHeap(
				2 * (leftSet.size() + rightSet.size()) + 10);
		for (int i = 0; i < leftSet.size(); i++) {
			double dimCoordinate = localHiData.get(leftSet.get(i))
					.getCoordinates().get(dimIdx);
			h.addItem(dimCoordinate - interval / 2);
			h.addItem(dimCoordinate + interval / 2);
		}
		for (int j = 0; j < rightSet.size(); j++) {
			double dimCoordinate = remoteHiData.get(rightSet.get(j))
					.getCoordinates().get(dimIdx);
			h.addItem(dimCoordinate - interval / 2);
			h.addItem(dimCoordinate + interval / 2);
		}
		double dimCoordinate = remoteHiData.get(idx).getCoordinates()
				.get(dimIdx);
		h.addItem(dimCoordinate - interval / 2);
		h.addItem(dimCoordinate + interval / 2);

		double[] endpoints = MaxIntHeap.heapSort(h);
		h = null;

		double remoteDimCoordinate = dimCoordinate; // an alias
		int id = 0;
		while (endpoints[id] < remoteDimCoordinate - interval / 2) {
			id++;
		}

		double pre = endpoints[id++];
		double x;
		while (endpoints[id] <= remoteDimCoordinate + interval / 2) {
			x = endpoints[id++];
			if (pre == x) {
				if (id >= endpoints.length) {
					break;
				} else
					continue;
			}

			ArrayList<Integer> lset = new ArrayList<Integer>();
			for (int i = 0; i < leftSet.size(); i++) {
				double tempLeftDimCoordinate = localHiData.get(leftSet.get(i))
						.getCoordinates().get(dimIdx);
				if (tempLeftDimCoordinate - interval / 2 <= pre
						&& tempLeftDimCoordinate + interval / 2 >= x) {
					lset.add(leftSet.get(i));
				}
			}

			ArrayList<Integer> rset = new ArrayList<Integer>();
			for (int i = 0; i < rightSet.size(); i++) {
				double tempRightDimCoordinate = remoteHiData
						.get(rightSet.get(i)).getCoordinates().get(dimIdx);
				if (tempRightDimCoordinate - interval / 2 <= pre
						&& tempRightDimCoordinate + interval / 2 >= x) {
					rset.add(rightSet.get(i));
				}
			}

			if (lset.size() + rset.size() > 0) {
				if (dimIdx == remoteHiData.get(idx).getCoordinates().getDim() - 1) {
					oc += x - pre;
				} else {
					oc += (x - pre)
							* swipeMarginalCoverage(idx, dimIdx + 1, lset,
									rset, oLSetRofL, oLSetRofR, localHiData,
									remoteHiData);
				}
			}

			pre = x;

			if (id >= endpoints.length)
				break;
		}

		endpoints = null;

		return oc;
	}

	public static double coverageComp(ArrayList<KAry> data, double interval){
		double coverage = 0;
		
		ArrayList<Integer> set = new ArrayList<Integer>();
		for(int i = 0; i<data.size(); i++){
			set.add(i);
		}
		
		coverage = swipeCoverageStep(0, data, set, interval);
		
		return coverage;
	}
	
	private static double swipeCoverageStep(int dimIdx, ArrayList<KAry> data, ArrayList<Integer> set, double interval){
		double coverage = 0;
		
		MaxIntHeap h = new MaxIntHeap(2*set.size() +10);
		for(int i = 0; i<set.size(); i++){
			double coordinate = data.get(set.get(i)).get(dimIdx);
			h.addItem(coordinate - interval/2);
			h.addItem(coordinate + interval/2);
		}
		double[] endpoints = MaxIntHeap.heapSort(h);
		h = null;
		
		int id = 0;
		double pre = endpoints[id++];
		while(id < endpoints.length){
			double x = endpoints[id++];
			if(pre == x)
				continue;
			
			ArrayList<Integer> s = new ArrayList<Integer>();
			for(int i = 0; i<set.size(); i++){
				double coord = data.get(set.get(i)).get(dimIdx);
				if(coord - interval/2 <= pre && coord + interval/2 >= x){
					s.add(set.get(i));
				}
			}
			
			if(s.size() > 0){
				if ( dimIdx == data.get(0).getDim()-1){
					coverage += x- pre;
				} else {
					coverage += (x-pre)* swipeCoverageStep(dimIdx+1, data, s, interval);
				}
			}
			
			pre = x;
		}
		
		return coverage;
	}
	
	public static void main(String[] args){
		double interval = 0.01;
		double hiRatio = 0.5;
		
		long start_time = System.currentTimeMillis();
		Data d = new Data(2, 2000);

		ArrayList<KAry> data = d.gen();
		
		MetaGen gen = new MetaGen(data, interval, hiRatio);
		MetaData remotemeta = gen.generate();
		
		long time_2 = System.currentTimeMillis();
		
		ArrayList<KAry> data2 = d.gen();
		MetaGen gen2 = new MetaGen(data2, interval, hiRatio);
		MetaData localmeta = gen2.generate();
		
		long time_3 = System.currentTimeMillis();
		
		Minerva m = new Minerva(localmeta, remotemeta, data2, interval, hiRatio);
		ArrayList<KAry> order = m.orderComp();
		
		long end_time = System.currentTimeMillis();
		
		System.out.println(end_time - time_3);
	}
}
