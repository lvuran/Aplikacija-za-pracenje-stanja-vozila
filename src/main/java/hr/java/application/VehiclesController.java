package hr.java.application;

import hr.java.models.Changes;
import hr.java.models.ElectricCar;
import hr.java.models.FuelCar;
import hr.java.models.User;
import hr.java.threads.SerializeThread;
import hr.java.utilities.DataBaseUtilities;
import hr.java.utilities.FilesUtilities;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static hr.java.application.LoginScreen.logger;


public class VehiclesController {

    @FXML
    private TextField SearchTextField;
    @FXML
    private TableView<ElectricCar> electricCarTableView;
    @FXML
    private TableColumn<ElectricCar, String> ecarManufacturerTableColumn;
    @FXML
    private TableColumn<ElectricCar, String> ecarModelTableColumn;
    @FXML
    private TableColumn<ElectricCar, String> ecarRegistrationTableColumn;
    @FXML
    private TableView<FuelCar> fuelCarTableView;
    @FXML
    private TableColumn<FuelCar, String> fcarManufacturerTableColumn;
    @FXML
    private TableColumn<FuelCar, String> fcarModelTableColumn;
    @FXML
    private TableColumn<FuelCar, String> fcarRegistrationTableColumn;


    List<ElectricCar> ecars ;
    List<FuelCar> fcars;

    private static User selecteduser =null;

    public static void setSelecteduser(User selecteduser) {
        VehiclesController.selecteduser = selecteduser;
    }

    public void initialize() {

        ecarManufacturerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ElectricCar, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ElectricCar, String> carStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(carStringCellDataFeatures.getValue().getManufacturer());
            }
        });
        ecarModelTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ElectricCar, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ElectricCar, String> carStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(carStringCellDataFeatures.getValue().getModel());
            }
        });
        ecarRegistrationTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ElectricCar, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ElectricCar, String> carStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(carStringCellDataFeatures.getValue().getRegistrationNumber());
            }
        });

        fcarManufacturerTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FuelCar, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<FuelCar, String> carStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(carStringCellDataFeatures.getValue().getManufacturer());
            }
        });
        fcarModelTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FuelCar, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<FuelCar, String> carStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(carStringCellDataFeatures.getValue().getModel());
            }
        });
        fcarRegistrationTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FuelCar, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<FuelCar, String> carStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(carStringCellDataFeatures.getValue().getRegistrationNumber());
            }
        });
        try {
            carSearch();
        } catch (SQLException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
        if(UsersController.getVehiclesStage()!=null)
        {
        UsersController.vehiclesStage.setOnCloseRequest(windowEvent -> {
            selecteduser = null;
                }

        );}
    }


    public void delete() throws SQLException, IOException {

        FuelCar fcar =fuelCarTableView.getSelectionModel().getSelectedItem();
        ElectricCar ecar = electricCarTableView.getSelectionModel().getSelectedItem();

        if( ecar== null) {
            if( fcar == null)
                return;
        }
        if( ecar != null) {
            if( fcar != null)
            {Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Greska");
                alert.setHeaderText("Odabran prevelik broj automobila");
                alert.setContentText("Molimo odaberite samo jedan automobil");
                alert.showAndWait();
                electricCarTableView.getSelectionModel().clearSelection();
                fuelCarTableView.getSelectionModel().clearSelection();
                return;}

            //
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Unesite lozinku za admina");
            td.showAndWait();
            HashMap<String, String> userMap = FilesUtilities.getUsersFromFile();
            String passwordForKey = userMap.get("admin");
            if(td.getResult()==null)
                return;
            if (td.getResult().isEmpty() || td.getResult()==null)
                return;
            if(!passwordForKey.equals(FilesUtilities.passwords(td.getEditor().getText())))
                return;

            //

            Alert ealert = new Alert(Alert.AlertType.CONFIRMATION);
            ealert.setTitle("Uklanjanje automobila");
            ealert.setHeaderText(ecar.getManufacturer() + " " + ecar.getModel() + " (" + ecar.getRegistrationNumber() + ")");
            ealert.setContentText("Obrisati odabrani automobil?");
            ButtonType ebuttonYes = new ButtonType("Da");
            ButtonType ebuttonNo = new ButtonType("Ne");
            ealert.getButtonTypes().setAll(ebuttonYes, ebuttonNo);
            ElectricCar efinalCar = ecar;
            ealert.setOnCloseRequest(e -> {
                ButtonType result = ealert.getResult();
                if (result != null && result == ebuttonYes) {
                    try {
                        DataBaseUtilities.deleteElectricCar(efinalCar);
                    } catch (IOException | SQLException e1) {
                        logger.error(String.valueOf(e1));
                        Alert egreska = new Alert(Alert.AlertType.ERROR);
                        egreska.setTitle("Greska");
                        egreska.setHeaderText("Neuspjelo uklanjanje");
                        egreska.setContentText("Dogodila se greska pri pokusaju brisanja, brisanje prekinuto");
                        egreska.showAndWait();
                        return;
                    }

                    String deleted = ecar.getManufacturer() + " " + ecar.getModel();
                    String change = "Vozilo";
                    String oldValue = "Uneseno";

                    String newValue = "Obrisano";
                    String user = LoginController.getUsername();
                    Changes changed = new Changes<>(deleted, change, oldValue, newValue,user);

                    SerializeThread serialize = new SerializeThread(changed);
                    Thread startSerialization = new Thread(serialize);
                    startSerialization.start();
                    try {
                        startSerialization.join();
                    } catch (InterruptedException ex) {
                        logger.error(String.valueOf(ex));
                        throw new RuntimeException(ex);
                    }

                    Alert euspjeh = new Alert(Alert.AlertType.INFORMATION);
                    euspjeh.setTitle("Uspjeh");
                    euspjeh.setHeaderText("Automobil uspjesno uklonjen");
                    euspjeh.showAndWait();
                    try {
                        carSearch();
                    } catch (SQLException | IOException ex) {
                        logger.error(String.valueOf(ex));
                        throw new RuntimeException(ex);}
                    return;
                }
                if (result != null && result == ebuttonNo) {
                    return;
                }
            });
            ealert.show();
        }

        if( fcar != null) {


            //
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Unesite lozinku za admina");
            td.showAndWait();
            HashMap<String, String> userMap = FilesUtilities.getUsersFromFile();
            String passwordForKey = userMap.get("admin");
            if(td.getResult()==null)
                return;
            if (td.getResult().isEmpty() || td.getResult()==null)
                return;
            if(!passwordForKey.equals(FilesUtilities.passwords(td.getEditor().getText())))
                return;

            //

            Alert falert = new Alert(Alert.AlertType.CONFIRMATION);
            falert.setTitle("Uklanjanje automobila");
            falert.setHeaderText(fcar.getManufacturer() + " " + fcar.getModel() + " (" + fcar.getRegistrationNumber() + ")");
            falert.setContentText("Obrisati odabrani automobil?");
            ButtonType fbuttonYes = new ButtonType("Da");
            ButtonType fbuttonNo = new ButtonType("Ne");
            falert.getButtonTypes().setAll(fbuttonYes, fbuttonNo);
            FuelCar ffinalCar = fcar;
            falert.setOnCloseRequest(e -> {
                ButtonType result = falert.getResult();
                if (result != null && result == fbuttonYes) {
                    try {
                        DataBaseUtilities.deleteFuelCar(ffinalCar);
                    } catch (IOException | SQLException e1) {
                        logger.error(String.valueOf(e1));
                        Alert fgreska = new Alert(Alert.AlertType.ERROR);
                        fgreska.setTitle("Greska");
                        fgreska.setHeaderText("Neuspjelo uklanjanje");
                        fgreska.setContentText("Dogodila se greska pri pokusaju brisanja, brisanje prekinuto");
                        fgreska.showAndWait();
                        return;
                    }

                    String deleted = fcar.getManufacturer() + " " + fcar.getModel();
                    String change = "Vozilo";
                    String oldValue = "Uneseno";

                    String newValue = "Obrisano";
                    String user = LoginController.getUsername();
                    Changes changed = new Changes<>(deleted, change, oldValue, newValue,user);

                    SerializeThread serialize = new SerializeThread(changed);
                    Thread startSerialization = new Thread(serialize);
                    startSerialization.start();
                    try {
                        startSerialization.join();
                    } catch (InterruptedException ex) {
                        logger.error(String.valueOf(ex));
                        throw new RuntimeException(ex);
                    }


                    Alert fuspjeh = new Alert(Alert.AlertType.INFORMATION);
                    fuspjeh.setTitle("Uspjeh");
                    fuspjeh.setHeaderText("Automobil uspjesno uklonjen");
                    fuspjeh.showAndWait();
                    try {
                        carSearch();
                    } catch (SQLException | IOException ex) {
                        logger.error(String.valueOf(ex));
                        throw new RuntimeException(ex);}
                    return;
                }
                if (result != null && result == fbuttonNo) {
                    return;
                }
            });
            falert.show();
        }
    }

    public void details() throws IOException {


        FuelCar fcar =fuelCarTableView.getSelectionModel().getSelectedItem();
        ElectricCar ecar = electricCarTableView.getSelectionModel().getSelectedItem();

        if( ecar== null) {
            if( fcar == null)
                return;
        }

        if( ecar!= null)
        {

            UserScreenController.setSelected( ecar);
            CarDetailsScreenController.setKlasa(ElectricCar.class);
            if( fcar != null)
            {Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Greska");
                alert.setHeaderText("Odabran prevelik broj automobila");
                alert.setContentText("Molimo odaberite samo jedan automobil");
                alert.showAndWait();
                electricCarTableView.getSelectionModel().clearSelection();
                fuelCarTableView.getSelectionModel().clearSelection();
                return;}

            //
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Unesite lozinku za admina");
            td.showAndWait();
            HashMap<String, String> userMap = FilesUtilities.getUsersFromFile();
            String passwordForKey = userMap.get("admin");
            if(td.getResult()==null)
                return;
            if (td.getResult().isEmpty() || td.getResult()==null)
                return;
            if(!passwordForKey.equals(FilesUtilities.passwords(td.getEditor().getText())))
                return;

            //

        }
        if( fcar != null)
        {
            //
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Unesite lozinku za admina");
            td.showAndWait();
            HashMap<String, String> userMap = FilesUtilities.getUsersFromFile();
            String passwordForKey = userMap.get("admin");
            if(td.getResult()==null)
                return;
            if (td.getResult().isEmpty() || td.getResult()==null)
                return;
            if(!passwordForKey.equals(FilesUtilities.passwords(td.getEditor().getText())))
                return;

            //

            UserScreenController.setSelected( fcar);
            CarDetailsScreenController.setKlasa(FuelCar.class);
        }
        electricCarTableView.getSelectionModel().clearSelection();
        fuelCarTableView.getSelectionModel().clearSelection();

        UserScreenController.detailsStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(LoginScreen.class.getResource("carDetails.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 650);
        if(ecar!=null)
            UserScreenController.detailsStage.setTitle("Detalji " + ecar.getManufacturer());

        if(fcar!=null) {
            UserScreenController.detailsStage.setTitle("Detalji " + fcar.getManufacturer());}

        UserScreenController.detailsStage.setScene(scene);
        UserScreenController.detailsStage.show();

    }

    public void carSearch() throws SQLException, IOException {

        if(selecteduser!= null)
        {

        Long idUser = selecteduser.getId();
        List<ElectricCar> eCarList = DataBaseUtilities.getAllElectricCarsForuser(idUser);
        selecteduser.setElectricCarsList(eCarList);
        String search = SearchTextField.getText();
        List<ElectricCar> filteredECarList;
        filteredECarList = eCarList.stream()
                .filter(c -> c.getManufacturer().toLowerCase().contains(search.toLowerCase()) || c.getModel().toLowerCase().contains(search.toLowerCase())|| c.getRegistrationNumber().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        ObservableList observableListE =
                FXCollections.observableArrayList(filteredECarList);

        electricCarTableView.setItems(observableListE);

        List<FuelCar> fCarList = DataBaseUtilities.getAllFuelCarsForuser(idUser);
        selecteduser.setFuelCarsList(fCarList);

        List<FuelCar> filteredFCarList;
        filteredFCarList = fCarList.stream()
                .filter(c -> c.getManufacturer().toLowerCase().contains(search.toLowerCase()) || c.getModel().toLowerCase().contains(search.toLowerCase())|| c.getRegistrationNumber().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        ObservableList observableListF =
                FXCollections.observableArrayList(filteredFCarList);

        fuelCarTableView.setItems(observableListF);}
        else{
            List<ElectricCar> eCarList = DataBaseUtilities.getAllElectricCarsFromDatabase();

            String search = SearchTextField.getText();
            List<ElectricCar> filteredECarList;
            filteredECarList = eCarList.stream()
                    .filter(c -> c.getManufacturer().toLowerCase().contains(search.toLowerCase()) || c.getModel().toLowerCase().contains(search.toLowerCase())|| c.getRegistrationNumber().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());

            ObservableList observableListE =
                    FXCollections.observableArrayList(filteredECarList);

            electricCarTableView.setItems(observableListE);

            List<FuelCar> fCarList = DataBaseUtilities.getAllFuelCarsFromDatabase();


            List<FuelCar> filteredFCarList;
            filteredFCarList = fCarList.stream()
                    .filter(c -> c.getManufacturer().toLowerCase().contains(search.toLowerCase()) || c.getModel().toLowerCase().contains(search.toLowerCase())|| c.getRegistrationNumber().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());

            ObservableList observableListF =
                    FXCollections.observableArrayList(filteredFCarList);

            fuelCarTableView.setItems(observableListF);

        }
    }


}
