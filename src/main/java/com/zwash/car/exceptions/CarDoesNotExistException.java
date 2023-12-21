package com.zwash.car.exceptions;

public class CarDoesNotExistException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = -3267837584889499032L;
    public CarDoesNotExistException()
    {

    }
	public CarDoesNotExistException(String car)
	{
		super(car+ " is not in the System");
	}
}
