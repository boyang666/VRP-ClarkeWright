package com.polytechtours.vrp.cw;

import java.util.ArrayList;

import com.polytechtours.vrp.data.IDemands;
import com.polytechtours.vrp.data.IDistanceMatrix;

/**
 * The simple implementation of the algorithm of Clarke-Wright </br>
 * This algorithm serves to solve the classic VRP problem with restrict of capacity
 * 
 * <ul>
 * <li>STEP	1:	Calculate the savings s(i, j) = d(D, i) + d(D, j) - d(i, j) for every pair (i, j) of demand points.</li>
 * <li>STEP	2:	Rank the savings s(i, j) and list them in descending order of magnitude. This creates the "savings list." Process the savings list beginning with the topmost entry in the list (the largest s(i, j)).</li>
 * <li>STEP	3:	For the savings s(i, j) under consideration, include link (i, j) in a route if no route constraints will be violated through the inclusion of (i, j) in a route, and if:
 * <ol>
 * 	<li>Either, neither i nor j have already been assigned to a route, in which case a new route is initiated including both i and j.</li>
 * 	<li>Or, exactly one of the two points (i or j) has already been included in an existing route and that point is not interior to that route (a point is interior to a route if it is not adjacent to the depot D in the order of traversal of points), in which case the link (i, j) is added to that same route.</li>
 * 	<li>Or, both i and j have already been included in two different existing routes and neither point is interior to its route, in which case the two routes are merged.</li>
 * </ol>
 * <li>STEP	4:	If the savings list s(i, j) has not been exhausted, return to Step 3, processing the next entry in the list; otherwise, stop: the solution to the VRP consists of the routes created during Step 3. (Any points that have not been assigned to a route during Step 3 must each be served by a vehicle route that begins at the depot D visits the unassigned point and returns to D.)</li>
 * </ul>
 * 
 * @author Boyang Wang
 * @version %I%, %G%
 * @since Mars 13, 2018
 *
 */
public class ClarkeWright implements IOptimizationAlgorithm{
	/**
	 * matrix of distances
	 */
	private IDistanceMatrix distances;
	/**
	 * array of demands
	 */
	private IDemands demands;
	/**
	 * capacity of car
	 */
	private double Q;
	/**
	 * list of savings
	 */
	private ArrayList<Saving> savings;
	/**
	 * solution found by the algorithm
	 */
	VRPSolution solution;
	/**
	 * constructor with matrix of distances, demands and the capacity
	 * @param distances
	 * @param demands
	 * @param Q
	 */
	public ClarkeWright(IDistanceMatrix distances, IDemands demands, double Q){
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
		
		while(savings.size() > 0){// if not all the elements of saving list have been evaluated
			
			//get the first top saving in the list
			Saving saving = savings.get(0);
			int sourceId = saving.getSourceId();
			int targetId = saving.getTargetId();
			
			// if no tours contain the current two nodes
			if(!solution.containsNode(sourceId) && !solution.containsNode(targetId)){
				
				//create a D->i->D route in the solution
				VRPRoute route1 = new VRPRoute();
				route1.add(0);
				route1.add(sourceId);
				route1.add(0);
				route1.setLoad(demands.getDemand(sourceId));
				solution.addRoute(route1);
				int idSource = solution.getRouteIdByNodeId(sourceId);
				
				//create a D->j->D route in the solution
				VRPRoute route2 = new VRPRoute();
				route2.add(0);
				route2.add(targetId);
				route2.add(0);
				route2.setLoad(demands.getDemand(targetId));
				solution.addRoute(route2);
				int idTarget = solution.getRouteIdByNodeId(targetId);
				
				//to join these two route to one if possible
				join(idSource, idTarget, 1);
			}
			// if one node has been added in one route while the other not
			else if(solution.containsNode(sourceId) && !solution.containsNode(targetId)){
				
				//create a D->i->D route for the non-contained node
				VRPRoute routeT = new VRPRoute();
				routeT.add(0);
				routeT.add(targetId);
				routeT.add(0);
				routeT.setLoad(demands.getDemand(targetId));
				solution.addRoute(routeT);
				
				//get the route which contains the node in this route
				VRPRoute routeS = (VRPRoute) solution.getRoute(solution.getRouteIdByNodeId(sourceId));
				//if this node is the first one to visit
				if(routeS.positionOf(sourceId) == 1){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 1);
				}
				//if this node is the last one to visit
				else if(routeS.positionOf(sourceId) == routeS.size() - 2){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 3);
				}
			}
			// if one node has been added in one route while the other not
			else if(!solution.containsNode(sourceId) && solution.containsNode(targetId)){
				
				//create a D->i->D route in the solution
				VRPRoute routeS = new VRPRoute();
				routeS.add(0);
				routeS.add(sourceId);
				routeS.add(0);
				routeS.setLoad(demands.getDemand(sourceId));
				solution.addRoute(routeS);
				
				//get the route which contains the node in this route
				VRPRoute routeT = (VRPRoute) solution.getRoute(solution.getRouteIdByNodeId(targetId));
				//if this node is the first one to visit
				if(routeT.positionOf(targetId) == 1){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 2);
				}
				//if this node is the last one to visit
				else if(routeT.positionOf(targetId) == routeS.size() - 2){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 1);
				}
			}
			// if the twos nodes are already in two different routes
			else if(solution.getRouteIdByNodeId(targetId) != solution.getRouteIdByNodeId(sourceId)){
				
				//get the route for each other
				VRPRoute routeT = (VRPRoute) solution.getRoute(solution.getRouteIdByNodeId(targetId));
				VRPRoute routeS = (VRPRoute) solution.getRoute(solution.getRouteIdByNodeId(sourceId));
				
				//if source node is the first to visit and the target node is also the first
				if(routeS.positionOf(sourceId) == 1 && routeT.positionOf(targetId) == 1){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 0);
				}
				//if source node is the first to visit while the target node is the last
				else if(routeS.positionOf(sourceId) == 1 && routeT.positionOf(targetId) == routeT.size() - 2){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 1);
				}
				//if source node is the last to visit while the target is the first
				else if(routeS.positionOf(sourceId) == routeS.size() - 2 && routeT.positionOf(targetId) == 1){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 2);
				}
				//if source node is the last to visit and the target is the last
				else if(routeS.positionOf(sourceId) == routeS.size() - 2 && routeT.positionOf(targetId) == routeT.size() - 2){
					join(solution.getRouteIdByNodeId(sourceId), solution.getRouteIdByNodeId(targetId), 3);
				}
			}
			
			//remove the saving evaluated from the list
			savings.remove(0);
			
			//calculate for each route the current cost and also the total cost of current solution
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
		
		}
		
		return solution;
	}
	
	/**
	 * Join two different routes into one route.</br>
	 * Four directions are introduced to implement:
	 * <ul>
	 * <li>0: head-head, means the twos routes are combined with their first element.</li>
	 * <li>1: head-tail, means the first route use its head element to combine the last element of the second route</li>
	 * <li>2: tail-head, means the first route use its last element to combine with the first element of the other route</li>
	 * <li>3: tail-tail, means two routes use both their last element to combine</li>
	 * </ul>
	 * 
	 * @param routeIdS id of route
	 * @param routeIdT id of route
	 * @param direction direction of join
	 */
	private void join(int routeIdS, int routeIdT, int direction){
		if(verifyJoin(routeIdS, routeIdT)){// if all the constraints are respected
			
			// get the two routes
			VRPRoute routeS = (VRPRoute) solution.getRoute(routeIdS);
			VRPRoute routeT = (VRPRoute) solution.getRoute(routeIdT);
			
			// recalculate the load for the car
			double load = (routeS.getLoad() + routeT.getLoad());
			
			//head - head
			if(direction == 0){
				solution.remove(routeIdS, 0);
				routeT.remove(0);
				solution.reverse(routeIdS);
				for(int i=0 ;i<routeT.size(); i++){
					solution.insert(routeT.get(i), routeIdS, solution.getRoute(routeIdS).size());
				}
				solution.setLoad(routeIdS, load);
				solution.remove(routeIdT);
			}
			//head - tail
			if(direction == 1){
				routeS.remove(0);
				solution.remove(routeIdT, routeT.size() - 1);
				for(int i=0 ;i<routeS.size(); i++){
					solution.insert(routeS.get(i), routeIdT, solution.getRoute(routeIdT).size());
				}
				solution.setLoad(routeIdT, load);
				solution.remove(routeIdS);
			}
			//tail - head
			if(direction == 2){
				solution.remove(routeIdS, solution.size(routeIdS) - 1);
				routeT.remove(0);
				for(int i=0 ;i<routeT.size(); i++){
					solution.insert(routeT.get(i), routeIdS, solution.getRoute(routeIdS).size());
				}
				solution.setLoad(routeIdS, load);
				solution.remove(routeIdT);
			}
			//tail - tail
			if(direction == 3){
				solution.remove(routeIdS, solution.size(routeIdS) - 1);
				routeT.remove(routeT.size() - 1);
				routeT.reverse();
				for(int i=0 ;i<routeT.size(); i++){
					solution.insert(routeT.get(i), routeIdS, solution.getRoute(routeIdS).size());
				}
				solution.setLoad(routeIdS, load);
				solution.remove(routeIdT);
			}
			
		}
		
	}
	
	/**
	 * Verify if the two routes can be combined</br>
	 * Here we consider the capacity of car which should not be exceeded.
	 * 
	 * @param routeIdS
	 * @param routeIdT
	 * @return true if all constraints are respected
	 */
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
