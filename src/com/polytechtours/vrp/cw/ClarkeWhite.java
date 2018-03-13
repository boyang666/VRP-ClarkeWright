package com.polytechtours.vrp.cw;

import java.util.ArrayList;

import com.polytechtours.vrp.data.IDemands;
import com.polytechtours.vrp.data.IDistanceMatrix;

public class ClarkeWhite implements IOptimizationAlgorithm{
	
	private IDistanceMatrix distances;
	
	private IDemands demands;
	
	private double Q;
	
	private ArrayList<Saving> savings;
	
	VRPSolution solution;

	public ClarkeWhite(IDistanceMatrix distances, IDemands demands, double Q){
		this.distances = distances;
		this.demands = demands;
		this.Q = Q;
		this.savings = Saving.calc(distances);
		this.solution = new VRPSolution();
	}
	

	public VRPSolution getSolution() {
		return solution;
	}


	public void setSolution(VRPSolution solution) {
		this.solution = solution;
	}


	public IDistanceMatrix getDistances() {
		return distances;
	}

	public void setDistances(IDistanceMatrix distances) {
		this.distances = distances;
	}

	public IDemands getDemands() {
		return demands;
	}

	public void setDemands(IDemands demands) {
		this.demands = demands;
	}

	public double getQ() {
		return Q;
	}

	public void setQ(double q) {
		Q = q;
	}

	public ArrayList<Saving> getSavings() {
		return savings;
	}

	public void setSavings(ArrayList<Saving> savings) {
		this.savings = savings;
	}

	@Override
	public ISolution run() {
		
		while(savings.size() > 0){
			Saving saving = savings.get(0);
			int sourceId = saving.getSourceId();
			int targetId = saving.getTargetId();
			
			if(!solution.containsNode(sourceId) && !solution.containsNode(targetId)){
				
				VRPRoute route1 = new VRPRoute();
				route1.add(0);
				route1.add(sourceId);
				route1.add(0);
				route1.setLoad(demands.getDemand(sourceId));
				solution.addRoute(route1);
				int idSource = solution.getRouteIdByNodeId(sourceId);
				
				VRPRoute route2 = new VRPRoute();
				route2.add(0);
				route2.add(targetId);
				route2.add(0);
				route2.setLoad(demands.getDemand(targetId));
				solution.addRoute(route2);
				int idTarget = solution.getRouteIdByNodeId(targetId);
				
				
				join(idSource, idTarget, 1);
			}
			
			else if(solution.containsNode(sourceId) && !solution.containsNode(targetId)){
				VRPRoute routeT = new VRPRoute();
				routeT.add(0);
				routeT.add(targetId);
				routeT.add(0);
				routeT.setLoad(demands.getDemand(targetId));
				solution.addRoute(routeT);
				
				VRPRoute routeS = (VRPRoute) solution.getRoute(solution.getRouteIdByNodeId(sourceId));
				if(routeS.positionOf(sourceId) == 1){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 1);
				}
				else if(routeS.positionOf(sourceId) == routeS.size() - 2){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 3);
				}
			}
			
			else if(!solution.containsNode(sourceId) && solution.containsNode(targetId)){
				VRPRoute routeS = new VRPRoute();
				routeS.add(0);
				routeS.add(sourceId);
				routeS.add(0);
				routeS.setLoad(demands.getDemand(sourceId));
				solution.addRoute(routeS);
				
				VRPRoute routeT = (VRPRoute) solution.getRoute(solution.getRouteIdByNodeId(targetId));
				if(routeT.positionOf(targetId) == 1){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 2);
				}
				else if(routeT.positionOf(targetId) == routeS.size() - 2){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 1);
				}
			}
			
			else{
				VRPRoute routeT = (VRPRoute) solution.getRoute(solution.getRouteIdByNodeId(targetId));
				VRPRoute routeS = (VRPRoute) solution.getRoute(solution.getRouteIdByNodeId(sourceId));
				
				if(routeS.positionOf(sourceId) == 1 && routeT.positionOf(targetId) == 1){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 0);
				}
				else if(routeS.positionOf(sourceId) == 1 && routeT.positionOf(targetId) == routeT.size() - 2){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 1);
				}
				else if(routeS.positionOf(sourceId) == routeS.size() - 2 && routeT.positionOf(targetId) == 1){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 2);
				}
				else if(routeS.positionOf(sourceId) == routeS.size() - 2 && routeT.positionOf(targetId) == routeT.size() - 2){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 3);
				}
			}
			
			savings.remove(0);
			
			double costTotal = 0.0;
			for(int i=0; i<solution.size(); i++){
				double cost = 0.0;
				for(int j=0; j<solution.size(i) - 1; j++){
					cost += distances.getDistance(solution.getNode(i, j), solution.getNode(i, j+1));
				}
				solution.setCost(i, cost);
				costTotal += cost;
			}
			solution.setOF(costTotal);
			System.out.println(solution);
		}
		
		return solution;
	}
	
	private void join(int routeIdS, int routeIdT, int direction){
		if(verifyJoin(routeIdS, routeIdT)){
			VRPRoute routeS = (VRPRoute) solution.getRoute(routeIdS);
			VRPRoute routeT = (VRPRoute) solution.getRoute(routeIdT);
			double load = (routeS.getLoad() + routeT.getLoad());
			
			//head - head
			if(direction == 0){
				solution.remove(routeIdS, 0);
				routeT.remove(0);
				//routeS.reverse();
				solution.reverse(routeIdS);
				for(int i=0 ;i<routeT.size(); i++){
					//routeS.add(routeT.get(i));
					solution.insert(routeT.get(i), routeIdS, solution.getRoute(routeIdS).size());
				}
				solution.setLoad(routeIdS, load);
				//solution.remove(routeIdS);
				solution.remove(routeIdT);
				//solution.addRoute(routeS);
			}
			//head - tail
			if(direction == 1){
				routeS.remove(0);
				solution.remove(routeIdT, routeT.size() - 1);
				for(int i=0 ;i<routeS.size(); i++){
					//routeT.add(routeS.get(i));
					solution.insert(routeS.get(i), routeIdT, solution.getRoute(routeIdT).size());
				}
				solution.setLoad(routeIdT, load);
				solution.remove(routeIdS);
				//solution.remove(routeIdT);
				//solution.addRoute(routeT);
			}
			//tail - head
			if(direction == 2){
				//routeS.remove(routeS.size() - 1);
				solution.remove(routeIdS, solution.size(routeIdS) - 1);
				routeT.remove(0);
				for(int i=0 ;i<routeT.size(); i++){
					//routeS.add(routeT.get(i));
					solution.insert(routeT.get(i), routeIdS, solution.getRoute(routeIdS).size());
				}
				solution.setLoad(routeIdS, load);
				//solution.remove(routeIdS);
				solution.remove(routeIdT);
				//solution.addRoute(routeS);
			}
			//tail - tail
			if(direction == 3){
				//routeS.remove(routeS.size() - 1);
				solution.remove(routeIdS, solution.size(routeIdS));
				routeT.remove(routeT.size() - 1);
				routeT.reverse();
				for(int i=0 ;i<routeT.size(); i++){
					//routeS.add(routeT.get(i));
					solution.insert(routeT.get(i), routeIdS, solution.getRoute(routeIdS).size());
				}
				solution.setLoad(routeIdS, load);
				//solution.remove(routeIdS);
				solution.remove(routeIdT);
				//solution.addRoute(routeS);
			}
			
		}
		
	}
	
	private boolean verifyJoin(int routeIdS, int routeIdT){
		boolean satisfy = true;
		VRPRoute routeS = (VRPRoute) solution.getRoute(routeIdS);
		VRPRoute routeT = (VRPRoute) solution.getRoute(routeIdT);
		double demandTotal = routeS.getLoad() + routeT.getLoad();
		if(demandTotal > Q){
			satisfy = false;
		}
		
		return satisfy;
	}
	
	
}
