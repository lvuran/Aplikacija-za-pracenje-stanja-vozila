package hr.java.models;

import hr.java.enums.CarCategory;
import hr.java.enums.CarType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public non-sealed class FuelCar extends Car implements Fuel{

    TankCapacity tankCapacity;

    BigDecimal fuelExpense = BigDecimal.valueOf(0);



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuelCar fuelCar = (FuelCar) o;
        return Objects.equals(tankCapacity, fuelCar.tankCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tankCapacity);
    }

    public FuelCar(Long id, String manufacturer, String model, String registrationNumber, LocalDate manufacturingDate, String color, CarType carType, CarCategory vehicleCategory, Date lastMaintenanceDate, Date lastTireCheckDate, String notes, BigDecimal tankCapacity, BigDecimal fuelExpense) {
        super(id, manufacturer, model, registrationNumber, manufacturingDate, color, carType, vehicleCategory, lastMaintenanceDate, lastTireCheckDate, notes);
        this.tankCapacity =  new TankCapacity(tankCapacity);
        if(fuelExpense!= null)
        this.fuelExpense = fuelExpense;
    }

    @Override
    public BigDecimal calculateTotalFuelExpense(long expense) {
        fuelExpense = fuelExpense.add(BigDecimal.valueOf( expense * 1.5));
        return  fuelExpense;
    }

    public TankCapacity getTankCapacity() {
        return tankCapacity;
    }

    public BigDecimal getFuelExpense() {
        return fuelExpense;
    }
}
