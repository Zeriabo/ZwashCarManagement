package com.zwash.car.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zwash.auth.exceptions.UserIsNotFoundException;
import com.zwash.car.exceptions.CarDoesNotExistException;
import com.zwash.common.pojos.Car;
import com.zwash.common.pojos.User;
import com.zwash.common.pojos.UserCar;

@Service
public interface CarService  extends Serializable {

	Car getCar(long id) throws CarDoesNotExistException;
    Car getCar(String registerationPlate) throws CarDoesNotExistException;
    boolean setCar(User user, Car car) throws Exception;
	List<Car> getCarsOfUser(User user) throws UserIsNotFoundException;
    Car register(UserCar car) throws Exception;
	boolean updateCar(Car car) throws Exception;
   	boolean deleteCar(Car car) throws Exception;
	List<Car> getAllCars();



}
