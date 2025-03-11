package hr.java.models;

import hr.java.models.Car;

import java.util.ArrayList;
import java.util.List;

public class User <T extends Electric, U extends FuelCar>{
    Long id;
    String Name;
    List<T> electricCarsList = new ArrayList<>();

    List<U> fuelCarsList = new ArrayList<>();

    public User(Long id, String name) {
        this.id = id;
        Name = name;
    }

    public void setElectricCarsList(List<T> electricCarsList) {
        this.electricCarsList = electricCarsList;
    }

    public void setFuelCarsList(List<U> fuelCarsList) {
        this.fuelCarsList = fuelCarsList;
    }

    public String getName() {
        return Name;
    }

    public Long getId() {
        return id;
    }

    public List<T> getElectricCarsList() {
        return electricCarsList;
    }

    public List<U> getFuelCarsList() {
        return fuelCarsList;
    }


}
