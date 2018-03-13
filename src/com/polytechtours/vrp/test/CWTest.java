package com.polytechtours.vrp.test;

import java.util.ArrayList;

import com.polytechtours.vrp.cw.ClarkeWright;
import com.polytechtours.vrp.cw.Saving;
import com.polytechtours.vrp.cw.VRPSolution;
import com.polytechtours.vrp.util.EuclideanCalculator;
import com.polytechtours.vrp.data.*;

public class CWTest {
	
	public static void main(String[] args){
		test();
	}
	
	public static void test(){
		String path = "data/CMT/CMT01.xml";
		@SuppressWarnings("resource")
		VRPREPInstanceReader reader = new VRPREPInstanceReader(path);
		IDistanceMatrix distances = reader.getDistanceMatrix();
		IDemands demands = reader.getDemands();
		double capacity = reader.getCapacity("0");
		ClarkeWright cw = new ClarkeWright(distances, demands, capacity);
		VRPSolution solution = (VRPSolution) cw.run();
		System.out.println(solution.toString());
	}
	
	public static void test0(){
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
			System.out.println(savings.get(i).getSourceId() + "," + savings.get(i).getTargetId() + "," + savings.get(i).getSaving());
		}
		
		double[] arrDemands = {0, 18, 26, 11, 30, 21, 16, 29, 37};
		ArrayDemands demands = new ArrayDemands(arrDemands);
		
		ClarkeWright cw = new ClarkeWright(distances, demands, 100);
		VRPSolution solution = (VRPSolution) cw.run();
		System.out.println(solution.toString());
	}

	
}
