package com.minime.statistics;

/**
 * Created by allen on 4/5/2018.
 */

public class RegressionLineForLinearAccelerationSensor extends RegressionLine {

    //allen: negative value offset
    double negativeValueOffset = 0.0;
    /**
     * * Constructor.
     * * @param data the array of data points      */
    public RegressionLineForLinearAccelerationSensor(DataPoint data[], int n) {

        //this part is to restrict the data set to first n values, n <= data.length
        DataPoint[] newData = new DataPoint[n];
        if (n>=data.length)
            newData = data;
        else
        for (int i=0; i<n; i++) {
            newData[i] = data[i];
        }
        data = newData;

        //see if any of the given Y values of the data is negative
        //if yes, the one with the largest absolute value will become the negativeValueOffset
        for (int i=0; i<data.length; i++) {
        	if (Math.abs(data[i].getY())>negativeValueOffset && data[i].getY()<0.0) {
                negativeValueOffset = Math.abs(data[i].getY());
            }
        }
        //if negativeValueOffset is bigger than 0, then adjust all y values in the data
        for (int i=0; i<data.length; i++) {
            data[i].setY(data[i].getY()+negativeValueOffset);
        }
        //then re-calculate all the stats values
        super.reset();
        for (int i=0; i<data.length; i++) {
            addDataPoint(data[i]);
        }

    }
    //when returning A0, you need to put back the negativeValueOffset value
    @Override
    public double getA0()     {
        double a0FromSuper = super.getA0();
        return a0FromSuper-negativeValueOffset;
    }
}
