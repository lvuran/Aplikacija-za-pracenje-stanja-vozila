package hr.java.models;

import java.math.BigDecimal;

public record TankCapacity(BigDecimal capacity) {


    public TankCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

}
