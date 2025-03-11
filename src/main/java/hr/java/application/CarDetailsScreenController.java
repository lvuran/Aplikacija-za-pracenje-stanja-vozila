package hr.java.application;

import hr.java.models.Car;
import hr.java.models.Changes;
import hr.java.models.ElectricCar;
import hr.java.models.FuelCar;
import hr.java.threads.CalculateRemainingTimeThread;
import hr.java.threads.SerializeThread;
import hr.java.utilities.DataBaseUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import static hr.java.application.LoginScreen.logger;

public class CarDetailsScreenController {

    //CAR INFO
    @FXML
    private Label manufacturer;
    @FXML
    private Label model;
    @FXML
    private Label registration;
    @FXML
    private Label category;
    @FXML
    private Label type;
    @FXML
    private Label manufacturingDate;
    @FXML
    private Label color;
    @FXML
    private Label capacity;


    //MAINTENANCE
    @FXML
    private DatePicker lastMaintenanceDatePicker;
    @FXML
    public Label nextMaintenanceDateLabel;

    //TIRES
    @FXML
    private DatePicker lastTireCheckDatePicker;
    @FXML
    public Label nextTireCheckDateLabel;


    //FUEL BATTERY
    @FXML
    private Text fuelBatterytext;
    @FXML
    private Text fuelBatteyexpensetext;
    @FXML
    private TextField fuelBatteryInsertedTextField;
    @FXML
    public Label calculatedFuelBattery;

    //NOTES
    @FXML
    public TextArea notesTextArea;



    static Class<? extends Car> klasa ;

    public static void setKlasa(Class<? extends Car> klasa) {
        CarDetailsScreenController.klasa = klasa;
    }

    //THREADS
    public static LocalDateTime nextmaintenance = null;
    public static LocalDateTime nextTireCheck = null;
    public static boolean maintenanceThreadNotStarted;
    public static boolean tiresThreadNotStarted;
    CalculateRemainingTimeThread remainingTireCheck;
    Thread tireThread;
    CalculateRemainingTimeThread remainingMaitenance;
    Thread maintenanceThread;


    public void initialize() throws SQLException, IOException, InterruptedException {

        manufacturer.setText(UserScreenController.getselectedCar().getManufacturer());
        model.setText(UserScreenController.getselectedCar().getModel());
        registration.setText(UserScreenController.getselectedCar().getRegistrationNumber());
        color.setText("Boja: " + UserScreenController.getselectedCar().getColor());
        category.setText(UserScreenController.getselectedCar().getVehicleCategory().name());
        type.setText("Tip: " + UserScreenController.getselectedCar().getCarType().name());
        manufacturingDate.setText("Proizvedeno: " + UserScreenController.getselectedCar().getManufacturingDate().format(DateTimeFormatter.ofPattern("dd:MM:yyyy")));
        if (klasa == FuelCar.class) {
            FuelCar car = UserScreenController.getselectedCar();
            capacity.setText("Kapacitet rezervoara: " + car.getTankCapacity().capacity() + " l");
            fuelBatterytext.setText("Unesite koliko goriva ste natočili pri zadnjoj posjeti crpki.");
            fuelBatteyexpensetext.setText("Ukupan trošak:");
            calculatedFuelBattery.setText(String.valueOf(car.getFuelExpense()) + " eura");

        }
        if(UserScreenController.getselectedCar() instanceof ElectricCar){
        //if (klasa == ElectricCar.class) {
            ElectricCar car =  UserScreenController.getselectedCar();
            capacity.setText("Kapacitet baterije: " + car.getBatteryCapacity().capacity() + " kWh");
            fuelBatterytext.setText("Unesite trenutno stanje baterije.");
            fuelBatteyexpensetext.setText("Preostalo kilometara:");
            calculatedFuelBattery.setText(String.valueOf(car.getRemainingKilometers()) + " km");
        }


        open = 0;
        lastMaintenanceDatePicker.setValue(UserScreenController.getselectedCar().getLastMaintenanceDate());
        lastTireCheckDatePicker.setValue(UserScreenController.getselectedCar().getLastTireCheckDate());
        maintenanceThreadNotStarted = true;
        tiresThreadNotStarted = true;
        calculateMaintenance();
        calculateTireCheck();
        open++;


        notesTextArea.setText(UserScreenController.getselectedCar().getNotes());


        UserScreenController.detailsStage.setOnCloseRequest(event -> {
            try {
                if (maintenanceThread != null) {
                    remainingMaitenance.stopThread();
                    maintenanceThread.join();
                }
                if (tireThread != null) {
                    remainingTireCheck.stopThread();
                    tireThread.join();
                }
            } catch (InterruptedException e) {
                logger.error(String.valueOf(e));
                throw new RuntimeException(e);
            }
        });
    }


    public void calculateMaintenance() throws InterruptedException {

        if (lastMaintenanceDatePicker.getValue() != null) {
            if (lastMaintenanceDatePicker.getValue().isAfter(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pogresan datum");
                alert.setHeaderText("Molimo unesite datum koji je prosao");

                lastMaintenanceDatePicker.setValue(UserScreenController.getselectedCar().getLastMaintenanceDate());

                alert.showAndWait();
                return;

            }
            if (open > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potvrda");
                alert.setHeaderText(lastMaintenanceDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                alert.setContentText("Postaviti navedeni datum za datum posljednjeg tehničkog pregleda?");
                ButtonType buttonYes = new ButtonType("Da");
                ButtonType buttonNo = new ButtonType("Ne");
                alert.getButtonTypes().setAll(buttonYes, buttonNo);

                alert.setOnCloseRequest(e -> {
                    ButtonType result = alert.getResult();
                    if (result != null && result == buttonYes) {

                        {
                            if (maintenanceThreadNotStarted) {
                                remainingMaitenance = new CalculateRemainingTimeThread(nextMaintenanceDateLabel, 1);
                                maintenanceThread = new Thread(remainingMaitenance);
                                maintenanceThread.start();
                                maintenanceThreadNotStarted = false;

                            }
                            CalculateRemainingTimeThread.setAlert1(0);
                            String carName = UserScreenController.getselectedCar().getManufacturer() +" "+ UserScreenController.getselectedCar().getModel();
                            String change = "Tehnički pregled";
                            LocalDate oldValue = UserScreenController.getselectedCar().getLastMaintenanceDate();

                            UserScreenController.getselectedCar().setLastMaintenanceDate(lastMaintenanceDatePicker.getValue());
                            LocalDate newValue = lastMaintenanceDatePicker.getValue();
                            String user = LoginController.getUsername();
                            Changes changed = new Changes<>(carName, change, oldValue, newValue,user);



                            if (klasa == ElectricCar.class) {
                                ElectricCar car =  UserScreenController.getselectedCar();
                                try {
                                    DataBaseUtilities.updateElectricCarMaintenance(car, lastMaintenanceDatePicker.getValue());
                                } catch (SQLException ex) {
                                    logger.error(String.valueOf(ex));
                                    throw new RuntimeException(ex);
                                } catch (IOException ex) {
                                    logger.error(String.valueOf(ex));
                                    throw new RuntimeException(ex);
                                }
                            }
                            if (klasa == FuelCar.class) {
                                FuelCar car =  UserScreenController.getselectedCar();
                                try {
                                    DataBaseUtilities.updateFuelCarMaintenance(car, lastMaintenanceDatePicker.getValue());
                                } catch (SQLException ex) {
                                    logger.error(String.valueOf(ex));
                                    throw new RuntimeException(ex);
                                } catch (IOException ex) {
                                    logger.error(String.valueOf(ex));
                                    throw new RuntimeException(ex);
                                }
                            }


                            LocalDate nextMaintenanceDateCalculated = lastMaintenanceDatePicker.getValue().plusYears(1);
                            nextmaintenance = nextMaintenanceDateCalculated.atTime(8, 0, 0);
                            SerializeThread serialize = new SerializeThread(changed);
                            Thread startSerialization = new Thread(serialize);
                            startSerialization.start();
                            try {
                                startSerialization.join();
                            } catch (InterruptedException ex) {
                                logger.error(String.valueOf(ex));
                                throw new RuntimeException(ex);
                            }
                        }
                        if (result != null && result == buttonNo) {
                            lastMaintenanceDatePicker.setValue(UserScreenController.getselectedCar().getLastMaintenanceDate());
                            return;
                        }
                    } });
                alert.showAndWait();
        }
            if(open == 0)
            {
                if (maintenanceThreadNotStarted) {
                    remainingMaitenance = new CalculateRemainingTimeThread(nextMaintenanceDateLabel, 1);
                    maintenanceThread = new Thread(remainingMaitenance);
                    maintenanceThread.start();
                    maintenanceThreadNotStarted = false;
                }
                LocalDate nextMaintenanceDateCalculated = lastMaintenanceDatePicker.getValue().plusYears(1);
                nextmaintenance = nextMaintenanceDateCalculated.atTime(8, 0, 0);
            }
    }}

    int open;

    public void calculateTireCheck() throws InterruptedException {

        if (lastTireCheckDatePicker.getValue() != null) {

            if (lastTireCheckDatePicker.getValue().isAfter(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pogresan datum");
                alert.setHeaderText("Molimo unesite datum koji je prosao");

                lastTireCheckDatePicker.setValue(UserScreenController.getselectedCar().getLastTireCheckDate());

                alert.showAndWait();
                return;

            }
            if (open > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potvrda");
                alert.setHeaderText(lastTireCheckDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                alert.setContentText("Postaviti navedeni datum za datum posljednje provjere tlaka guma?");
                ButtonType buttonYes = new ButtonType("Da");
                ButtonType buttonNo = new ButtonType("Ne");
                alert.getButtonTypes().setAll(buttonYes, buttonNo);

                alert.setOnCloseRequest(e -> {
                    ButtonType result = alert.getResult();
                    if (result != null && result == buttonYes) {
                        {
                            if (tiresThreadNotStarted) {
                                remainingTireCheck = new CalculateRemainingTimeThread(nextTireCheckDateLabel, 2);
                                tireThread = new Thread(remainingTireCheck);
                                tireThread.start();
                                tiresThreadNotStarted = false;
                            }
                            CalculateRemainingTimeThread.setAlert2(0);
                            String carName = UserScreenController.getselectedCar().getManufacturer() +" "+ UserScreenController.getselectedCar().getModel();
                            String change = "Pregled guma";
                            LocalDate oldValue = UserScreenController.getselectedCar().getLastTireCheckDate();

                            UserScreenController.getselectedCar().setLastTireCheckDate(lastTireCheckDatePicker.getValue());

                            LocalDate newValue = UserScreenController.getselectedCar().getLastTireCheckDate();
                            String user = LoginController.getUsername();
                            Changes changed = new Changes<>(carName, change, oldValue, newValue,user);



                            if (klasa == ElectricCar.class) {
                                ElectricCar car =  UserScreenController.getselectedCar();
                                try {
                                    DataBaseUtilities.updateElectricCarTires(car,lastTireCheckDatePicker.getValue());
                                } catch (SQLException ex) {
                                    logger.error(String.valueOf(ex));
                                    throw new RuntimeException(ex);
                                } catch (IOException ex) {
                                    logger.error(String.valueOf(ex));
                                    throw new RuntimeException(ex);
                                }}
                            if (klasa == FuelCar.class) {
                                FuelCar car = UserScreenController.getselectedCar();
                                try {
                                    DataBaseUtilities.updateFuelCarTires(car,lastTireCheckDatePicker.getValue());
                                } catch (SQLException ex) {
                                    logger.error(String.valueOf(ex));
                                    throw new RuntimeException(ex);
                                } catch (IOException ex) {
                                    logger.error(String.valueOf(ex));
                                    throw new RuntimeException(ex);
                                }}

                            LocalDate nextTireCheckDateCalculated = lastTireCheckDatePicker.getValue().plusMonths(1);
                            nextTireCheck = nextTireCheckDateCalculated.atTime(8, 0, 0);
                            SerializeThread serialize = new SerializeThread(changed);
                            Thread startSerialization = new Thread(serialize);
                            startSerialization.start();
                            try {
                                startSerialization.join();
                            } catch (InterruptedException ex) {
                                logger.error(String.valueOf(ex));
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                    if (result != null && result == buttonNo) {
                        lastTireCheckDatePicker.setValue(UserScreenController.getselectedCar().getLastTireCheckDate());
                        return;
                    }
                });
                alert.showAndWait();
            }
            if(open == 0)
            {
                if (tiresThreadNotStarted) {
                    remainingTireCheck = new CalculateRemainingTimeThread(nextTireCheckDateLabel, 2);
                    tireThread = new Thread(remainingTireCheck);
                    tireThread.start();
                    tiresThreadNotStarted = false;
                }
                LocalDate nextTireCheckDateCalculated = lastTireCheckDatePicker.getValue().plusMonths(1);
                nextTireCheck = nextTireCheckDateCalculated.atTime(8, 0, 0);

            }
        }
    }

    public void calculate() {
        if (!fuelBatteryInsertedTextField.getText().isEmpty()) {

            try {
                Long.parseLong(fuelBatteryInsertedTextField.getText());
            }
            catch (NumberFormatException ex)
            {
                logger.error(String.valueOf(ex));
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pogresan unos");
                alert.setHeaderText("Molimo unesite brojčanu vrijednost.");

                fuelBatteryInsertedTextField.clear();
                alert.showAndWait();
                return;
            }


            if (klasa == FuelCar.class) {
                if(Long.valueOf(fuelBatteryInsertedTextField.getText()) < 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Pogresan unos");
                    alert.setHeaderText("Molimo unesite broj litara koji je veći od 0");

                    fuelBatteryInsertedTextField.clear();
                    alert.showAndWait();
                return;
                }
                FuelCar car = UserScreenController.getselectedCar();
                String value = fuelBatteryInsertedTextField.getText();
                BigDecimal bd = new BigDecimal(value);
                if(bd.compareTo(car.getTankCapacity().capacity())==1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Pogresan unos");
                    alert.setHeaderText("Molimo unesite broj litara koji je manji od kapaciteta rezrvoara");

                    fuelBatteryInsertedTextField.clear();
                    alert.showAndWait();
                    return;
                }


                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potvrda");
                alert.setHeaderText(fuelBatteryInsertedTextField.getText() + " L");
                alert.setContentText("Pri zadnjoj posjeti crpki natočili ste navedeni broj litara goriva?");
                ButtonType buttonYes = new ButtonType("Da");
                ButtonType buttonNo = new ButtonType("Ne");
                alert.getButtonTypes().setAll(buttonYes, buttonNo);

                alert.setOnCloseRequest(e -> {
                    ButtonType result = alert.getResult();
                    if (result != null && result == buttonYes) {

                        FuelCar fuelCar = UserScreenController.getselectedCar();
                        String carName = fuelCar.getManufacturer() +" "+ fuelCar.getModel();
                        String change = "Trošak goriva";
                        BigDecimal oldValue = fuelCar.getFuelExpense();
                        BigDecimal expense = fuelCar.calculateTotalFuelExpense(Long.valueOf(fuelBatteryInsertedTextField.getText()));
                        BigDecimal newValue = fuelCar.getFuelExpense();
                        String user = LoginController.getUsername();
                        Changes changed = new Changes<>(carName, change, oldValue, newValue,user);
                        try {
                            DataBaseUtilities.updateFuelCarExpense(fuelCar);
                        } catch (SQLException ex) {
                            logger.error(String.valueOf(ex));
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            logger.error(String.valueOf(ex));
                            throw new RuntimeException(ex);
                        }

                        calculatedFuelBattery.setText(expense.toString() + " eura");
                        fuelBatteryInsertedTextField.clear();
                        SerializeThread serialize = new SerializeThread(changed);
                        Thread startSerialization = new Thread(serialize);
                        startSerialization.start();
                        try {
                            startSerialization.join();
                        } catch (InterruptedException ex) {
                            logger.error(String.valueOf(ex));
                            throw new RuntimeException(ex);
                        }
                    }
                    if (result != null && result == buttonNo) {
                        fuelBatteryInsertedTextField.clear();
                        return;
                    }
                });
                alert.showAndWait();
            }
            if (klasa == ElectricCar.class) {
                if(Long.valueOf(fuelBatteryInsertedTextField.getText()) > 100 || Long.valueOf(fuelBatteryInsertedTextField.getText())< 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Pogresan unos");
                    alert.setHeaderText("Molimo unesite mogući postotak baterije (između 0% i 100%)");

                    fuelBatteryInsertedTextField.clear();
                    alert.showAndWait();
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Potvrda");
                alert.setHeaderText(fuelBatteryInsertedTextField.getText() + " %");
                alert.setContentText("Je li navedeno točno stanje baterije?");
                ButtonType buttonYes = new ButtonType("Da");
                ButtonType buttonNo = new ButtonType("Ne");
                alert.getButtonTypes().setAll(buttonYes, buttonNo);

                alert.setOnCloseRequest(e -> {
                    ButtonType result = alert.getResult();
                    if (result != null && result == buttonYes) {

                        ElectricCar electricCar =  UserScreenController.getselectedCar();
                        String carName = electricCar.getManufacturer() +" "+ electricCar.getModel();
                        String change = "Preostalo kilometara";
                        BigDecimal oldValue = electricCar.getRemainingKilometers();
                electricCar.setBatteryPercentage(BigDecimal.valueOf(Long.parseLong(fuelBatteryInsertedTextField.getText())));
                BigDecimal batteryTime = electricCar.calculateRemaingingRange();
                        BigDecimal newValue = electricCar.getRemainingKilometers();
                        String user = LoginController.getUsername();
                        Changes changed = new Changes<>(carName, change, oldValue, newValue,user);
                        try {
                            DataBaseUtilities.updateElectricCarRange(electricCar);
                        } catch (SQLException ex) {
                            logger.error(String.valueOf(ex));
                            throw new RuntimeException(ex);
                        } catch (IOException ex) {
                            logger.error(String.valueOf(ex));
                            throw new RuntimeException(ex);
                        }
                        calculatedFuelBattery.setText(batteryTime.toString() + " km");
                        SerializeThread serialize = new SerializeThread(changed);
                        Thread startSerialization = new Thread(serialize);
                        startSerialization.start();
                        try {
                            startSerialization.join();
                        } catch (InterruptedException ex) {
                            logger.error(String.valueOf(ex));
                            throw new RuntimeException(ex);
                        }
                fuelBatteryInsertedTextField.clear();     }
                        if (result != null && result == buttonNo) {
                            fuelBatteryInsertedTextField.clear();
                            return;
                        }
                    });
                alert.showAndWait();
            }
        }


    }
    public void saveNotes()
    {


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potvrda");
        alert.setHeaderText("Unos bilješki");
        alert.setContentText("Želite li unijeti bilješke?");
        ButtonType buttonYes = new ButtonType("Da");
        ButtonType buttonNo = new ButtonType("Ne");
        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        alert.setOnCloseRequest(e -> {
            ButtonType result = alert.getResult();
            if (result != null && result == buttonYes) {
                String carName = UserScreenController.getselectedCar().getManufacturer() +" "+ UserScreenController.getselectedCar().getModel();
                String change = "Bilješke";
                String  oldValue =UserScreenController.getselectedCar().getNotes() ;
                UserScreenController.getselectedCar().setNotes(notesTextArea.getText());
                String  newValue =UserScreenController.getselectedCar().getNotes() ;
                String user = LoginController.getUsername();
                Changes changed = new Changes<>(carName, change, oldValue, newValue,user);
                if (klasa == ElectricCar.class) {
                    ElectricCar car = UserScreenController.getselectedCar();
                    try {
                        DataBaseUtilities.updateElectricCarNotes(car);
                    } catch (SQLException ex) {
                        logger.error(String.valueOf(ex));
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        logger.error(String.valueOf(ex));
                        throw new RuntimeException(ex);
                    }}
                if (klasa == FuelCar.class) {
                    FuelCar car =  UserScreenController.getselectedCar();
                    try {
                        DataBaseUtilities.updateFuelCarNotes(car);
                    } catch (SQLException ex) {
                        logger.error(String.valueOf(ex));
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        logger.error(String.valueOf(ex));
                        throw new RuntimeException(ex);
                    }}
                SerializeThread serialize = new SerializeThread(changed);
                Thread startSerialization = new Thread(serialize);
                startSerialization.start();
                try {
                    startSerialization.join();
                } catch (InterruptedException ex) {
                    logger.error(String.valueOf(ex));
                    throw new RuntimeException(ex);
                }

                 }
            if (result != null && result == buttonNo) {
                notesTextArea.setText(UserScreenController.getselectedCar().getNotes());
                return;
            }
        });
        alert.showAndWait();
    }

    }
