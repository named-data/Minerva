package com.secon.submission;

public class MaxIntHeap {
	private final int filling = 1; 
	private int capacity;
	private int size;
	public double[] heapArr;
	public int[] indexArr;
	public int[] originalIndexArr;
	boolean addable;
	public int popedNum;
	
	public MaxIntHeap(int s){
		this.capacity = s+filling;
		heapArr = new double[capacity];
		indexArr = new int[capacity];
		originalIndexArr = new int[capacity];
		this.size = 0+filling;
		
		addable = true;
		popedNum = 0;
	}
	
	public MaxIntHeap(){
		this.capacity = 1+filling;
		heapArr = new double[capacity];
		indexArr = new int[capacity];
		originalIndexArr = new int[capacity];
		this.size = 0+filling;
		
		addable = true;
		popedNum = 0;
	}
	
	public double getItemOnOriginalIndex(int idx){
		assert(idx + filling < size);
		return heapArr[indexArr[idx+filling]];
	}

	public double getItemOnCurrentIndex(int idx){
		assert(idx + filling < size);
		return heapArr[idx+filling];
	}
	
	public int getSize(){
		return size - filling;
	}
	
	public int getCapacity(){
		return capacity - filling;
	}
	
	public int popMax(){
		addable = false; // once starting pop, we cannot add item
		swap(filling, size-1);
		size--;
		popedNum ++;
		heapify(filling);
		return originalIndexArr[size]-filling;
	}
	
	public double peepMaxValue(){
		return heapArr[filling];
	}
	
	public void updateValueOnOriginalIndex(int idx, double newValue){
		assert(idx+filling < size);
		int index = indexArr[idx+filling];
		heapArr[index] = newValue;
		heapify(index);
	}
	
	public void addItem(double item){
		if(! addable){
			return;
		}
		heapArr[size] = item;
		indexArr[size] = size;
		originalIndexArr[size] = size;
		heapify(size);

		size++;
		if(size == capacity){
			doubleCapacity();
		}
	}
	
	private void doubleCapacity(){
		capacity = 2*capacity-filling;
		double[] tempHeapArr = new double[capacity];
		int[] tempIndexArr = new int[capacity];
		int[] tempOriginalIndexArr = new int[capacity];
		for(int i = filling; i<size; i++){
			tempHeapArr[i] = heapArr[i];
			tempIndexArr[i] = indexArr[i];
			tempOriginalIndexArr[i] = originalIndexArr[i];
		}
		heapArr = tempHeapArr;
		indexArr = tempIndexArr;
		originalIndexArr = tempOriginalIndexArr;
		tempHeapArr = null;
		tempIndexArr = null;
		tempOriginalIndexArr =null;
		System.gc();
	}
	
	private void swap(int idx1, int idx2){
		double tempValue;
		int tempIdx;
		
		assert(idx1>filling && idx1<size);
		assert(idx2>filling && idx2<size);
		
		tempValue = heapArr[idx1];
		heapArr[idx1] = heapArr[idx2];
		heapArr[idx2] = tempValue;
		
		tempIdx = indexArr[originalIndexArr[idx1]];
		indexArr[originalIndexArr[idx1]] = indexArr[originalIndexArr[idx2]];
		indexArr[originalIndexArr[idx2]] = tempIdx;

		tempIdx = originalIndexArr[idx1];
		originalIndexArr[idx1] = originalIndexArr[idx2];
		originalIndexArr[idx2] = tempIdx;
	}
	
	private void heapifyUpwards(int idx){
		while(idx/2 >= filling){
			if(heapArr[idx] > heapArr[idx/2]){
				swap(idx, idx/2);
				idx = idx/2;
			} else{
				return;
			}	
		}
	}
	
	private void heapifyDownwards(int idx){
		while(idx*2<size || idx*2+1 < size){
			if(idx*2+1 == size){
				if(heapArr[idx] < heapArr[idx*2])
					swap(idx, idx*2);
				return;
			}
			
			int maxChildIdx = heapArr[idx*2]>heapArr[idx*2+1]? idx*2 : idx*2+1;
			if(heapArr[idx] < heapArr[maxChildIdx]){
				swap(idx, maxChildIdx);
				idx = maxChildIdx;
			} else{
				return;
			}
		}
	}
	
	private void heapify(int idx){
		// upwards heapify
		assert(idx > filling && idx < size);
		
		if (idx / 2 >= filling) {
			if (heapArr[idx] > heapArr[idx / 2]) {
				// in this case the current node is great than its parent
				heapifyUpwards(idx);
				return;
			}
		}
		if (idx * 2 < size || idx * 2 + 1 < size) {
			if (heapArr[idx] < heapArr[idx * 2]){
				// in this case the current node is smaller than some of its
				// child
				heapifyDownwards(idx);
				return;
			}
			if(idx*2+1 < size && heapArr[idx] < heapArr[idx * 2 + 1]) {
				heapifyDownwards(idx);
				return;
			}
		}
	}
	
	public void printHeap(){
		System.out.print("Data: ");
		for(int i = filling; i<size; i++){
			System.out.print(heapArr[i]+", ");
		}
		System.out.println();
		System.out.print("Index: ");
		for(int i = filling; i<size; i++){
			System.out.print(indexArr[i]+", ");
		}
		for(int i = 0; i<this.popedNum; i++ ){
			System.out.print(indexArr[i+size]+", ");
		}
		System.out.println();
	}
	
	public static double[] heapSort(MaxIntHeap h){
		int size = h.getSize();
		double[] result = new double[size];
		
		for(int i = 0; i<size; i++){
			h.popMax();
		}
		for(int i = 0; i<size; i++){
			result[i] = h.heapArr[i+h.filling];
		}
		
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		int[] array = {1,2,3,4,5};
		MaxIntHeap h = new MaxIntHeap();
		for(int i= 0; i<10; i++){
			h.addItem(Math.random());
		}
//		System.out.println(h.peepMaxValue());
//		h.printHeap();
//		h.updateValueOnOriginalIndex(3, 30);
//		h.printHeap();
//		System.out.println(h.getItemOnOriginalIndex(3)); 
		h.updateValueOnOriginalIndex(3, -5);
//		h.printHeap();
		for(int i = 0; i<10; i++){
			System.out.println(h.popMax());
//			h.printHeap();
		}
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

}
