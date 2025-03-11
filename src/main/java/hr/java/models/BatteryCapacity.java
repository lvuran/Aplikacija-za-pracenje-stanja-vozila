package hr.java.models;

import java.math.BigDecimal;

public record BatteryCapacity(BigDecimal capacity) {
    public BatteryCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }
}
