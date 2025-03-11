package hr.java.models;

import java.math.BigDecimal;

public sealed interface Fuel permits FuelCar {



  public BigDecimal calculateTotalFuelExpense(long expense);



}
