package com.polytechtours.vrp.cw;

import java.util.ArrayList;

import com.polytechtours.vrp.data.ArrayDistanceMatrix;
import com.polytechtours.vrp.data.IDistanceMatrix;
import com.polytechtours.vrp.util.EuclideanCalculator;

public class Saving implements Comparable<Saving>{

	private int sourceId;
	
	private int targetId;
	
	private double saving;
	
	public static ArrayList<Saving> calc(IDistanceMatrix distances){
		
		// initialize the saving list
		ArrayList<Saving> savings = new ArrayList<Saving>();
		
		// calculate the saving list
		for(int i=1; i<distances.size(); i++){
			for(int j=i+1; j<distances.size(); j++){
				Saving saving = new Saving();
				saving.setSourceId(i);
				saving.setTargetId(j);
				saving.setSaving(distances.getDistance(i, 0) + distances.getDistance(j, 0) - distances.getDistance(i, j));
				savings.add(saving);
			}
		}
		
		// sort the list with descent order
		java.util.Collections.sort(savings);
		
		return savings;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public double getSaving() {
		return saving;
	}

	public void setSaving(double saving) {
		this.saving = saving;
	}
	
	public static void main(String[] args){
		double[][] coor  = new double[9][2];
		coor[0][0] = 40;
		coor[0][1] = 40;
		coor[1][0] = 22;
		coor[1][1] = 22;
		coor[2][0] = 36;
		coor[2][1] = 26;
		coor[3][0] = 21;
		coor[3][1] = 45;
		coor[4][0] = 45;
		coor[4][1] = 35;
		coor[5][0] = 55;
		coor[5][1] = 20;
		coor[6][0] = 55;
		coor[6][1] = 45;
		coor[7][0] = 26;
		coor[7][1] = 59;
		coor[8][0] = 55;
		coor[8][1] = 65;
		
		ArrayDistanceMatrix distances = new ArrayDistanceMatrix(EuclideanCalculator.calc(coor));
		ArrayList<Saving> savings = Saving.calc(distances);
		java.util.Collections.sort(savings);
		for(int i=0; i<savings.size(); i++){
			System.out.println(savings.get(i).getSaving());
		}
	}

	@Override
	public int compareTo(Saving other) {
		if(other.saving > saving){
			return 1;
		}
		else if(other.saving < saving){
			return -1;
		}
		else{
			return 1;
		}
	}
	
}
