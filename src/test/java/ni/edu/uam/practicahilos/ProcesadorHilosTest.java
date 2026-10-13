package ni.edu.uam.practicahilos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para validar la lógica de concurrencia, ciclo de vida de los hilos
 * e interrupción controlada requerida en la práctica.
 */
class ProcesadorHilosTest {

    @Test
    @DisplayName("Debe procesar los 100 registros completamente en un hilo secundario")
    void testProcesamientoCompleto() throws InterruptedException {
        AtomicInteger contador = new AtomicInteger(0);
        AtomicBoolean finalizado = new AtomicBoolean(false);

        Runnable tarea = () -> {
            for (int i = 1; i <= 100; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                contador.incrementAndGet();
            }
            if (!Thread.currentThread().isInterrupted()) {
                finalizado.set(true);
            }
        };

        Thread hilo = new Thread(tarea, "Test-Hilo-Completo");
        hilo.start();
        hilo.join();

        assertEquals(100, contador.get(), "Debe haber procesado exactamente 100 registros");
        assertTrue(finalizado.get(), "La tarea debe marcarse como finalizada");
    }

    @Test
    @DisplayName("Debe interrumpir la ejecución de forma inmediata y limpia ante una señal interrupt()")
    void testInterrupcionHilo() throws InterruptedException {
        AtomicInteger contador = new AtomicInteger(0);
        AtomicBoolean interrumpido = new AtomicBoolean(false);

        Runnable tarea = () -> {
            for (int i = 1; i <= 100; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                try {
                    Thread.sleep(50);
                    contador.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    interrumpido.set(true);
                    return;
                }
            }
        };

        Thread hilo = new Thread(tarea, "Test-Hilo-Interrupcion");
        hilo.start();

        // Esperar un breve momento para que inicie y luego interrumpir
        Thread.sleep(120);
        hilo.interrupt();
        hilo.join(1000);

        assertFalse(hilo.isAlive(), "El hilo debe finalizar tras la interrupción");
        assertTrue(interrumpido.get(), "El hilo debe haber detectado la InterruptedException");
        assertTrue(contador.get() < 100, "No debe haber completado los 100 registros al ser interrumpido");
    }

    @Test
    @DisplayName("Debe manejar dos hilos concurrentes e independientes simultáneamente")
    void testHilosConcurrentesIndependientes() throws InterruptedException {
        AtomicInteger clientes = new AtomicInteger(0);
        AtomicInteger productos = new AtomicInteger(0);

        Thread hiloClientes = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                clientes.incrementAndGet();
            }
        });

        Thread hiloProductos = new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                productos.incrementAndGet();
            }
        });

        hiloClientes.start();
        hiloProductos.start();

        hiloClientes.join();
        hiloProductos.join();

        assertEquals(100, clientes.get(), "Clientes procesados debe ser 100");
        assertEquals(50, productos.get(), "Productos procesados debe ser 50");
    }
}
