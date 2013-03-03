package edu.uiuc;

import java.io.File;
import java.util.ArrayList;

public class Minerva{

	private TransList _tList;
	private String _dir;
	private Mapper _m;
	
	public Minerva(String directory, Mapper m){
		_dir = directory;
		_m = m;
	}
	
	public TransList genMetadata(){
		// given the directory of the data, generate the meta data for files.
		
		//1. get the file names of the directory
		ArrayList<File> results = new ArrayList<File>();
		File folder = new File(_dir);
		File[] files = folder.listFiles();
		for( File file : files){
			if(file.isFile()){
				results.add(file);
			}
		}
		
		TransList t = new TransList();
		// 2. get the KdBox features of all the data
		// 3. generate the Translist instance		
		for( File file : results){
			String name = file.getName();
			KdBox features = _m.map(name);
			KdBox intervals = Configuration.intervals;
			
			Data temp = new Data(name, features, intervals);
			t.addData(temp);
		}
		
		return t;
		
		// 4. generate the order based on which to transmit data
		
		// 5. output to a file (optional?)
	}
	
	public TransList getTList(){
		return _tList;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ArrayList<File> results = new ArrayList<File>();
//		File folder = new File("/Users/Shiguang/Downloads");
//		File[] files = folder.listFiles();
//		for( File file : files){
//			System.out.println(file.getName());
//		}
		File f = new File("/Users/shiguang/Desktop/123.txt");
		System.out.println(f.renameTo(new File("/Users/shiguang/Desktop/1234.txt")));
//		f.renameTo(new File("/Users/shiguang/Desktop/123.txt"));
		
		f = new File("/Users/shiguang/Desktop/1234.txt");
		System.out.println(f.renameTo(new File("/Users/shiguang/Desktop/123.txt")));
		
	}
}
