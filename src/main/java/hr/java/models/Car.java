package hr.java.models;

import hr.java.enums.CarCategory;
import hr.java.enums.CarType;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public abstract class Car {

    private Long id;
    private String manufacturer;
    private String model;
    private String registrationNumber;
    private LocalDate manufacturingDate;
    private String color;
    private CarType carType;
    private CarCategory vehicleCategory;


    private Date lastMaintenanceDate;
    private Date lastTireCheckDate;
    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = Date.from(lastMaintenanceDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void setLastTireCheckDate(LocalDate lastTireCheckDateDate) {
        this.lastTireCheckDate = Date.from(lastTireCheckDateDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Car(Long id, String manufacturer, String model, String registrationNumber, LocalDate manufacturingDate, String color, CarType carType, CarCategory vehicleCategory) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.manufacturingDate = manufacturingDate;
        this.color = color;
        this.carType = carType;
        this.vehicleCategory = vehicleCategory;
    }

    public Car(Long id, String manufacturer, String model, String registrationNumber, LocalDate manufacturingDate, String color, CarType carType, CarCategory vehicleCategory, Date lastMaintenanceDate, Date lastTireCheckDate, String notes) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.manufacturingDate = manufacturingDate;
        this.color = color;
        this.carType = carType;
        this.vehicleCategory = vehicleCategory;
        this.lastMaintenanceDate = lastMaintenanceDate;
        this.lastTireCheckDate = lastTireCheckDate;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public String getColor() {
        return color;
    }

    public CarType getCarType() {
        return carType;
    }

    public CarCategory getVehicleCategory() {
        return vehicleCategory;
    }

    public LocalDate getLastMaintenanceDate() {

        if(lastMaintenanceDate!=null){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(lastMaintenanceDate);
        LocalDate localDate = LocalDate.parse(dateString);
        return localDate;}
        return null;
    }


    public LocalDate getLastTireCheckDate() {
        if(lastTireCheckDate!=null){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(lastTireCheckDate);
        LocalDate localDate = LocalDate.parse(dateString);

            return localDate;}
        return null;
    }


}
