package com.github.bindernews.lwjgltest;

public class Accelerator
{
	private double mmin,
					mmax,
					mvalue,
					mdifference;

	public Accelerator(double min, double max)
	{
		setAll(min, max, min);
	}

	public Accelerator(double min, double max, double value)
	{
		setAll(min, max, value);
	}
	
	public void acceleratePercent(float percent)
	{
		mvalue += mdifference*percent;
		checkValue();
	}
	
	public void accelerateAmount(double amt)
	{
		mvalue += amt;
		checkValue();
	}
	
	public double getValue()
	{
		return mvalue;
	}
	
	public void setValue(double val)
	{
		mvalue = val;
		checkValue();
	}
	
	public double getMin()
	{
		return mmin;
	}
	
	/**
	 * Convenience method for setting the minimum. Uses setAll to actually change it.
	 */
	public void setMin(double newmin)
	{
		setAll(newmin, mmax, mvalue);
	}
	
	public double getMax()
	{
		return mmax;
	}
	
	/**
	 * Convenience method for setting the maximum. Uses setAll to actually change it.
	 */
	public void setMax(double newmax)
	{
		setAll(mmin, newmax, mvalue);
	}
	
	/**
	 * Allows setting of all values at once. This is the best way to reuse Accelerator objects.
	 * This is also where most of the validity checking is.
	 */
	public void setAll(double min, double max, double value)
	{
		if (min < max)
		{
			mmin = min;
			mmax = max;
		}
		else
		{
			mmin = max;
			mmax = min;
		}
		if (testValue(value))
			mvalue = value;
		else
			mvalue = mmin;
		recalcDifference();
	}
	
	public boolean testValue(double val)
	{
		return (mmin <= val && val <= mmax);
	}
	
	private void checkValue()
	{
		if (mvalue > mmax)
			mvalue = mmax;
		if (mvalue < mmin)
			mvalue = mmin;
	}
	
	private void recalcDifference()
	{
		mdifference = mmax - mmin;
	}
}