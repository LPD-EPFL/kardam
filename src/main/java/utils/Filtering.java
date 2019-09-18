package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.math3.geometry.Vector;

import utils.Helpers.Tuple;

public class Filtering<Tgrad extends Vector, Tmodel extends Vector> {


	private int currWorker;
	private int workers;
	
	// the following 4 field are useful for the simulation (otherwise each worker directly sends the lip value)
	private HashMap<Integer, Tuple<Tgrad, Integer>> grads;
	private HashMap<Integer,Tmodel> models;
	private HashMap<Integer, Double> modelDiffNorm; // lipschitzian numerator
	private HashMap<Integer, Double> gradDiffNorm; // lipschitzian denominator
		
	private HashMap<Integer, ArrayList<Double>> lips;
	
	private int subsequentRejects = 0;
	
	public Filtering(int workers) {
		this.workers = workers;
		grads = new HashMap<>(workers);
		models = new HashMap<>(workers);
		modelDiffNorm = new HashMap<>(workers);
		gradDiffNorm = new HashMap<>(workers);
				
		lips = new HashMap<>(workers);
		
		currWorker = 0;
	}
	
	/**
	 * Cache a gradient response from a worker
	 * @param workerId
	 * @param g
	 * @param epoch model epoch that the g was computed upon
	 * @return if this is the first gradient from this worker
	 */
	public boolean setGrad(int workerId, Tgrad g, int epoch) {
		if (grads.containsKey(workerId)) {
			if (grads.get(workerId).y == epoch) {
				System.out.println("Ignore grad push: Grad epoch same as previous one: " + epoch);
				return false;
			}
			gradDiffNorm.put(workerId, g.subtract(grads.get(workerId).x).getNorm());// * Math.abs(grads.get(workerId).y - epoch));
			grads.put(workerId, new Tuple(g, epoch));
			return true;
		}
		else {
			grads.put(workerId, new Tuple(g, epoch));
			return false;
		}
	}
	

	/**
	 * Checks whether the given gradient is computed on the prev model (used to avoid kardam filtering stuck)
	 * @param workerId
	 * @param epoch
	 * @return
	 */
	public boolean checkGradVersion(int workerId, int epoch) {
		if (!grads.containsKey(workerId))
			return false;
		else if (epoch == grads.get(workerId).y)
			System.out.println("ERROR? + " + epoch);
		return epoch < grads.get(workerId).y;
	}
	
	
	public Tuple<Tgrad, Integer> getGrad(int workerId) {
		return grads.get(workerId);
	}
	
	/**
	 * Checks whether the model m (to be sent to the worker) is the same as the previous one
	 * @param workerId
	 * @param m
	 * @return
	 */
	public boolean checkModelVersion(int workerId, Tmodel m) {
		if (!models.containsKey(workerId))
			return false;
		if (m.subtract(models.get(workerId)).getNorm() == 0)
			return true;
		else
			return false;
	}
	
	
	/**
	 * Cache a model given at a worker
	 * @param workerId
	 * @param mcurr current version of the model (to-be-sent to the worker)
	 */
	public void setModel(int workerId, Tmodel mcurr) {
		if (models.containsKey(workerId)) {
			double norm = mcurr.subtract(models.get(workerId)).getNorm();
			if (norm == 0) { // same prev model
				System.out.println("Ignore model push: zero norm");
				return;
			}
			modelDiffNorm.put(workerId, norm);
		}
		models.put(workerId, mcurr);	
	}
	
	/**
	 * If true then gradient passes the Kardam filter
	 * @param workerId
	 * @param g gradient
	 * @param lastGrad last used gradient by the server
	 * @param currModel current version of the model
	 * @param lastModel last used version of the model by the server
	 * @return
	 */
	public boolean filterCheck(int workerId, Tgrad g, Tgrad lastGrad, Tmodel currModel, Tmodel lastModel, int tau) {
		ArrayList<Double> l = new ArrayList<>();
		if (lips.isEmpty()) {
			subsequentRejects = 0;
			return true;
		}
		for (int worker : lips.keySet()) {
			l.add(Collections.max(lips.get(worker)));
			Collections.sort(lips.get(worker));
		}
		
		// get median
		double midLip = Helpers.percentile(l, 50);
		double low = Helpers.percentile(l, 100/(double) 3);
		double high = Helpers.percentile(l, 200/(double) 3);
		
		double checkNorm;
		if (lastGrad == null) {
			System.out.println("Kardam: Null previous gradient in kardam check");
			checkNorm = g.getNorm() / currModel.getNorm();
		}
		else {
			double gnorm = g.subtract(lastGrad).getNorm();
			double mnorm = currModel.subtract(lastModel).getNorm();
			checkNorm = gnorm / mnorm;
		}
		if (checkNorm <= high) {
			subsequentRejects = 0;
			return true;			
		}
		else if (subsequentRejects == workers) {
			System.out.println("Too maby subsequent rejects => Accepting");
			subsequentRejects = 0;
			return true;
		}
		else {
			subsequentRejects++;
			return false;
		}

		
	}

	/**
	 * Insert the empirical Lipschitz coefficient of the given worker
	 * The value is calculated from the stored info
	 * @param workerId
	 */
	public void updateLip(int workerId) {
		double lip = gradDiffNorm.get(workerId) / modelDiffNorm.get(workerId);
		
		if (!lips.containsKey(workerId))
			lips.put(workerId, new ArrayList<>());
		
		if (lips.get(workerId).size() > 25) {
			System.out.println("Removing lip value for client: " + workerId);
			lips.get(workerId).remove(0);
		}
		lips.get(workerId).add(lip);
	}
	
	public void nextWorkerId() {
		currWorker = (currWorker + 1) % workers;
	}
	
	public int currentWorkerId() {
		return currWorker;
	}
	
	public int numWorkers() {
		return workers;
	}
}
