module hr.java.pracenje_stanja_vozilaseminarski {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;


    opens hr.java.application to javafx.fxml;
    exports hr.java.application;
}