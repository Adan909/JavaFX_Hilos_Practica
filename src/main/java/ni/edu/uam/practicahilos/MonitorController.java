package ni.edu.uam.practicahilos;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 * Controlador de la vista del Reto Final: Monitor de procesamiento empresarial.
 * Gestiona la ejecución simultánea e independiente de dos hilos de trabajo:
 * 1. Hilo de Clientes: 100 registros (100 ms por registro).
 * 2. Hilo de Productos: 50 registros (180 ms por registro).
 *
 * Cada proceso cuenta con sus propios controles, barra de progreso y estado,
 * garantizando la seguridad en el acceso a la UI mediante Platform.runLater().
 */
public class MonitorController {

    // =========================================================================
    // CONTROLES - PROCESAMIENTO DE CLIENTES
    // =========================================================================
    @FXML
    private Label lblEstadoClientes;

    @FXML
    private ProgressBar barraProgresoClientes;

    @FXML
    private Label lblPorcentajeClientes;

    @FXML
    private Button btnIniciarClientes;

    @FXML
    private Button btnDetenerClientes;

    @FXML
    private Button btnReiniciarClientes;

    // =========================================================================
    // CONTROLES - PROCESAMIENTO DE PRODUCTOS
    // =========================================================================
    @FXML
    private Label lblEstadoProductos;

    @FXML
    private ProgressBar barraProgresoProductos;

    @FXML
    private Label lblPorcentajeProductos;

    @FXML
    private Button btnIniciarProductos;

    @FXML
    private Button btnDetenerProductos;

    @FXML
    private Button btnReiniciarProductos;

    // =========================================================================
    // CONTROLES GLOBALES
    // =========================================================================
    @FXML
    private Button btnIniciarTodos;

    @FXML
    private Button btnDetenerTodos;

    @FXML
    private Button btnReiniciarTodos;

    // Instancias de los hilos de trabajo independientes
    private Thread hiloClientes;
    private Thread hiloProductos;

    /**
     * Inicializa los controles visuales en el arranque de la vista.
     */
    @FXML
    public void initialize() {
        reiniciarClientes();
        reiniciarProductos();
    }

    // =========================================================================
    // LÓGICA DEL HILO DE CLIENTES (100 registros, 100 ms)
    // =========================================================================

    /**
     * Inicia el procesamiento concurrente de clientes en su propio hilo.
     */
    @FXML
    public void iniciarClientes() {
        // Evitar doble ejecución concurrente
        if (hiloClientes != null && hiloClientes.isAlive()) {
            return;
        }

        barraProgresoClientes.setProgress(0.0);
        lblPorcentajeClientes.setText("0%");
        lblEstadoClientes.setText("Procesando clientes...");

        btnIniciarClientes.setDisable(true);
        btnReiniciarClientes.setDisable(true);
        btnDetenerClientes.setDisable(false);

        Runnable tareaClientes = () -> {
            final int totalClientes = 100;
            final int retardoMs = 100;

            for (int i = 1; i <= totalClientes; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                try {
                    Thread.sleep(retardoMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Platform.runLater(() -> {
                        lblEstadoClientes.setText("Clientes: Detenido");
                        habilitarBotonesClientes();
                    });
                    return;
                }

                final int actual = i;
                final double progreso = (double) actual / totalClientes;

                Platform.runLater(() -> {
                    barraProgresoClientes.setProgress(progreso);
                    lblPorcentajeClientes.setText(actual + "%");
                    lblEstadoClientes.setText("Cliente " + actual + " de " + totalClientes + " procesado");
                });
            }

            if (!Thread.currentThread().isInterrupted()) {
                Platform.runLater(() -> {
                    lblEstadoClientes.setText("Clientes: Finalizado (100/100)");
                    habilitarBotonesClientes();
                });
            }
        };

        hiloClientes = new Thread(tareaClientes, "Hilo-Clientes");
        hiloClientes.setDaemon(true);
        hiloClientes.start();
    }

    /**
     * Solicita la detención del hilo de clientes.
     */
    @FXML
    public void detenerClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            btnDetenerClientes.setDisable(true);
            lblEstadoClientes.setText("Deteniendo clientes...");
            hiloClientes.interrupt();
        }
    }

    /**
     * Reinicia los componentes del proceso de clientes.
     */
    @FXML
    public void reiniciarClientes() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            return;
        }

        barraProgresoClientes.setProgress(0.0);
        lblPorcentajeClientes.setText("0%");
        lblEstadoClientes.setText("Esperando inicio");
        habilitarBotonesClientes();
    }

    private void habilitarBotonesClientes() {
        btnIniciarClientes.setDisable(false);
        btnReiniciarClientes.setDisable(false);
        btnDetenerClientes.setDisable(true);
    }

    // =========================================================================
    // LÓGICA DEL HILO DE PRODUCTOS (50 registros, 180 ms)
    // =========================================================================

    /**
     * Inicia el procesamiento concurrente de productos en su propio hilo.
     */
    @FXML
    public void iniciarProductos() {
        // Evitar doble ejecución concurrente
        if (hiloProductos != null && hiloProductos.isAlive()) {
            return;
        }

        barraProgresoProductos.setProgress(0.0);
        lblPorcentajeProductos.setText("0%");
        lblEstadoProductos.setText("Procesando productos...");

        btnIniciarProductos.setDisable(true);
        btnReiniciarProductos.setDisable(true);
        btnDetenerProductos.setDisable(false);

        Runnable tareaProductos = () -> {
            final int totalProductos = 50;
            final int retardoMs = 180;

            for (int i = 1; i <= totalProductos; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                try {
                    Thread.sleep(retardoMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Platform.runLater(() -> {
                        lblEstadoProductos.setText("Productos: Detenido");
                        habilitarBotonesProductos();
                    });
                    return;
                }

                final int actual = i;
                final double progreso = (double) actual / totalProductos;
                final int porcentaje = (int) Math.round(progreso * 100);

                Platform.runLater(() -> {
                    barraProgresoProductos.setProgress(progreso);
                    lblPorcentajeProductos.setText(porcentaje + "%");
                    lblEstadoProductos.setText("Producto " + actual + " de " + totalProductos + " procesado");
                });
            }

            if (!Thread.currentThread().isInterrupted()) {
                Platform.runLater(() -> {
                    lblEstadoProductos.setText("Productos: Finalizado (50/50)");
                    habilitarBotonesProductos();
                });
            }
        };

        hiloProductos = new Thread(tareaProductos, "Hilo-Productos");
        hiloProductos.setDaemon(true);
        hiloProductos.start();
    }

    /**
     * Solicita la detención del hilo de productos.
     */
    @FXML
    public void detenerProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            btnDetenerProductos.setDisable(true);
            lblEstadoProductos.setText("Deteniendo productos...");
            hiloProductos.interrupt();
        }
    }

    /**
     * Reinicia los componentes del proceso de productos.
     */
    @FXML
    public void reiniciarProductos() {
        if (hiloProductos != null && hiloProductos.isAlive()) {
            return;
        }

        barraProgresoProductos.setProgress(0.0);
        lblPorcentajeProductos.setText("0%");
        lblEstadoProductos.setText("Esperando inicio");
        habilitarBotonesProductos();
    }

    private void habilitarBotonesProductos() {
        btnIniciarProductos.setDisable(false);
        btnReiniciarProductos.setDisable(false);
        btnDetenerProductos.setDisable(true);
    }

    // =========================================================================
    // ACCIONES GLOBALES
    // =========================================================================

    /**
     * Inicia ambos procesos simultáneamente.
     */
    @FXML
    public void iniciarTodos() {
        iniciarClientes();
        iniciarProductos();
    }

    /**
     * Detiene ambos procesos simultáneamente.
     */
    @FXML
    public void detenerTodos() {
        detenerClientes();
        detenerProductos();
    }

    /**
     * Reinicia ambos procesos si ninguno está en ejecución activa.
     */
    @FXML
    public void reiniciarTodos() {
        reiniciarClientes();
        reiniciarProductos();
    }

    /**
     * Detiene todos los hilos secundarios al cerrar la ventana.
     */
    public void detenerAlCerrar() {
        if (hiloClientes != null && hiloClientes.isAlive()) {
            hiloClientes.interrupt();
        }
        if (hiloProductos != null && hiloProductos.isAlive()) {
            hiloProductos.interrupt();
        }
    }
}
