package hr.java.application;

import hr.java.enums.CarCategory;
import hr.java.enums.CarType;
import hr.java.exeptions.RegistrationNumberDuplicateExeption;
import hr.java.models.*;
import hr.java.threads.SerializeThread;
import hr.java.utilities.DataBaseUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static hr.java.application.LoginScreen.logger;

public class NewCarScreenController {

    @FXML
    TextField manufacturerTextField;
    @FXML
    TextField modelTextField;
    @FXML
    TextField registrationNumberTextField;
    @FXML
    ComboBox<CarType> carTypeComboBox;
    @FXML
    ComboBox<CarCategory> vehicleCategoryComboBox;
    @FXML
    DatePicker manufacturedDatePicker;
    @FXML
    TextField colorTextField;
    @FXML
    TextField capacityTextField;




    public void initialize()
    {
        carTypeComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(CarType.values())));
        vehicleCategoryComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(CarCategory.values())));
    }

public void saveCar() throws SQLException, IOException {
    if(manufacturerTextField.getText().isEmpty()||modelTextField.getText().isEmpty()|| registrationNumberTextField.getText().isEmpty()
            || colorTextField.getText().isEmpty()||capacityTextField.getText().isEmpty()||carTypeComboBox.getValue() == null
            || vehicleCategoryComboBox.getValue() == null|| manufacturedDatePicker.getValue() == null)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upis nije uspio");
        alert.setHeaderText("Unesite sve trazene vrijednosti");
        alert.setContentText("Nisu unesene sve potrebne vrijednosti, unos prekinut");
        alert.showAndWait();
        return;
    }

    try {
        Integer.parseInt(capacityTextField.getText());
    }
    catch (NumberFormatException e) {
        logger.error(String.valueOf(e));
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upis nije uspio");
        alert.setHeaderText("Unesite ispravne vrijednosti");
        alert.setContentText("Nisu unesene ispravne vrijednosti, unos prekinut");
        alert.showAndWait();
        capacityTextField.clear();
        return;
    }

    List<ElectricCar> electricCars = DataBaseUtilities.getAllElectricCarsFromDatabase();
    List<FuelCar> fuelCars = DataBaseUtilities.getAllFuelCarsFromDatabase();
    try{

        Optional electricreg = electricCars.stream().filter(c -> c.getRegistrationNumber().equals(registrationNumberTextField.getText())).findAny();
        Optional fuelreg = fuelCars.stream().filter(c -> c.getRegistrationNumber().equals(registrationNumberTextField.getText())).findAny();
        if(electricreg.isPresent() || fuelreg.isPresent())
        {
            throw new RegistrationNumberDuplicateExeption("Već postoji vozilo unesene registarske oznake.");
        }
    } catch (RegistrationNumberDuplicateExeption e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Upis nije uspio");
        alert.setHeaderText("Vec postoji vozilo te registracije");
        alert.setContentText("Nisu unesene ispravne vrijednosti, unos prekinut");
        alert.showAndWait();
        registrationNumberTextField.clear();
        return;
    }
    if (manufacturedDatePicker.getValue().isAfter(LocalDate.now())) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pogresan datum");
        alert.setHeaderText("Molimo unesite datum koji je prosao");

        manufacturedDatePicker.setValue(null);

        alert.showAndWait();
        return;

    }


    if(carTypeComboBox.getValue() == CarType.GORIVO)
    {
        DataBaseUtilities.saveNewFuelCarToDatabase(vehicleCategoryComboBox.getValue().getId(), carTypeComboBox.getValue().getId(), manufacturerTextField.getText(), modelTextField.getText(), registrationNumberTextField.getText(), colorTextField.getText(), manufacturedDatePicker.getValue(), BigDecimal.valueOf(Long.parseLong(capacityTextField.getText())) );
        DataBaseUtilities.saveUserFuelCar();


    }
    if(carTypeComboBox.getValue() == CarType.BATERIJA)
    {
        DataBaseUtilities.saveNewElectricCarToDatabase(vehicleCategoryComboBox.getValue().getId(), carTypeComboBox.getValue().getId(), manufacturerTextField.getText(), modelTextField.getText(), registrationNumberTextField.getText(), colorTextField.getText(), manufacturedDatePicker.getValue(), BigDecimal.valueOf(Long.parseLong(capacityTextField.getText())) );
        DataBaseUtilities.saveUserElectricCar();
    }
    String carName = manufacturerTextField.getText() +" "+ modelTextField.getText();
    String change = "Vozilo";
    String oldValue = "";

    String newValue = "Uneseno";
    String user = LoginController.getUsername();
    Changes changed = new Changes<>(carName, change, oldValue, newValue,user);

    SerializeThread serialize = new SerializeThread(changed);
    Thread startSerialization = new Thread(serialize);
    startSerialization.start();
    try {
        startSerialization.join();
    } catch (InterruptedException ex) {
        logger.error(String.valueOf(ex));
        throw new RuntimeException(ex);
    }



    FXMLLoader fxmlLoader = new FXMLLoader(UserScreenController.class.getResource("userScreen.fxml"));
    try{
        Scene scene = new Scene(fxmlLoader.load(), 610,500);
        LoginScreen.getMainStage().setTitle(LoginController.getUsername());
        LoginScreen.getMainStage().setScene(scene);
        LoginScreen.getMainStage().show();
    }catch (IOException e) {
        logger.error(String.valueOf(e));
        throw new RuntimeException(e);
    }





}

public void cancel()
{
    FXMLLoader fxmlLoader = new FXMLLoader(UserScreenController.class.getResource("userScreen.fxml"));

    try{
        Scene scene = new Scene(fxmlLoader.load(), 610,500);
        LoginScreen.getMainStage().setTitle(LoginController.getUsername());
        LoginScreen.getMainStage().setScene(scene);
        LoginScreen.getMainStage().show();
    }catch (IOException e) {
        logger.error(String.valueOf(e));
        throw new RuntimeException(e);
    }
}


}
