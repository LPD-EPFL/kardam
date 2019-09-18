package example;

import utils.*;
import utils.Helpers.Gradient;

/**
 * Example usage of Kardam.
 * No actual training or asynchrony is present as the goal is to simply illustrate the API and functionality of Kardam.
 */
public class RandomSGD {

	/**
	 * Constant learning rate
	 */
	private double lrate;

	/**
	 * Total number of workers
	 */
	private int workers;
	
	/**
	 * Number of training iterations
	 */
	private int iterations;
	
	/**
	 * Epoch at the server
	 */
	private int epoch;

	/**
	 * Staleness-aware dampening
	 */
	private Dampening.Policy policy;

	/**
	 * Param alpha exponent used for exponential dampening
	 */
	private double alpha;

	/**
	 * Stateful filtering component of Kardam
	 */
	private Filtering<MyVec, MyVec> filter;

	/**
	 * Last used gradients; useful for the filtering component
	 */
	private MyVec lastGrad;

	/**
	 * Last model version; useful for the filtering component
	 */
	private MyVec lastModel;
	
	/**
	 * Current version of the model
	 */
	private MyVec currModel;
	
	/**
	 * Number of features for the ML model
	 */
	private int modelSize;


	public RandomSGD() {
		modelSize = 100;
		lrate = 0.1;
		alpha = 0.2;
		policy = Dampening.Policy.EXPONENTIAL;
		workers = 10;
		iterations = 100;
		filter = new Filtering<MyVec, MyVec>(workers);
		lastGrad = null;
		lastModel = null;
		currModel = new MyVec(Helpers.randomArray(modelSize));
	}
	
	public static void main(String[] args) {
		RandomSGD solver = new RandomSGD();
		solver.train();
	}
	
	public void train() {
		for (int i=0; i<iterations; i++) {
			System.out.println("Begin of iteration: " + i);
            Gradient g = gradient();
            descent(g);
		}
	}
	
	/**
	 * Generate a random gradient
	 * @return
	 */
	public Gradient gradient() {
		MyVec grad_vec = new MyVec(Helpers.randomArray(modelSize));
		int workerId = (int)(10.0 * Math.random());
		return new Gradient(grad_vec, currModel, epoch, workerId);
	}
	
	
	public void descent(Gradient gradient) {
		int tau = this.epoch - gradient.epoch;

		// staleness-aware dampening component
		// Note: given the lack of asynchrony of this example the following will not change the original gradient (i.e., staleness = 0)
		MyVec dampenedGrad = (MyVec) gradient.grad_vec.scalarMultiply(Dampening.getDampen(tau, alpha, policy));

		// update filtering component info
		filter.setModel(gradient.id, gradient.model_vec);
		if (filter.setGrad(gradient.id, (MyVec) dampenedGrad.scalarMultiply(lrate), gradient.epoch)) {
			filter.updateLip(gradient.id);
		}

		// filtering component check
		// Note: given the random gradients of this example the check will always fail
		if (filter.filterCheck(gradient.id, gradient.grad_vec, lastGrad, currModel, lastModel, tau)) {
			lastModel = new MyVec(currModel.v);
			lastGrad = new MyVec(gradient.grad_vec.v);
			currModel = (MyVec) currModel.subtract(gradient.grad_vec);
			epoch++;
		} else
			System.out.println("Kardam: Filtered gradient");
		
	}

}
