package utils;

import java.text.NumberFormat;
import java.util.Arrays;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.Vector;

public class MyVec implements Vector {

	public double[] v;
	

	/**
	 * Array representation as double matrix
	 * @param v
	 */
	public MyVec(double[] v) {
		this.v = Arrays.copyOf(v, v.length);
	}
	
	@Override
	public Space getSpace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNaN() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double distance(Point p) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector getZero() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getNorm1() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getNorm() {
	    double s = 0;
	    for (int i=0; i<this.v.length; i++)
	    		s += this.v[i] * this.v[i];
	    return Math.sqrt(s);
	}

	@Override
	public double getNormSq() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getNormInf() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector add(Vector v) {
		MyVec temp = (MyVec) v;
		double[] ret = Arrays.copyOf(this.v, this.v.length);
		for (int i=0; i<ret.length; i++)
			ret[i] += temp.v[i];
		
		return new MyVec(ret);
	}

	@Override
	public Vector add(double factor, Vector v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector subtract(Vector v) {
		MyVec temp = (MyVec) v;
		double[] ret = Arrays.copyOf(this.v, this.v.length);
		for (int i=0; i<ret.length; i++)
			ret[i] -= temp.v[i];
		
		return new MyVec(ret);
	}
	
	@Override
	public Vector subtract(double factor, Vector v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector negate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector normalize() throws MathArithmeticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector scalarMultiply(double a) {
		double[] ret = Arrays.copyOf(this.v, this.v.length);
		for (int i=0; i<ret.length; i++)
			ret[i] *= a;
		
		return new MyVec(ret);	
	}

	@Override
	public boolean isInfinite() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double distance1(Vector v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double distance(Vector v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double distanceInf(Vector v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double distanceSq(Vector v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double dotProduct(Vector v) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString(NumberFormat format) {
		// TODO Auto-generated method stub
		return null;
	}

}
