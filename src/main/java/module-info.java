module ru.vorotov.simulationslab4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.vorotov.simulationslab4 to javafx.fxml;
    exports ru.vorotov.simulationslab4;
}