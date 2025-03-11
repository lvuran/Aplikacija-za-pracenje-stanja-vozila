package hr.java.threads;

import hr.java.application.CarDetailsScreenController;
import hr.java.application.LoginController;
import hr.java.application.LoginScreen;
import hr.java.models.Car;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static hr.java.application.LoginScreen.logger;


public class CalculateRemainingTimeThread implements Runnable{
    private LocalDateTime nextDate;
    private boolean  operate = true;
    int broj;
    static int alert1 = 0, alert2 = 0;
    @FXML
    public Label toUpdate;



    @Override
    public void run() {


        while(operate) {
            if(broj == 1)
            nextDate = CarDetailsScreenController.nextmaintenance;
            if(broj == 2)
            nextDate =CarDetailsScreenController.nextTireCheck;
            if(nextDate != null){
                Duration remainingTime;
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);
                remainingTime = Duration.between(currentDateTime, nextDate);
                Long dana = remainingTime.getSeconds() / 60 / 60 / 24;
                Long sati = remainingTime.getSeconds() / 60 / 60 - dana * 24;
                Long minuta = remainingTime.getSeconds() / 60 - dana * 24 * 60 - sati * 60;
                Long sekundi = remainingTime.getSeconds() - dana * 24 * 60 * 60 - sati * 60 * 60 - minuta * 60;

                Platform.runLater(() -> {
                    toUpdate.setText(dana + " Dana " + sati + " sati " + minuta + " minuta " + sekundi + "sekundi");

                    if(dana > 0  ) {
                        toUpdate.textFillProperty().setValue(Color.BLACK);
                    }
                    if(dana <= 0  )
                    {
                        toUpdate.textFillProperty().setValue(Color.RED);

                        if(broj == 1 && alert1 == 0){
                        alert1++;
                        if (!LoginController.getUsername().equals("admin")) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);

                        alert.setTitle("Kasnite s tehničkim pregledom");
                        alert.setHeaderText("Molimo prvom prilikom obavite tehnički pregled");
                        alert.showAndWait();}}

                    if(broj == 2 && alert2 == 0)
                    {
                        alert2++;
                        if (!LoginController.getUsername().equals("admin")){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Kasnite s pregledom tlaka u gumama");
                        alert.setHeaderText("Molimo prvom prilikom pregledajte tlak u gumama");
                        alert.showAndWait();}
                    }}

                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(String.valueOf(e));
                    stopThread();
                    throw new RuntimeException(e);
                }
            }}}


    public CalculateRemainingTimeThread(Label nextMaintenanceDateLabel, int _broj) {
        toUpdate = nextMaintenanceDateLabel;
        broj = _broj;
    }


    public void stopThread() {
        operate = false;
    }

    public void setNextDate(LocalDateTime nextDate) {
        this.nextDate = nextDate;
    }

    public static void setAlert1(int alert1) {
        CalculateRemainingTimeThread.alert1 = alert1;
    }

    public static void setAlert2(int alert2) {
        CalculateRemainingTimeThread.alert2 = alert2;
    }
}
