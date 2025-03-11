package hr.java.application;

import hr.java.exeptions.UserNotRegisteredExeption;
import hr.java.utilities.FilesUtilities;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

import static hr.java.application.LoginScreen.logger;

public class LoginController {
    @FXML
    TextField usernameTextField;
    @FXML
    PasswordField passwordField;

  private static String username;


    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        LoginController.username = username;
    }

    public void userLogin() throws IOException {

        if(usernameTextField.getText().isEmpty())
        {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nedostaju podaci");
            alert.setHeaderText("Molimo unesite korisnicko ime");
            alert.setContentText("Nisu unesene sve potrebne vrijednosti, unos prekinut");
            alert.showAndWait();
            return;

        }


        username = usernameTextField.getText();


        if(passwordField.getText().isEmpty())
        {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nedostaju podaci");
            alert.setHeaderText("Molimo unesite lozinku");
            alert.setContentText("Nisu unesene sve potrebne vrijednosti, unos prekinut");
            alert.showAndWait();
            return;

        }


        HashMap <String, String> userMap = FilesUtilities.getUsersFromFile();

    try{
       if(!userMap.containsKey(usernameTextField.getText())) { throw new UserNotRegisteredExeption("Uneseno korisničko ime nije registrirano.");}
            String passwordForKey = userMap.get(usernameTextField.getText());
            if(passwordForKey.equals(FilesUtilities.passwords( passwordField.getText()))) {

                if(username.equals("admin")){
                    FXMLLoader fxmlLoader = new FXMLLoader(UserScreenController.class.getResource("changes.fxml"));
                    try{
                        Scene scene = new Scene(fxmlLoader.load(), 710,415);
                        LoginScreen.getMainStage().setTitle(usernameTextField.getText());
                        LoginScreen.getMainStage().setScene(scene);
                        LoginScreen.getMainStage().show();
                        return;
                    }catch (IOException e) {
                        logger.error(String.valueOf(e));
                        throw new RuntimeException(e);
                    }

                }


                FXMLLoader fxmlLoader = new FXMLLoader(UserScreenController.class.getResource("userScreen.fxml"));
                try{
                    Scene scene = new Scene(fxmlLoader.load(), 610,500);
                    LoginScreen.getMainStage().setTitle(usernameTextField.getText());
                    LoginScreen.getMainStage().setScene(scene);
                    LoginScreen.getMainStage().show();
                }catch (IOException e) {
                    logger.error(String.valueOf(e));
                    throw new RuntimeException(e);
                }
            }else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pogresna lozinka");
                alert.setHeaderText("Molimo unesite tocnu lozinku");

                passwordField.clear();

                alert.showAndWait();
                return;

            }

        }catch (UserNotRegisteredExeption exeption){

            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setHeaderText(null);
            alert.setTitle("Nepostojući korisnik");
            alert.setContentText("Unijeli ste korisnicko ime koje nije registrirano. Zelite li se registrirati");


            ButtonType buttonNewUser = new ButtonType("Novi korisnik");
            alert.getButtonTypes().setAll(buttonNewUser);

            alert.setOnCloseRequest(e -> {

                ButtonType result = alert.getResult();
                if (result != null && result == buttonNewUser) {
                   newUser();
                }
            });

            alert.show();
        }


    }

    public void newUser(){
        FXMLLoader fxmlLoader = new FXMLLoader(NewUserScreenController.class.getResource("NewUser.fxml"));

        try{
            Scene scene = new Scene(fxmlLoader.load(), 610,500);
            LoginScreen.getMainStage().setTitle("Novi korisnik");
            LoginScreen.getMainStage().setScene(scene);
            LoginScreen.getMainStage().show();
        }catch (IOException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }


}