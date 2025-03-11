package hr.java.utilities;

import hr.java.application.LoginController;
import hr.java.enums.CarCategory;
import hr.java.enums.CarType;
import hr.java.models.*;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static hr.java.application.LoginScreen.logger;

public class DataBaseUtilities {

    private static final String DATABASE_FILE = "conf/database.properties";
    public static Connection connectToDatabase() throws SQLException, IOException {

        Properties svojstva = new Properties();
        svojstva.load(new FileReader(DATABASE_FILE));
        String urlBazePodataka = svojstva.getProperty("databaseUrl");
        String korisnickoIme = svojstva.getProperty("username");
        String lozinka = svojstva.getProperty("password");
        Connection veza = DriverManager.getConnection(urlBazePodataka, korisnickoIme, lozinka);
        return veza;
    }

    static void disconnectFromDatabase(Connection veza, Statement statement, ResultSet resultSet) throws SQLException {
        try {
            resultSet.close();
            statement.close();
            veza.close();
        } catch (SQLException ex) {
            logger.error(String.valueOf(ex));
            ex.printStackTrace();
        }
    }

    static void disconnectFromDatabase(Connection veza, Statement statement) throws SQLException {
        try {
            statement.close();
            veza.close();
        } catch (SQLException ex) {
            logger.error(String.valueOf(ex));
            ex.printStackTrace();
        }
    }


    //GET ENTRIES

    public static List<User> getAllUsersFromDatabase()
            throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        List<User> userList = new ArrayList<>();
        Statement sqlStatement = connection.createStatement();
        ResultSet userResultSet = sqlStatement.executeQuery("SELECT * FROM REGISTERED_USER");
        while(userResultSet.next()) {
            User newUser = getUserFromResultSet(userResultSet);
            userList.add(newUser);
        }

        disconnectFromDatabase(connection, sqlStatement,userResultSet);
        return userList;
    }
    private static User getUserFromResultSet(ResultSet eCarResultSet)
            throws SQLException, IOException {
        Long userId = eCarResultSet.getLong("ID");
        String userName = eCarResultSet.getString("NAME");
        return new UserBuilder().setId(userId).setName(userName).createUser();

    }

   public static List<ElectricCar> getAllElectricCarsFromDatabase()
           throws SQLException, IOException
   {
       Connection connection = connectToDatabase();
       List<ElectricCar> eCarList = new ArrayList<>();
       Statement sqlStatement = connection.createStatement();
       ResultSet eCarResultSet = sqlStatement.executeQuery("SELECT * FROM ELECTRIC");
       while(eCarResultSet.next()) {
           ElectricCar newECar = getElectricCarFromResultSet(eCarResultSet);
           eCarList.add(newECar);
       }

       disconnectFromDatabase(connection, sqlStatement,eCarResultSet);
       return eCarList;
   }
    private static ElectricCar getElectricCarFromResultSet(ResultSet eCarResultSet)
            throws SQLException, IOException {
        Long carId = eCarResultSet.getLong("ID");
        Long categoryId = eCarResultSet.getLong("CATEGORY_ID");
        Long typeId = eCarResultSet.getLong("TYPE_ID");
        String manufacturer = eCarResultSet.getString("MANUFACTURER");
        String model = eCarResultSet.getString("MODEL");
        String registration = eCarResultSet.getString("REGISTRATION");
        String color = eCarResultSet.getString("COLOR");
        LocalDate manufacturingDate = eCarResultSet.getDate("MANUFACTURING_DATE").toLocalDate();
        BigDecimal batteryCapacity = eCarResultSet.getBigDecimal("BATTERY_CAPACITY");
        Date maintenanceDate = eCarResultSet.getDate("MAINTENANCE_DATE");
        Date tiresDate = eCarResultSet.getDate("TIRES_DATE");
        BigDecimal remainingRange = eCarResultSet.getBigDecimal("REMAINING_RANGE");
        String notes = eCarResultSet.getString("NOTES");
        List<CarCategory> categoryList = List.of(CarCategory.values());
        CarCategory category = categoryList.stream().filter(c ->c.getId() == categoryId).findFirst().get();
        List<CarType> typeList = List.of(CarType.values());
        CarType type = typeList.stream().filter(t ->t.getId() == typeId).findFirst().get();
        return new ElectricCar(carId,manufacturer,model,registration,manufacturingDate,color,type,category,maintenanceDate,tiresDate,notes,batteryCapacity,remainingRange);
    }



    public static List<ElectricCar> getAllElectricCarsForuser(Long id)
            throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        Statement sqlStatement = connection.createStatement();
        ResultSet eCarResultSet = sqlStatement.executeQuery("SELECT * FROM USER_ELECTRIC SI, ELECTRIC I WHERE SI.USER_ID = " + String.valueOf(id) + "  AND SI.CAR_ID = I.ID");
        List<ElectricCar> electricCars = new ArrayList<>();
        while(eCarResultSet.next()) {
            electricCars.add(getElectricCarForUser(eCarResultSet));
        }

        disconnectFromDatabase(connection, sqlStatement,eCarResultSet);
        return electricCars;
    }
    private static ElectricCar getElectricCarForUser(ResultSet eCarResultSet)
            throws SQLException, IOException {

        Long eCarId = eCarResultSet.getLong("CAR_ID");
        List<ElectricCar> eCarList = getAllElectricCarsFromDatabase();
        ElectricCar eCar = eCarList.stream().filter(car -> car.getId().equals(eCarId)).findFirst().get();

        return eCar;
    }


    public static List<FuelCar> getAllFuelCarsFromDatabase()
            throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        List<FuelCar> fCarList = new ArrayList<>();
        Statement sqlStatement = connection.createStatement();
        ResultSet fCarResultSet = sqlStatement.executeQuery("SELECT * FROM FUEL");
        while(fCarResultSet.next()) {
            FuelCar newFCar = getFuelCarFromResultSet(fCarResultSet);
            fCarList.add(newFCar);
        }

        disconnectFromDatabase(connection, sqlStatement,fCarResultSet);
        return fCarList;
    }
    private static FuelCar getFuelCarFromResultSet(ResultSet fCarResultSet)
            throws SQLException, IOException {
        Long carId = fCarResultSet.getLong("ID");
        Long categoryId = fCarResultSet.getLong("CATEGORY_ID");
        Long typeId = fCarResultSet.getLong("TYPE_ID");
        String manufacturer = fCarResultSet.getString("MANUFACTURER");
        String model = fCarResultSet.getString("MODEL");
        String registration = fCarResultSet.getString("REGISTRATION");
        String color = fCarResultSet.getString("COLOR");
        LocalDate manufacturingDate = fCarResultSet.getDate("MANUFACTURING_DATE").toLocalDate();
        BigDecimal tankCapacity = fCarResultSet.getBigDecimal("TANK_CAPACITY");
        Date maintenanceDate = fCarResultSet.getDate("MAINTENANCE_DATE");
        Date tiresDate = fCarResultSet.getDate("TIRES_DATE");
        BigDecimal remainingRange = fCarResultSet.getBigDecimal("FUEL_EXPENSE");
        String notes = fCarResultSet.getString("NOTES");
        List<CarCategory> categoryList = List.of(CarCategory.values());
        CarCategory category = categoryList.stream().filter(c ->c.getId() == categoryId).findFirst().get();
        List<CarType> typeList = List.of(CarType.values());
        CarType type = typeList.stream().filter(t ->t.getId() == typeId).findFirst().get();
        return new FuelCar(carId,manufacturer,model, registration, manufacturingDate, color, type,category, maintenanceDate, tiresDate,notes, tankCapacity,remainingRange);

    }

    public static List<FuelCar> getAllFuelCarsForuser(Long id)
            throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        Statement sqlStatement = connection.createStatement();

        ResultSet fCarResultSet = sqlStatement.executeQuery("SELECT * FROM USER_FUEL SI, FUEL I WHERE SI.USER_ID = " + String.valueOf(id) + "  AND SI.CAR_ID = I.ID");
        List<FuelCar> fuelCars = new ArrayList<>();
        while(fCarResultSet.next()) {
            fuelCars.add(getFuelCarForUser(fCarResultSet));
        }

        disconnectFromDatabase(connection, sqlStatement,fCarResultSet);
        return fuelCars;
    }
    private static FuelCar getFuelCarForUser(ResultSet fCarResultSet)
            throws SQLException, IOException {

        Long fCarId = fCarResultSet.getLong("CAR_ID");
        List<FuelCar> fCarList = getAllFuelCarsFromDatabase();
        FuelCar fCar = fCarList.stream().filter(car -> car.getId().equals(fCarId)).findFirst().get();

        return fCar;
    }
    //NEW ENTRIES

    public static void saveNewUserToDatabase(String username) throws SQLException, IOException {
        Connection connection = connectToDatabase();
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO REGISTERED_USER (NAME) VALUES(?)");
        stmt.setString(1, username);
        stmt.executeUpdate();
        disconnectFromDatabase(connection, stmt);
    }
    public static void saveNewElectricCarToDatabase(Long category, Long type, String manufacturer, String model, String registration, String color, LocalDate manufacturingDate, BigDecimal batteryCapacity) throws SQLException, IOException {
        Connection connection = connectToDatabase();
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO ELECTRIC (CATEGORY_ID, TYPE_ID, MANUFACTURER, MODEL, REGISTRATION, COLOR, MANUFACTURING_DATE, BATTERY_CAPACITY) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        stmt.setString(1, String.valueOf(category));
        stmt.setString(2, String.valueOf(type));
        stmt.setString(3, manufacturer);
        stmt.setString(4, model);
        stmt.setString(5, registration);
        stmt.setString(6, color);
        stmt.setString(7, String.valueOf(manufacturingDate));
        stmt.setString(8, String.valueOf(batteryCapacity));
        stmt.executeUpdate();
        disconnectFromDatabase(connection, stmt);
    }
    public static void saveNewFuelCarToDatabase(Long category, Long type, String manufacturer, String model, String registration, String color, LocalDate manufacturingDate, BigDecimal tankCapacity) throws SQLException, IOException {
        Connection connection = connectToDatabase();
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO FUEL (CATEGORY_ID, TYPE_ID, MANUFACTURER, MODEL, REGISTRATION, COLOR, MANUFACTURING_DATE, TANK_CAPACITY) VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
        stmt.setString(1, String.valueOf(category));
        stmt.setString(2, String.valueOf(type));
        stmt.setString(3, manufacturer);
        stmt.setString(4, model);
        stmt.setString(5, registration);
        stmt.setString(6, color);
        stmt.setString(7, String.valueOf(manufacturingDate));
        stmt.setString(8, String.valueOf(tankCapacity));
        stmt.executeUpdate();
        disconnectFromDatabase(connection, stmt);
    }

    public static void saveUserElectricCar() throws SQLException, IOException {

        Connection connection = connectToDatabase();
        List<User> users = getAllUsersFromDatabase();
        User user = users.stream().filter(user1 -> user1.getName().equals(LoginController.getUsername())).findFirst().get();
        Long idUser = user.getId();
        List<ElectricCar> eCars = getAllElectricCarsFromDatabase();
        ElectricCar car = eCars.getLast();
        Long idCar = car.getId();
        PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO USER_ELECTRIC (USER_ID, CAR_ID) VALUES(?, ?)");
            stmt.setString(1, String.valueOf(idUser));
            stmt.setString(2, String.valueOf(idCar));
            stmt.executeUpdate();
        disconnectFromDatabase(connection, stmt);
    }
    public static void saveUserFuelCar() throws SQLException, IOException {

        Connection connection = connectToDatabase();
        List<User> users = getAllUsersFromDatabase();
        User user = users.stream().filter(user1 -> user1.getName().equals(LoginController.getUsername())).findFirst().get();
        Long idUser = user.getId();
        List<FuelCar> fCars = getAllFuelCarsFromDatabase();
        FuelCar car = fCars.getLast();
        Long idCar = car.getId();
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO USER_FUEL (USER_ID, CAR_ID) VALUES(?, ?)");
        stmt.setString(1, String.valueOf(idUser));
        stmt.setString(2, String.valueOf(idCar));
        stmt.executeUpdate();
        disconnectFromDatabase(connection, stmt);
    }


    //DELETE ENTRIES



    public static void deleteFuelCar(FuelCar car) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        deleteUserFuelCar(car);
        PreparedStatement deleteCar = connection.prepareStatement("DELETE FROM FUEL WHERE ID = ?");
        deleteCar.setString(1, String.valueOf( car.getId()));
        deleteCar.executeUpdate();
        connection.close();
    }

    public static void deleteUserFuelCar (FuelCar car) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement deleteUserCar = connection.prepareStatement("DELETE FROM USER_FUEL WHERE CAR_ID = ?");
        deleteUserCar.setString(1, String.valueOf( car.getId()));
        deleteUserCar.executeUpdate();
        connection.close();
    }

    public static void deleteElectricCar(ElectricCar car) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        deleteUserElectricCar(car);
        PreparedStatement deleteCar = connection.prepareStatement("DELETE FROM ELECTRIC WHERE ID = ?");
        deleteCar.setString(1, String.valueOf( car.getId()));
        deleteCar.executeUpdate();
        connection.close();
    }

    public static void deleteUserElectricCar (ElectricCar car) throws SQLException, IOException
    {
        Connection connection = connectToDatabase();
        PreparedStatement deleteUserCar = connection.prepareStatement("DELETE FROM USER_ELECTRIC WHERE CAR_ID = ?");
        deleteUserCar.setString(1, String.valueOf( car.getId()));
        deleteUserCar.executeUpdate();
        connection.close();
    }



    public static void deleteUser (User user) throws SQLException, IOException {

        List<ElectricCar> electricCars = getAllElectricCarsForuser(user.getId());
        for(ElectricCar car : electricCars)
        {
            deleteElectricCar(car);
        }
        List<FuelCar> felCars = getAllFuelCarsForuser(user.getId());
        for(FuelCar car : felCars)
        {
            deleteFuelCar(car);
        }
        Connection connection = connectToDatabase();
        PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM REGISTERED_USER WHERE ID = ?");
        deleteUser.setString(1, String.valueOf( user.getId()));
        deleteUser.executeUpdate();
        connection.close();
    }




    //UPDATE ENTRIES


    public static void updateElectricCarMaintenance(ElectricCar car, LocalDate date)
            throws SQLException, IOException {
        Connection connection = connectToDatabase();

        PreparedStatement updateMaintenance = connection.prepareStatement("UPDATE ELECTRIC SET MAINTENANCE_DATE = '" + date +  "' WHERE ID = ?");
        updateMaintenance.setString(1, car.getId().toString());
        updateMaintenance.executeUpdate();
        connection.close();
    }
    public static void updateElectricCarTires(ElectricCar car, LocalDate date)
            throws SQLException, IOException {
        Connection connection = connectToDatabase();

        PreparedStatement updateMaintenance = connection.prepareStatement("UPDATE ELECTRIC SET TIRES_DATE = '" + date +  "' WHERE ID = ?");
        updateMaintenance.setString(1, car.getId().toString());
        updateMaintenance.executeUpdate();
        connection.close();
    }


    public static void updateElectricCarRange(ElectricCar car)
            throws SQLException, IOException {
        Connection connection = connectToDatabase();

        PreparedStatement updateMaintenance = connection.prepareStatement("UPDATE ELECTRIC SET REMAINING_RANGE = '" + car.getRemainingKilometers() +  "' WHERE ID = ?");
        updateMaintenance.setString(1, car.getId().toString());
        updateMaintenance.executeUpdate();
        connection.close();
    }

    public static void updateElectricCarNotes(ElectricCar car)
            throws SQLException, IOException {
        Connection connection = connectToDatabase();

        PreparedStatement updateMaintenance = connection.prepareStatement("UPDATE ELECTRIC SET NOTES = '" + car.getNotes() +  "' WHERE ID = ?");
        updateMaintenance.setString(1, car.getId().toString());
        updateMaintenance.executeUpdate();
        connection.close();
    }

    public static void updateFuelCarMaintenance(FuelCar car, LocalDate date)
            throws SQLException, IOException {
        Connection connection = connectToDatabase();
        PreparedStatement updateMaintenance = connection.prepareStatement("UPDATE FUEL SET MAINTENANCE_DATE = '" + date +  "' WHERE ID = ?");
        updateMaintenance.setString(1, car.getId().toString());
        updateMaintenance.executeUpdate();
        connection.close();
    }
    public static void updateFuelCarTires(FuelCar car, LocalDate date)
            throws SQLException, IOException {
        Connection connection = connectToDatabase();
        PreparedStatement updateMaintenance = connection.prepareStatement("UPDATE FUEL SET TIRES_DATE = '" + date +  "' WHERE ID = ?");
        updateMaintenance.setString(1, car.getId().toString());
        updateMaintenance.executeUpdate();
        connection.close();
    }
    public static void updateFuelCarExpense(FuelCar car)
            throws SQLException, IOException {
        Connection connection = connectToDatabase();
        PreparedStatement updateMaintenance = connection.prepareStatement("UPDATE FUEL SET FUEL_EXPENSE = '" + car.getFuelExpense() +  "' WHERE ID = ?");
        updateMaintenance.setString(1, car.getId().toString());
        updateMaintenance.executeUpdate();
        connection.close();
    }
    public static void updateFuelCarNotes(FuelCar car)
            throws SQLException, IOException {
        Connection connection = connectToDatabase();

        PreparedStatement updateMaintenance = connection.prepareStatement("UPDATE FUEL SET NOTES = '" + car.getNotes() +  "' WHERE ID = ?");
        updateMaintenance.setString(1, car.getId().toString());
        updateMaintenance.executeUpdate();
        connection.close();
    }
}
