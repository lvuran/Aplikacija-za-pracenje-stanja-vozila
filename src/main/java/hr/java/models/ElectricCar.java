package hr.java.models;

import hr.java.enums.CarCategory;
import hr.java.enums.CarType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class ElectricCar extends Car implements Electric{

    BatteryCapacity batteryCapacity;

    BigDecimal batteryPercentage;
    BigDecimal remainingKilometers = BigDecimal.valueOf(0);

    public ElectricCar(Long id, String manufacturer, String model, String registrationNumber, LocalDate manufacturingDate, String color, CarType carType, CarCategory vehicleCategory, Date lastMaintenanceDate, Date lastTireCheckDate, String notes, BigDecimal batteryCapacity, BigDecimal remainingKilometers) {
        super(id, manufacturer, model, registrationNumber, manufacturingDate, color, carType, vehicleCategory, lastMaintenanceDate, lastTireCheckDate, notes);
        this.batteryCapacity = new BatteryCapacity(batteryCapacity);
        if(remainingKilometers!=null)
        this.remainingKilometers = remainingKilometers;
    }

    @Override
    public BigDecimal calculateRemaingingRange() {
BigDecimal kilometers1 = batteryCapacity.capacity().multiply(batteryPercentage.divide(BigDecimal.valueOf(100)));
    kilometers1 = kilometers1.multiply(BigDecimal.valueOf(10));
    kilometers1 = kilometers1.multiply(BigDecimal.valueOf(0.77));
    remainingKilometers = kilometers1;
        return kilometers1;
    }

    public BigDecimal getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(BigDecimal batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public BatteryCapacity getBatteryCapacity() {
        return batteryCapacity;
    }

    public BigDecimal getRemainingKilometers() {
        return remainingKilometers;
    }
}
