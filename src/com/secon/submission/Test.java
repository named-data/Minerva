package com.secon.submission;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Test {

	private static void sendFile(String file) throws IOException{
		Socket clientSocket = new Socket("128.174.241.64", 34343);
		 
		InputStream is = clientSocket.getInputStream();
		OutputStream os = clientSocket.getOutputStream();
		
		DataOutputStream dos = new DataOutputStream(os);
		
		File theFile = new File(file);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(theFile));
		
		long numBytes = theFile.length();
		dos.writeLong(numBytes);
		dos.flush();
		
		byte[] buffer = new byte[1024];
		int readBytes;
		while((readBytes = bis.read(buffer)) != -1){
			os.write(buffer, 0, readBytes);
			os.flush();
		}
		bis.close();
		
		DataInputStream dis = new DataInputStream(is);
		int number = dis.readInt();
		
//		System.out.println(number);
		
		clientSocket.close();
		clientSocket = null;
	}
	
	public static long overhead(int dimNum, int dataCnt, double mean,double std, double interval, double hiRatio, String metaFileName)
			throws IOException {

		long res = 0;
		int cnt = 10;

		for (int i = 0; i < cnt; i++) {

			Data d;
			if (mean < 0) {
				d = new Data(dimNum, dataCnt);
			} else {
				d = new Data(dimNum, dataCnt, mean, std);
			}

			ArrayList<KAry> localData = d.gen();
			ArrayList<KAry> remoteData = d.gen();

			MetaGen localGen = new MetaGen(localData, interval, hiRatio);
			MetaData localMeta = localGen.generate();

			MetaGen remoteGen = new MetaGen(remoteData, interval, hiRatio);
			MetaData remoteMeta = remoteGen.generate();

			FileOutputStream fos = new FileOutputStream(metaFileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(remoteMeta);
			oos.flush();

			oos.close();
			fos.close();
			oos = null;
			fos = null;

			long start_time = System.currentTimeMillis();
			Test.sendFile(metaFileName);
			Minerva m = new Minerva(localMeta, remoteMeta, localData, interval,
					hiRatio);
			ArrayList<KAry> order = m.orderComp();
			long end_time = System.currentTimeMillis();

			res += (end_time - start_time);
		}

		return res / cnt;

	}
	
	private static class Stat{
		public double mean;
		public double std;
		public Stat(double m, double s){
			mean = m;
			std = s;
		}
	}
	
	public static Stat overhead_server(int dimNum, int dataCnt1, int dataCnt2, double mean,double std, double interval, double hiRatio, String metaFileName)
			throws IOException {

		Stat res;
		int cnt = 1;
		ArrayList<Long> overheads = new ArrayList<Long>();

		for (int i = 0; i < cnt; i++) {
			System.out.println(i);
			
			long overhead = 0;

			Data d1, d2;
			if (mean < 0) {
				d1 = new Data(dimNum, dataCnt1);
				d2 = new Data(dimNum, dataCnt2);
			} else {
				d1 = new Data(dimNum, dataCnt1, mean, std);
				d2 = new Data(dimNum, dataCnt2, mean, std);
			}

			System.out.println("newed the data generators");
			
			ArrayList<KAry> localData = d1.gen();
			ArrayList<KAry> remoteData = d2.gen();
			
			System.out.println("Generated the data.");

			MetaGen localGen = new MetaGen(localData, interval, hiRatio);
			MetaData localMeta = localGen.generate();
			
			System.out.println("Generated the local metadata");

			MetaGen remoteGen = new MetaGen(remoteData, interval, hiRatio);
			MetaData remoteMeta = remoteGen.generate();
			
			System.out.println("Generated the remote metadata");

//			FileOutputStream fos = new FileOutputStream(metaFileName);
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			oos.writeObject(remoteMeta);
//			oos.flush();
//
//			oos.close();
//			fos.close();
//			oos = null;
//			fos = null;

			long start_time = System.currentTimeMillis();
			//Test.sendFile(metaFileName);
			Minerva m = new Minerva(localMeta, remoteMeta, localData, interval,
					hiRatio);
			ArrayList<KAry> order = m.orderComp();
			long end_time = System.currentTimeMillis();

			overhead = (end_time - start_time);
			overheads.add(overhead);
		}
		
		double sum = 0;
		for(int i = 0; i<overheads.size(); i++){
			sum += overheads.get(i);
		}
		
		double mea = sum/overheads.size();
		
		sum = 0;
		for(int i = 0; i<overheads.size(); i++){
			sum += (overheads.get(i) - mea)*(overheads.get(i) - mea);
		}
		
		double st = sum / overheads.size();
		st = Math.sqrt(st);
		
		res = new Stat(mea, st);
		return res;

	}

	public static void exec(int dimNum, int dataCnt, double mean, double std,
			double interval, double hiRatio, String metaFileName,
			String dataFileName, String resultOrderFileName,
			String resultTimeFileName,
			String resultFifoOrderFileName,
			String resultFifoTimeFileName) throws IOException {
		// 1. Generate data
		Data d;
		if (mean < 0) {
			d = new Data(dimNum, dataCnt);
		} else {
			d = new Data(dimNum, dataCnt, mean, std);
		}

		ArrayList<KAry> localData = d.gen();
		ArrayList<KAry> remoteData = d.gen();

		MetaGen localGen = new MetaGen(localData, interval, hiRatio);
		MetaData localMeta = localGen.generate();

		MetaGen remoteGen = new MetaGen(remoteData, interval, hiRatio);
		MetaData remoteMeta = remoteGen.generate();

		FileOutputStream fos = new FileOutputStream(metaFileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(remoteMeta);
		oos.flush();

		oos.close();
		fos.close();
		oos = null;
		fos = null;

		long start_time = System.currentTimeMillis();
		Test.sendFile(metaFileName);
	Minerva m = new Minerva(localMeta, remoteMeta, localData, interval, hiRatio);
		ArrayList<KAry> order = m.orderComp();
		long end_time = System.currentTimeMillis();
		
		System.out.println("Overhead is "+(end_time-start_time));
		
		// 4. Request data
		ArrayList<Long> time = new ArrayList<Long>();
		time.add(end_time - start_time); // we first add the overhead
		
		start_time = System.currentTimeMillis();
		for(int i = 0; i<order.size(); i++){
			Test.sendFile(dataFileName);
			long cur_time = System.currentTimeMillis();
			time.add(cur_time - start_time);
		}
		
		// 5. save the result to files
		fos = new FileOutputStream(resultOrderFileName);
		oos = new ObjectOutputStream(fos);
		oos.writeObject(order);
		oos.flush();
		
		oos.close();
		fos.close();
		
		fos = new FileOutputStream(resultTimeFileName);
		oos = new ObjectOutputStream(fos);
		oos.writeObject(time);
		oos.flush();
		
		oos.close();
		fos.close();
		oos = null;
		fos = null;
		
		// 6. use FIFO to transmit
		ArrayList<Long> FIFO_time = new ArrayList<Long>();
		start_time = System.currentTimeMillis();
		for(int i = 0; i<remoteData.size(); i++){
			Test.sendFile(dataFileName);
			long cur_time = System.currentTimeMillis();
			FIFO_time.add(cur_time - start_time);
		}
		
		fos = new FileOutputStream(resultFifoOrderFileName);
		oos = new ObjectOutputStream(fos);
		oos.writeObject(remoteData);
		oos.flush();
		
		oos.close(); fos.close();
		
		fos = new FileOutputStream(resultFifoTimeFileName);
		oos = new ObjectOutputStream(fos);
		oos.writeObject(FIFO_time);
		oos.flush();
		
		oos.close(); fos.close();
		oos = null; fos = null;
	}
	
	public static void exec_sim(int dimNum, int dataCnt, double mean, double std,
			double interval, double hiRatio, long time_mean, double time_range_percentage,
			String metaFileName,
			String resultOrderFileName,
			String resultTimeFileName,
			String resultDistanceOrderFileName,
			String resultDistanceTimeFileName,
			String resultFifoOrderFileName,
			String resultFifoTimeFileName) throws IOException {

		final long overhead_low = 344;
		final long overhead_high = 485;
		
		final int gap = 10;
		
		String postfix = "_"+dimNum+"_"+dataCnt+"_"+mean+"_"+std+"_"+interval+"_"+hiRatio+"_"+time_mean+"_"+time_range_percentage;
		
		System.out.println(postfix);
		
		// 1. Generate data
		Data d;
		if (mean < 0) {
			d = new Data(dimNum, dataCnt);
		} else {
			d = new Data(dimNum, dataCnt, mean, std);
		}

		ArrayList<KAry> localData = d.gen();
		ArrayList<KAry> remoteData = d.gen();

		MetaGen localGen = new MetaGen(localData, interval, hiRatio);
		MetaData localMeta = localGen.generate();

		MetaGen remoteGen = new MetaGen(remoteData, interval, hiRatio);
		MetaData remoteMeta = remoteGen.generate();

//		FileOutputStream fos = new FileOutputStream(metaFileName);
//		ObjectOutputStream oos = new ObjectOutputStream(fos);
//		oos.writeObject(remoteMeta);
//		oos.flush();
//
//		oos.close();
//		fos.close();
//		oos = null;
//		fos = null;

		long start_time = System.currentTimeMillis();
		Minerva m = new Minerva(localMeta, remoteMeta, localData, interval, hiRatio);
		ArrayList<KAry> order = m.orderComp();
		long end_time = System.currentTimeMillis();

		long overhead = end_time - start_time;
		System.out.println("Overhead is "+ overhead);
		
		// 4. Request data
		ArrayList<Long> time = new ArrayList<Long>();
		
		long cur_time = overhead;
		for(int i = 0; i<order.size(); i++){
			cur_time += Math.random()*time_mean*2*time_range_percentage + time_mean*(1-time_range_percentage);
			time.add(cur_time);
		}
		
		System.out.println("Finished the greedy algorithm.");
		
		double localCoverageValue = Minerva.coverageComp(localData, interval);
		
		ArrayList<Double> coverageList = new ArrayList<Double>();
		ArrayList<Long> coverageTimeList = new ArrayList<Long>();
		ArrayList<KAry> tempList = new ArrayList<KAry>();
		for(int i = 0; i<localData.size(); i++){
			tempList.add(localData.get(i));
		}
		for(int i = 0; i<order.size(); i++){
			tempList.add(order.get(i));
			if((i+1)%gap == 0){
				coverageList.add(Minerva.coverageComp(tempList, interval) - localCoverageValue);
				coverageTimeList.add(time.get(i));
			}
		}
		
		if(coverageTimeList.size()*gap < order.size()){
			coverageList.add(Minerva.coverageComp(order, interval) - localCoverageValue);
			coverageTimeList.add(time.get(time.size()-1));
		}
		
		FileOutputStream fos = new FileOutputStream(resultOrderFileName+postfix);
		OutputStreamWriter ow = new OutputStreamWriter(fos);
		for(int i = 0; i<coverageList.size(); i++){
			String s = coverageTimeList.get(i) + ","+coverageList.get(i)+"\n";
			ow.write(s);
			ow.flush();
		}
		ow.close();
		fos.close();
		
		System.out.println("Finished the greedy coverage computation.");
		
//		ArrayList<KAry> afterMeta = new ArrayList<KAry>();
//		for(int i = 0; i<remoteMeta.getAllHi().size(); i++){
//			afterMeta.add(remoteMeta.getAllHi().get(i).getCoordinates());
//		}
//		afterMeta.addAll(remoteMeta.getAllLi());
//		
//		
//		int numLost = 0;
//		boolean is_OK = true;
//		for(int i = 0; i<remoteData.size(); i++){
//			is_OK = false;
//			KAry oneData = remoteData.get(i);
//			for(int j = 0; j<order.size(); j++){
//				KAry twoData = order.get(j);
//				boolean is_equal = true;
//				for(int k = 0; k<oneData.getDim(); k++){
//					if(oneData.get(k) != twoData.get(k)){
//						is_equal = false;
//					}
//				}
//				if(is_equal){
//					is_OK = true;
//					break;
//				}
//			}
//			if(!is_OK){
//				System.out.println((numLost++) + " Found a lost data: "+oneData);
//			
//			}
//		}
//		
//		System.out.println("The size of li element on remote is "+ remoteMeta.getAllLi().size());
//			
		// 6. use FIFO to transmit
		ArrayList<Long> FIFO_time = new ArrayList<Long>();
		cur_time = 0;
		for(int i = 0; i<remoteData.size(); i++){
			cur_time += Math.random()*time_mean*2*time_range_percentage + time_mean*(1-time_range_percentage);
			FIFO_time.add(cur_time);
		}
		
		ArrayList<Double> Fifo_coverage = new ArrayList<Double>();
		ArrayList<Long> Fifo_coverage_time = new ArrayList<Long>();
		tempList = null;
		tempList = new ArrayList<KAry>();
		for(int i = 0; i<localData.size(); i++){
			tempList.add(localData.get(i));
		}
		for(int i = 0; i<remoteData.size(); i++){
			tempList.add(remoteData.get(i));
			if((i+1)%gap == 0){
				Fifo_coverage.add(Minerva.coverageComp(tempList, interval) - localCoverageValue);
				Fifo_coverage_time.add(FIFO_time.get(i));
			}
		}
		if(Fifo_coverage.size()*gap < remoteData.size()){
			Fifo_coverage.add(Minerva.coverageComp(remoteData, interval) - localCoverageValue);
			Fifo_coverage_time.add(FIFO_time.get(FIFO_time.size()-1));
		}
		
		fos = new FileOutputStream(resultFifoOrderFileName + postfix);
		ow = new OutputStreamWriter(fos);
		for(int i = 0; i<Fifo_coverage.size(); i++){
			String s = Fifo_coverage_time.get(i) + ","+Fifo_coverage.get(i)+"\n";
			ow.write(s);
			ow.flush();
		}
		ow.close();
		fos.close();
		
		System.out.println("Finished the fifo coverage computation.");
				
		// 7. use distance to transmit
		ArrayList<Long> Dist_time = new ArrayList<Long>();
		
		start_time = System.currentTimeMillis();
		ArrayList<KAry> Dist_order = Distance.orderComp(localData, remoteData);
		end_time = System.currentTimeMillis();
		
		overhead = end_time - start_time;
		System.out.println("The overhead is "+overhead);
		
		cur_time = overhead;
		for(int i = 0; i<remoteData.size(); i++){
			cur_time += Math.random()*time_mean*2*time_range_percentage + time_mean * (1-time_range_percentage);
			Dist_time.add(cur_time);
		}
		
		ArrayList<Double> Dist_coverage = new ArrayList<Double>();
		ArrayList<Long> Dist_coverage_time = new ArrayList<Long>();
		tempList = null;
		tempList = new ArrayList<KAry>();
		for(int i = 0; i<localData.size(); i++){
			tempList.add(localData.get(i));
		}
		for(int i = 0; i<Dist_order.size(); i++){
			tempList.add(Dist_order.get(i));
			if((i+1)%gap == 0){
				Dist_coverage.add(Minerva.coverageComp(tempList, interval) - localCoverageValue);
				Dist_coverage_time.add(Dist_time.get(i));
			}
		}
		if(Dist_coverage.size() * gap < Dist_order.size()){
			Dist_coverage.add(Minerva.coverageComp(Dist_order, interval) - localCoverageValue);
			Dist_coverage_time.add(Dist_time.get(Dist_time.size()-1));
		}
		
		fos = new FileOutputStream(resultDistanceOrderFileName + postfix);
		ow = new OutputStreamWriter(fos);
		for(int i = 0; i<Dist_coverage.size(); i++){
			String s = Dist_coverage_time.get(i) + ","+Dist_coverage.get(i)+"\n";
			ow.write(s);
			ow.flush();
		}
		ow.close();
		fos.close();
	}
	
	public static void coverageComp() throws IOException, ClassNotFoundException{
		String fileRoot = "/home/shiguang/Desktop/Minerva/";
		String greedy_fileName = "Greedy_1_order";
		String greedy_timeName = "Greedy_7_time";
		String fifo_fileName = "Fifo_1_order";
		String fifo_timeName = "Fifo_1_time";
		
		FileInputStream fis = new FileInputStream(fileRoot+greedy_fileName);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		ArrayList<KAry> greedy_data = (ArrayList<KAry>) ois.readObject();
		
		ois.close();
		fis.close();

		fis = new FileInputStream(fileRoot+greedy_timeName);
		ois = new ObjectInputStream(fis);
		
		ArrayList<Long> greedy_time = (ArrayList<Long>) ois.readObject();
		
		ois.close();
		fis.close();

		
		fis = new FileInputStream(fileRoot+fifo_fileName);
		ois = new ObjectInputStream(fis);
		
		ArrayList<KAry> fifo_data = (ArrayList<KAry>) ois.readObject();
		
		ois.close();
		fis.close();
		
		fis = new FileInputStream(fileRoot+fifo_timeName);
		ois = new ObjectInputStream(fis);
		
		ArrayList<Long> fifo_time = (ArrayList<Long>) ois.readObject();
		
		ois.close();
		fis.close();

		
		ois = null; fis = null;
		
		for(int i= 0; i<greedy_time.size(); i++){
			System.out.println(greedy_time.get(i));
		}
		
		ArrayList<Double> greedy_coverage_value = new ArrayList<Double>();		
		ArrayList<Double> fifo_coverage_value = new ArrayList<Double>();
		
		
	}
	
	public static void main(String[] args){
		int[] localDataNums = {1000000};//{5000, 10000, 100000};
		int[] remoteDataNums = {500};
		
		int dimNum = 2;
		double interval = 0.01;
		double hiRatio = 0.1;
		String metaFileName = "/tmp/metaName";
		
		for(int i = 0; i < localDataNums.length; i++){
			for(int j = 0; j < remoteDataNums.length; j++){
				try {
					Stat overhead = overhead_server(dimNum, localDataNums[i], remoteDataNums[j], -1, 0, interval, hiRatio, metaFileName);
					System.out.println(""+localDataNums[i]+","+remoteDataNums[j]+","+overhead.mean+","+overhead.std);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main2(String[] args){
		try {
			Test.coverageComp();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main3(String[] args) {	
		String metaFileName = "/tmp/metaDataFile.file";
		
		try {
			String prefix = "/home/shiguang/Desktop/MinervaData/";
			System.out.println("running...");
			
			double[] intervals = {0.03, 0.045, 0.063, 0.077, 0.089}; // 0.5, 1, 2, 3, 4
			double[] hiRatios = {0.05, 0.1, 0.15, 0.2};
			long[]	roundTripTimes = {154, 195, 783, 6806}; 
			
//			for(int i = 0; i<intervals.length; i++){
//				for(int j = 0; j<hiRatios.length; j++){
//					Test.exec_sim(2, 500, -1, 0, 
//							intervals[i], hiRatios[j], 154, 0.1, 
//							metaFileName, 
//							prefix+"Greedy_order", prefix+"Greedy_time", 
//							prefix+"Dist_order", prefix+"Dist_time", 
//							prefix+"Fifo_order", prefix+"Fifo_time");
//					System.out.println("Finishes "+j+"/"+hiRatios.length);
//				}
//				System.out.println("Finishes "+i+"/"+intervals.length);
//			}
			
			for(int i = 0; i<roundTripTimes.length; i++){
				Test.exec_sim(2, 500, -1, 0, intervals[2], hiRatios[1], roundTripTimes[i], 0.1,
						metaFileName,
						prefix+"Greedy_order", prefix+"Greedy_time",
						prefix+"Dist_order", prefix+"Dist_time", 
						prefix+"Fifo_order", prefix+"Fifo_time");
				System.out.println("Finishes "+i+"/"+roundTripTimes.length);
			}
			
//			Test.exec_sim(2, 500, -1, 1, 
//					0.1, 0.05, 154, 0.1,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
//			Test.exec_sim(2, 500, -1, 1, 
//					0.1, 0.1, 154, 0.1,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
//			Test.exec_sim(2, 500, -1, 1, 
//					0.1, 0.2, 154, 0.1,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//
//			Test.exec_sim(2, 500, -1, 1, 
//					0.1, 0.5, 154, 0.1,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
//			Test.exec_sim(2, 500, -1, 1, 
//					0.1, 0.2, 195, 0.15,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
//			Test.exec_sim(2, 500, -1, 1, 
//					0.1, 0.2, 783, 0.15,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");	
//		
//			Test.exec_sim(2, 500, -1, 1, 
//					0.1, 0.2, 6806, 0.15,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
//			Test.exec_sim(2, 500, -1, 1, 
//					0.05, 0.2, 154, 0.1,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
//			Test.exec_sim(2, 500, -1, 1, 
//					0.15, 0.2, 154, 0.1,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
//			Test.exec_sim(2, 500, -1, 1, 
//					0.2, 0.2, 154, 0.1,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
//			Test.exec_sim(3, 500, -1, 1, 
//					0.15, 0.5, 154, 0.1,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
//			Test.exec_sim(4, 500, -1, 1, 
//					0.3, 0.5, 154, 0.1,
//					metaFileName,
//					prefix+"Greedy_order", 
//					prefix+"Greedy_time", 
//					prefix+"Dist_order",
//					prefix+"Dist_time",
//					prefix+"Fifo_order", 
//					prefix+"Fifo_time");
//			System.out.println("running...");
//			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
