package ni.edu.uam.practicahilos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX para la práctica de concurrencia e hilos.
 * Inicializa la interfaz gráfica base (Procesador de registros) y gestiona el ciclo de vida de la ventana.
 *
 * Caso de estudio: NicaData Solutions S.A.
 * Universidad Americana (UAM)
 */
public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("procesador-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 560, 420);

        // Obtener la instancia del controlador para registrar el cierre de la ventana
        ProcesadorController controller = fxmlLoader.getController();

        // Configuración de la ventana principal
        stage.setTitle("Práctica - Hilos en JavaFX");
        stage.setScene(scene);
        stage.setResizable(false);

        // Garantizar la detención limpia de hilos en ejecución al cerrar la ventana
        stage.setOnCloseRequest(event -> controller.detenerAlCerrar());

        stage.show();
    }

    /**
     * Método principal que lanza la aplicación JavaFX.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
