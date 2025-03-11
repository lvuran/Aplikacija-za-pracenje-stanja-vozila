package hr.java.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

import static hr.java.application.LoginScreen.logger;

public class AdminMenuBarController {
    private static boolean openedFromUserDetails = false;

    public static void setOpenedFromUserDetails(boolean openedFromUserDetails) {
        AdminMenuBarController.openedFromUserDetails = openedFromUserDetails;
    }

    public void logout(){
        if(openedFromUserDetails)
        {
            UsersController.getVehiclesStage().close();
            openedFromUserDetails = false;
        }
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

    public void showChanges(){
        if(openedFromUserDetails)
        {
            UsersController.getVehiclesStage().close();
            openedFromUserDetails = false;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(UserScreenController.class.getResource("changes.fxml"));
        try{
            Scene scene = new Scene(fxmlLoader.load(), 710,415);
            LoginScreen.getMainStage().setTitle("admin");
            LoginScreen.getMainStage().setScene(scene);
            LoginScreen.getMainStage().show();
            return;
        }catch (IOException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }

    public void showUsers(){
        if(openedFromUserDetails)
        {
            UsersController.getVehiclesStage().close();
            openedFromUserDetails = false;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(UserScreenController.class.getResource("users.fxml"));
        try{
            Scene scene = new Scene(fxmlLoader.load(), 710,415);
            LoginScreen.getMainStage().setTitle("admin");
            LoginScreen.getMainStage().setScene(scene);
            LoginScreen.getMainStage().show();
            return;
        }catch (IOException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }
    public void showVehicles(){
        if(openedFromUserDetails)
        {
            UsersController.getVehiclesStage().close();
            openedFromUserDetails = false;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(UserScreenController.class.getResource("vehicles.fxml"));
        try{
            Scene scene = new Scene(fxmlLoader.load(), 710,415);
            LoginScreen.getMainStage().setTitle("admin");
            LoginScreen.getMainStage().setScene(scene);
            LoginScreen.getMainStage().show();
            return;
        }catch (IOException e) {
            logger.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }
}
