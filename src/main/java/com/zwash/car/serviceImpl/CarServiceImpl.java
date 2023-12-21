package com.zwash.car.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import com.zwash.car.exceptions.CarDoesNotExistException;
import com.zwash.auth.exceptions.CarExistsException;
import com.zwash.auth.exceptions.IncorrectTokenException;
import com.zwash.auth.exceptions.UserIsNotFoundException;
import com.zwash.auth.security.JwtUtils;
import com.zwash.car.service.CarService;
import com.zwash.common.pojos.Car;
import com.zwash.common.pojos.User;
import com.zwash.common.pojos.UserCar;
import com.zwash.common.repository.CarRepository;
import com.zwash.common.repository.UserRepository;


@Service
public class CarServiceImpl implements CarService {

	private static final long serialVersionUID = 1L;
	@Autowired
	private CarRepository carRepository;
	@Autowired
	private UserRepository userRepository;

	@Override
	public Car getCar(long id) throws CarDoesNotExistException {
		  Optional<Car> optionalCar = carRepository.findById(id);
		    if (optionalCar.isPresent()) {
		        return optionalCar.get();
		    } else {
		        throw new CarDoesNotExistException("Car with ID " + id + " does not exist");
		    }
	}
	@Override
	public List<Car> getCarsOfUser(User user) throws UserIsNotFoundException {
		   return carRepository.findByUser(user);
	}

	@Override
	public List<Car> getAllCars() {

		 return carRepository.findAll();
	}

	@Override
	public Car register(UserCar car) throws Exception {

		try {
			Car newCar = new Car();
			Car foundcar = carRepository.findByRegisterationPlate(car.getRegistrationPlate());

			if (foundcar != null) {
				throw new CarExistsException(foundcar.getRegisterationPlate());
			}
			String userToken = car.getToken();
			JwtUtils jwtUtils = new JwtUtils();

			try {

			Claims claim =	jwtUtils.verifyJWT(userToken);

			Optional<User> user=userRepository.findByUsername(claim.getSubject());

			 newCar.setUser(user.get());

			} catch (Exception ex) {
				throw new IncorrectTokenException("The token is not valid!");
			}

				newCar.setDateOfManufacture(car.getDateOfManufacture());
				newCar.setManufacture(car.getManufacture());
				newCar.setRegisterationPlate(car.getRegistrationPlate());

				 return carRepository.save(newCar);


		} catch (Exception e) {
			throw e;
		}


	}


	@Override
	public Car getCar(String registerationPlate) throws CarDoesNotExistException {
	 
       Car car = carRepository.findByRegisterationPlate(registerationPlate);
       
       if(car ==null)
       {
         throw new CarDoesNotExistException();
       }else {
    	   return car;
       }
	}

	@Override
	public boolean setCar(User user, Car car) throws Exception {
	    try {
	        List<Car> existingCars = carRepository.findByUser(user);
	        if (!existingCars.isEmpty()) {
	            for (Car existingCar : existingCars) {
	                if (existingCar.getCarId() == car.getCarId()) {
	                    throw new CarExistsException("Car already exists!");
	                }
	            }
	        }

	        car.setUser(user);
	        carRepository.save(car);
	        return true;
	    } catch (Exception e) {
	        // log the error or handle it as appropriate for your application
	    	   throw e;
	    }
	}

	@Override
	public boolean updateCar(Car car) throws Exception {
		 Optional<Car> carOptional = carRepository.findById((car.getCarId()));
		  if (carOptional.isPresent()) {
		        Car existingCar = carOptional.get();
		        existingCar.setManufacture(car.getManufacture());
		        existingCar.setDateOfManufacture(car.getDateOfManufacture());
		        existingCar.setRegisterationPlate(car.getRegisterationPlate());
		        existingCar.setUser(car.getUser());
		        carRepository.save(existingCar);
		        return true;
		    }
		    return false;

	}
	@Override
	public boolean deleteCar(Car car) throws Exception {
		 Optional<Car> carOptional = carRepository.findById((car.getCarId()));
		  if (carOptional.isPresent()) {
		        Car existingCar = carOptional.get();
		     
		        carRepository.delete(existingCar);
		        return true;
		    }
		    return false;

	}

}
