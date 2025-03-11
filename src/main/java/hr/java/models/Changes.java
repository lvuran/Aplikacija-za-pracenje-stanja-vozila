package hr.java.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;



public class Changes <T> implements Serializable {

    private final String car;
    private final String change;

    private final T oldValue;
    private final T newValue;

    private final String user;
    String time;

    public Changes(String car, String change, T oldValue, T newValue, String user) {
        this.car = car;
        this.change = change;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.user = user;
        this.time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+ "  " + LocalDate.now().format(DateTimeFormatter.ofPattern(("dd:MM:yyyy")));
    }

    public String getChange() {
        return change;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    public String getUser() {
        return user;
    }

    public String getTime() {
        return time;
    }


    public String getCar() {
    return car;
    }
}
