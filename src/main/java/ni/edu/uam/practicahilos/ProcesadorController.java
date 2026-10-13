package ni.edu.uam.practicahilos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador de la vista principal (Parte 1: Práctica Base - Caso NicaData Solutions S.A.).
 * Demuestra la correcta delegación de tareas en segundo plano usando Threads en JavaFX
 * y la sincronización con la UI mediante Platform.runLater().
 */
public class ProcesadorController {

    // Componentes gráficos inyectados por FXML
    @FXML
    private Label lblEstado;

    @FXML
    private ProgressBar barraProgreso;

    @FXML
    private Label lblPorcentaje;

    @FXML
    private Button btnIniciar;

    @FXML
    private Button btnDetener;

    @FXML
    private Button btnReiniciar;

    // Referencia al hilo secundario de procesamiento
    private Thread hiloProceso;

    // Referencia opcional para el Reto Final
    private Stage stageMonitor;
    private MonitorController monitorController;

    /**
     * Inicializa los controles de la interfaz al cargar el FXML.
     */
    @FXML
    public void initialize() {
        barraProgreso.setProgress(0.0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Esperando inicio");
        habilitarBotones();
    }

    /**
     * Inicia la simulación del procesamiento de 100 registros en un hilo secundario.
     * Evita la creación de múltiples hilos concurrentes no deseados validando isAlive().
     */
    @FXML
    public void iniciarProceso() {
        // Validación: No permitir iniciar si ya hay un hilo en ejecución
        if (hiloProceso != null && hiloProceso.isAlive()) {
            return;
        }

        // Reiniciar valores visuales antes de iniciar
        barraProgreso.setProgress(0.0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Procesando registros...");

        // Configuración de estados de botones
        btnIniciar.setDisable(true);
        btnReiniciar.setDisable(true);
        btnDetener.setDisable(false);

        // Definición de la tarea en segundo plano mediante Runnable (Lambda)
        Runnable tarea = () -> {
            for (int i = 1; i <= 100; i++) {
                // Verificar si se ha solicitado una interrupción antes de continuar
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                try {
                    // Simulación de procesamiento pesado (100 ms por registro)
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Buena práctica: Restaurar el estado de interrupción del hilo
                    Thread.currentThread().interrupt();

                    // Notificar a la interfaz gráfica la detención
                    Platform.runLater(() -> {
                        lblEstado.setText("Proceso detenido");
                        habilitarBotones();
                    });
                    return; // Terminar la ejecución del Runnable inmediatamente
                }

                // Cálculo de progreso actual
                final int progreso = i;
                final double valorProgreso = progreso / 100.0;

                // Actualizar la interfaz de usuario de manera segura en el JavaFX Application Thread
                Platform.runLater(() -> {
                    barraProgreso.setProgress(valorProgreso);
                    lblPorcentaje.setText(progreso + "%");
                    lblEstado.setText("Procesando registro " + progreso + " de 100...");
                });
            }

            // Al finalizar todas las iteraciones satisfactoriamente
            if (!Thread.currentThread().isInterrupted()) {
                Platform.runLater(() -> {
                    lblEstado.setText("Proceso finalizado");
                    habilitarBotones();
                });
            }
        };

        // Creación y configuración del hilo
        hiloProceso = new Thread(tarea, "Hilo-Procesamiento");
        // Hilo daemon para que no impida la salida de la JVM si la ventana se cierra
        hiloProceso.setDaemon(true);
        hiloProceso.start();
    }

    /**
     * Solicita la interrupción limpia del hilo de procesamiento en ejecución.
     */
    @FXML
    public void detenerProceso() {
        if (hiloProceso != null && hiloProceso.isAlive()) {
            btnDetener.setDisable(true);
            lblEstado.setText("Solicitando detención...");
            hiloProceso.interrupt();
        }
    }

    /**
     * Reinicia los controles al estado original cuando ningún proceso está en ejecución.
     */
    @FXML
    public void reiniciarProceso() {
        // No reiniciar si hay un proceso corriendo
        if (hiloProceso != null && hiloProceso.isAlive()) {
            return;
        }

        barraProgreso.setProgress(0.0);
        lblPorcentaje.setText("0%");
        lblEstado.setText("Esperando inicio");
        habilitarBotones();
    }

    /**
     * Restaura el estado habilitado/deshabilitado de los botones.
     */
    public void habilitarBotones() {
        btnIniciar.setDisable(false);
        btnReiniciar.setDisable(false);
        btnDetener.setDisable(true);
    }

    /**
     * Método invocado cuando se cierra la ventana principal para asegurar
     * la detención de cualquier hilo activo y liberar recursos.
     */
    public void detenerAlCerrar() {
        if (hiloProceso != null && hiloProceso.isAlive()) {
            hiloProceso.interrupt();
        }

        if (monitorController != null) {
            monitorController.detenerAlCerrar();
        }
    }

    /**
     * =========================================================================
     * EJEMPLO DE ANTI-PATRÓN: PROCESO BLOQUEANTE EN EL HILO DE JavaFX
     * =========================================================================
     *
     * ¿Por qué no debemos ejecutar procesos prolongados directamente dentro de
     * los eventos de JavaFX?
     *
     * RESPUESTA:
     * Porque JavaFX utiliza un único hilo principal (JavaFX Application Thread)
     * encargado de despachar eventos de usuario (clics, teclado), calcular el layout,
     * procesar animaciones y repintar la escena gráfica en pantalla.
     *
     * Si ejecutamos una operación de larga duración (como Thread.sleep(5000), consultas
     * pesadas a base de datos, o cálculos intensivos) directamente en este hilo:
     * 1. La ventana se CONGELA por completo y no responde a la interacción del usuario.
     * 2. El sistema operativo puede marcar la aplicación como "(No responde)".
     * 3. No se pueden repintar los cambios visuales (como barras de progreso o etiquetas).
     * 4. Se degrada severamente la experiencia de usuario (UX).
     *
     * Por esta razón, las tareas pesadas deben delegarse a un hilo secundario (Thread/Task/Service)
     * y los cambios de interfaz deben sincronizarse mediante Platform.runLater().
     */
    @FXML
    public void procesoBloqueante() {
        lblEstado.setText("¡ADVERTENCIA! Bloqueando la interfaz por 5 segundos...");

        try {
            // Este sleep bloquea directamente el JavaFX Application Thread:
            // Observará que durante 5 segundos la ventana no se puede mover,
            // los botones no reaccionan y la interfaz no se redibuja.
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        lblEstado.setText("Bloqueo terminado. Esto es lo que se debe evitar.");
    }

    /**
     * Abre la ventana del Reto Final (Monitor de Procesamiento Empresarial con múltiples hilos).
     */
    @FXML
    public void abrirRetoFinal() {
        try {
            if (stageMonitor == null || !stageMonitor.isShowing()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("monitor-view.fxml"));
                Scene scene = new Scene(loader.load(), 620, 520);

                monitorController = loader.getController();

                stageMonitor = new Stage();
                stageMonitor.setTitle("Reto Final - Monitor de Procesamiento Empresarial");
                stageMonitor.setScene(scene);
                stageMonitor.setResizable(false);

                stageMonitor.setOnCloseRequest(e -> {
                    if (monitorController != null) {
                        monitorController.detenerAlCerrar();
                    }
                });

                stageMonitor.show();
            } else {
                stageMonitor.toFront();
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir el Monitor de Procesos");
            alert.setContentText("Ocurrió un error al cargar la vista monitor-view.fxml: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
