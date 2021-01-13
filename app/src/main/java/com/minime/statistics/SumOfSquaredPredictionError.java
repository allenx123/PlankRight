package com.minime.statistics;

public class SumOfSquaredPredictionError {
	public static double calculate(DataPoint[] data) {
		double Q = 0.0;
		if (data==null)
			return Q;
		for (int i=0; i<data.length; i++) {
			Q = Q + (data[i].x-data[i].y) * (data[i].x-data[i].y);
		}		
		return Q;
	}
}
