package hr.java.application;

import hr.java.models.Changes;
import hr.java.models.ElectricCar;
import hr.java.models.FuelCar;
import hr.java.models.User;
import hr.java.threads.SerializeThread;
import hr.java.utilities.DataBaseUtilities;
import hr.java.utilities.FilesUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static hr.java.application.LoginScreen.logger;

public class UsersController {

    @FXML
    private ListView usersListView;
    List<User> users;

    @FXML
    private TextField searchTextField;




    public void initialize() {
      setListView();
    }


    public void setListView()
    {
        try {users = DataBaseUtilities.getAllUsersFromDatabase();} catch (SQLException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);} catch (IOException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }

        users.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
        User admin = users.stream().filter(user -> user.getName().equals("admin")).findFirst().get();
        users.remove(admin);


        List<String> usernames = new ArrayList<>();

        for(User user:users)
        {
            usernames.add(user.getName());
        }

        ObservableList observableListUsers =
                FXCollections.observableArrayList(usernames);

        usersListView.setItems( observableListUsers);

    }



    public void delete() throws SQLException, IOException {


        try
        {
            String remove = usersListView.getSelectionModel().getSelectedItem().toString();

            //
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Unesite lozinku za admina");
            td.showAndWait();
            HashMap <String, String> userMap = FilesUtilities.getUsersFromFile();
            String passwordForKey = userMap.get("admin");
            if (td.getResult().isEmpty() || td.getResult()==null)
                return;
            if(!passwordForKey.equals(FilesUtilities.passwords(td.getEditor().getText())))
                return;

            //

            User delete = users.stream().filter(u -> u.getName().equals(remove)).findFirst().get();

            String deleted = remove;
            String change = "Korisnik";
            String oldValue = "Registriran";

            String newValue = "Obrisan";
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

            FilesUtilities.deleteUser(delete);
            DataBaseUtilities.deleteUser(delete);

            Alert uspjeh = new Alert(Alert.AlertType.INFORMATION);
            uspjeh.setTitle("Uspjeh");
            uspjeh.setHeaderText("Korisnik uspjesno uklonjen");
            uspjeh.showAndWait();

            setListView();
        }
        catch (NullPointerException ex)
        {
            logger.error(String.valueOf(ex));
            return;
        }

    }


    public static Stage vehiclesStage;

    public static Stage getVehiclesStage() {
        return vehiclesStage;
    }

    public void details() throws IOException {

        try
        {
            String selectedItem = usersListView.getSelectionModel().getSelectedItem().toString();
            usersListView.getSelectionModel().clearSelection();
            //
            TextInputDialog td = new TextInputDialog();
            td.setHeaderText("Unesite lozinku za admina");
            td.showAndWait();
            HashMap<String, String> userMap = FilesUtilities.getUsersFromFile();
            String passwordForKey = userMap.get("admin");
            if (td.getResult().isEmpty() || td.getResult()==null)
                return;
            if(!passwordForKey.equals(FilesUtilities.passwords(td.getEditor().getText())))
                return;

            //



            User selectedUser = users.stream().filter(u -> u.getName().equals(selectedItem)).findFirst().get();
            VehiclesController.setSelecteduser(selectedUser);
            vehiclesStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(LoginScreen.class.getResource("vehicles.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 710, 415);
            vehiclesStage.setTitle(selectedItem.toString());
            vehiclesStage.setScene(scene);
            vehiclesStage.show();
         //   VehiclesController.setSelecteduser(null);
            AdminMenuBarController.setOpenedFromUserDetails(true);


        }
        catch (NullPointerException ex)
        {
            logger.error(String.valueOf(ex));
            return;
        }
    }

    public void searchUsers()
    {
        String search = searchTextField.getText();
        List<User> filteredUsersList;
        filteredUsersList = users.stream()
                .filter(u -> u.getName().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
        filteredUsersList.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
        List<String> usernames = new ArrayList<>();

        for(User user:filteredUsersList)
        {
            usernames.add(user.getName());

        }

        ObservableList observableListUsers =
                FXCollections.observableArrayList(usernames);

        usersListView.setItems( observableListUsers);
    }

}
