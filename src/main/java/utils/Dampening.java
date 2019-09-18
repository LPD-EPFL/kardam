package utils;

public class Dampening {

	public static enum Policy {
		INVERSE, AVERAGING, EXPONENTIAL,
	}

	public static double getDampen(int tau, double alpha, Policy policy) {
		// staleness-aware learning
		double l_tau = 1;
		if (policy == Policy.AVERAGING)
			l_tau = 1;
		else if (policy == Policy.INVERSE)
			l_tau = 1 / (double) (tau + 1);
		else if (policy == Policy.EXPONENTIAL)
			l_tau = Math.exp((-1) * alpha * tau);

		return l_tau;
	}
}