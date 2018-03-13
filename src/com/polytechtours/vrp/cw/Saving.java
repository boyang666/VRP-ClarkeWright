package com.polytechtours.vrp.cw;

import java.util.ArrayList;

import com.polytechtours.vrp.data.IDistanceMatrix;

/**
 * A saving object is constructed by three variables: </br>
 * <ul>
 * <li>id of first node</li>
 * <li>id of second node</li>
 * <li>saving calculated by using Di + Dj - ij (D as Depot)</li>
 * </ul>
 * A saving indicates the distance saved when we combine these two nodes in one tour
 * 
 * @author Boyang Wang
 * @version %I%, %G%
 * @since Mars 13, 2018
 *
 */
public class Saving implements Comparable<Saving>{

	/**
	 * id of first node
	 */
	private int sourceId;
	/**
	 * id od second node
	 */
	private int targetId;
	/**
	 * saving calculated 
	 */
	private double saving;
	
	/**
	 * Function to calculate a list of savings </br>
	 * By using the matrix of distance in form of {@link IDistanceMatrix}
	 * 
	 * @param distances
	 * @return list of savings
	 */
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
