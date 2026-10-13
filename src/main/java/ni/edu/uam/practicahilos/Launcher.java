package ni.edu.uam.practicahilos;

import javafx.application.Application;

/**
 * Clase auxiliar de lanzamiento para entornos donde JavaFX no es invocado
 * directamente a través del plugin modular o empaquetado Fat JAR.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
