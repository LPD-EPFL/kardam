package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

/**
 * Various helpers
 *
 */
public class Helpers {

	public static double percentile(ArrayList<Double> list, double p) {
		Collections.sort(list);
		double[] target = new double[list.size()];
		for (int i = 0; i < target.length; i++)
			target[i] = list.get(i);

		return (new Percentile()).evaluate(target, p);
	}

	public static double[] randomArray(int length) {
		double[] ret = new double[length];
		for (int i = 0; i < length; i++)
			ret[i] = Math.random() * length;
		return ret;
	}

	public static class Gradient {
		/**
		 * Computed gradient
		 */
		public MyVec grad_vec;
		/**
		 * Model upon which the gradient was computed
		 */
		public MyVec model_vec;
		/**
		 * Epoch of the model that the gradient was computed
		 */
		public int epoch;
		/**
		 * Client id
		 */
		public int id;

		public Gradient(MyVec grad_vec, MyVec model_vec, int epoch, int id) {
			super();
			this.grad_vec = grad_vec;
			this.model_vec = model_vec;
			this.epoch = epoch;
			this.id = id;
		}
	}

	public static class Tuple<X, Y> {
		public X x;
		public Y y;

		public Tuple(X x, Y y) {
			this.x = x;
			this.y = y;
		}

		public X getFirst() {
			return x;
		}

		public Y getSecond() {
			return y;
		}
	}

}
