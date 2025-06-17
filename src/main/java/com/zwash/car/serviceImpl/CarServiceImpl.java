package com.zwash.car.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

import com.zwash.auth.exceptions.CarExistsException;
import com.zwash.auth.exceptions.IncorrectTokenException;
import com.zwash.auth.exceptions.UserIsNotFoundException;
import com.zwash.auth.security.JwtUtils;
import com.zwash.car.exceptions.CarDoesNotExistException;
import com.zwash.car.service.CarService;
import com.zwash.common.pojos.Car;
import com.zwash.common.pojos.User;
import com.zwash.common.pojos.UserCar;
import com.zwash.common.repository.CarRepository;
import com.zwash.common.repository.UserRepository;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Car getCar(long id) throws CarDoesNotExistException {
        return carRepository.findById(id)
                .orElseThrow(() -> new CarDoesNotExistException("Car with ID " + id + " does not exist"));
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
        Car foundCar = carRepository.findByRegisterationPlate(car.getRegistrationPlate());
        if (foundCar != null) {
            throw new CarExistsException("Car with plate " + car.getRegistrationPlate() + " already exists.");
        }

        Claims claim;
        try {
            claim = jwtUtils.verifyJWT(car.getToken());
        } catch (Exception ex) {
            throw new IncorrectTokenException("The token is not valid!");
        }

        Optional<User> userOpt = userRepository.findByUsername(claim.getSubject());
        if (userOpt.isEmpty()) {
            throw new UserIsNotFoundException("User not found for token subject: " + claim.getSubject());
        }

        Car newCar = new Car();
        newCar.setUser(userOpt.get());
        newCar.setDateOfManufacture(car.getDateOfManufacture());
        newCar.setManufacture(car.getManufacture());
        newCar.setRegisterationPlate(car.getRegistrationPlate());

        return carRepository.save(newCar);
    }

    @Override
    public Car getCar(String registrationPlate) throws CarDoesNotExistException {
        Car car = carRepository.findByRegisterationPlate(registrationPlate);
        if (car == null) {
            throw new CarDoesNotExistException("Car with plate " + registrationPlate + " does not exist");
        }
        return car;
    }

    @Override
    public boolean setCar(User user, Car car) throws Exception {
        List<Car> existingCars = carRepository.findByUser(user);
        for (Car existingCar : existingCars) {
            if (existingCar.getCarId() == car.getCarId()) {
                throw new CarExistsException("Car already exists for this user.");
            }
        }

        car.setUser(user);
        carRepository.save(car);
        return true;
    }

    @Override
    public boolean updateCar(Car car) throws Exception {
        Optional<Car> carOptional = carRepository.findById(car.getCarId());
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
        Optional<Car> carOptional = carRepository.findById(car.getCarId());
        if (carOptional.isPresent()) {
            carRepository.delete(carOptional.get());
            return true;
        }
        return false;
    }
}
