package hr.java.application;

import hr.java.models.Car;
import hr.java.models.Changes;
import hr.java.models.ElectricCar;
import hr.java.threads.DeserializeThread;
import hr.java.utilities.DataBaseUtilities;
import hr.java.utilities.FilesUtilities;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

import static hr.java.application.LoginScreen.logger;

public class ChangesScreenController {


    @FXML
    private TableView<Changes> changesTableView;
    @FXML
    private TableColumn<Changes, String> carTableColumn;
    @FXML
    private TableColumn<Changes, String> changeTableColumn;
    @FXML
    private TableColumn<Changes, String> newValueTableColumn;
    @FXML
    private TableColumn<Changes, String> oldValueTableColumn;
    @FXML
    private TableColumn<Changes, String> userTableColumn;
    @FXML
    private TableColumn<Changes, String> dateTimeTableColumn;

    public void initialize()
    {
        carTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes, String> carStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(carStringCellDataFeatures.getValue().getCar());
            }
        });
        changeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes, String> changeStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(changeStringCellDataFeatures.getValue().getChange());
            }
        });
        newValueTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes, String> newStringCellDataFeatures) {
                if(newStringCellDataFeatures.getValue().getNewValue()!=null)
                return new ReadOnlyStringWrapper(newStringCellDataFeatures.getValue().getNewValue().toString());
                return null;
            }
        });
        oldValueTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes, String> oldStringCellDataFeatures) {
                if(oldStringCellDataFeatures.getValue().getOldValue()!=null)
                return new ReadOnlyStringWrapper(oldStringCellDataFeatures.getValue().getOldValue().toString());
                return null;
            }
        });
        userTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes, String> userStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(userStringCellDataFeatures.getValue().getUser());
            }
        });
        dateTimeTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes, String> dateTimeStringCellDataFeatures) {
                return new ReadOnlyStringWrapper(dateTimeStringCellDataFeatures.getValue().getTime());
            }
        });
        List<Changes> changesList;
        DeserializeThread deserializeThread = new DeserializeThread();
        Thread startDeserialization = new Thread(deserializeThread);
        startDeserialization.start();
        try {
            startDeserialization.join();
        } catch (InterruptedException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
        changesList = deserializeThread.getChangesList();

        ObservableList observableList = FXCollections.observableArrayList(changesList);

        changesTableView.setItems(observableList);

    }


}
