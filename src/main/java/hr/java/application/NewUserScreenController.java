package hr.java.application;

import hr.java.exeptions.PasswordsNotMatchingExeption;
import hr.java.exeptions.UserNotRegisteredExeption;
import hr.java.exeptions.UsernameDuplicateExeption;
import hr.java.models.Changes;
import hr.java.threads.SerializeThread;
import hr.java.utilities.DataBaseUtilities;
import hr.java.utilities.FilesUtilities;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.HashMap;

import static hr.java.application.LoginScreen.logger;

public class NewUserScreenController {

    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField repeatedPasswordField;


    public void initialize() {
        usernameTextField.setText(LoginController.getUsername());

    }

    public void registerUser() throws IOException, SQLException {

        if (usernameTextField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nedostaju podaci");
            alert.setHeaderText("Molimo unesite korisnicko ime");
            alert.setContentText("Nisu unesene sve potrebne vrijednosti, unos prekinut");
            alert.showAndWait();
            return;
        }

        if (passwordField.getText().isEmpty() || repeatedPasswordField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nedostaju podaci");
            alert.setHeaderText("Molimo unesite lozinku");
            alert.setContentText("Nisu unesene sve potrebne vrijednosti, unos prekinut");
            alert.showAndWait();
            return;
        }

        try {
            HashMap<String, String> userMap = FilesUtilities.getUsersFromFile();
            if (userMap.containsKey(usernameTextField.getText())) {
                throw new UsernameDuplicateExeption("Uneseno korisničko ime je već registrirano.");
            }
        } catch (UsernameDuplicateExeption exeption) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registrirano kosisnicko ime");
            alert.setHeaderText("Korisnicko ime koje ste unijeli je vec registrirano, molimo unesite novo korisnicko ime.");
            usernameTextField.clear();
            passwordField.clear();
            repeatedPasswordField.clear();
            alert.showAndWait();
            return;
        }

        try {
            if (!passwordField.getText().equals(repeatedPasswordField.getText())) {
                throw new PasswordsNotMatchingExeption("Unesene su različite lozinke.");
            }
        } catch (PasswordsNotMatchingExeption ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Razlicite lozinke");
            alert.setHeaderText("Molimo unesite tocnu lozinku");

            passwordField.clear();
            repeatedPasswordField.clear();
            alert.showAndWait();
            return;
        }


        FilesUtilities.registerUser(usernameTextField.getText(), passwordField.getText());
        DataBaseUtilities.saveNewUserToDatabase(usernameTextField.getText());
        LoginController.setUsername(usernameTextField.getText());


        String carName = "";
        String change = "Korisnik";
        String oldValue = "";

        String newValue = "Registriran";
        String user = LoginController.getUsername();
        Changes changed = new Changes<>(carName, change, oldValue, newValue,usernameTextField.getText());

        SerializeThread serialize = new SerializeThread(changed);
        Thread startSerialization = new Thread(serialize);
        startSerialization.start();
        try {
            startSerialization.join();
        } catch (InterruptedException ex) {
            logger.error(String.valueOf(ex));
            throw new RuntimeException(ex);
        }


        FXMLLoader fxmlLoader = new FXMLLoader(NewCarScreenController.class.getResource("newCar.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load(), 610, 500);
            LoginScreen.getMainStage().setTitle("Unos automobila");
            LoginScreen.getMainStage().setScene(scene);
            LoginScreen.getMainStage().show();
        } catch (IOException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }

    public void cancel()
    {

        LoginController.setUsername(null);
        FXMLLoader fxmlLoader = new FXMLLoader(LoginScreen.class.getResource("login.fxml"));
        try{
            Scene scene = new Scene(fxmlLoader.load(), 600, 450);
            LoginScreen.getMainStage().setTitle("Hello!");
            LoginScreen.getMainStage().setScene(scene);
            LoginScreen.getMainStage().show();
        }catch (IOException e)
        {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }

}
