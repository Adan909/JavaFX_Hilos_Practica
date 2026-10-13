# Práctica de Concurrencia y Manejo de Hilos en JavaFX

**Universidad Americana (UAM)**  
**Facultad de Ingeniería y Arquitectura**  
**Proyecto:** PracticaHilosJavaFX  
**Paquete base:** `ni.edu.uam.practicahilos`

---

## 📋 Descripción del Proyecto

Este proyecto implementa una solución de software interactiva desarrollada con **Java** y **JavaFX**, orientada al aprendizaje y dominio de la programación concurrente y la gestión no bloqueante de interfaces gráficas de usuario (GUI).

### Caso de Estudio: NicaData Solutions S.A.
La empresa *NicaData Solutions S.A.* requería una aplicación de escritorio capaz de simular la carga y procesamiento de lotes masivos de registros de clientes (100 registros) y productos (50 registros) sin que la interfaz gráfica sufra congelamientos, garantizando que el usuario pueda mover la ventana, cancelar procesos en cualquier momento o reiniciar la tarea de forma reactiva y fluida.

El proyecto está estructurado en dos secciones principales:
1. **Parte 1 (Práctica Base):** Procesador de 100 registros con barra de progreso, porcentaje dinámico, estados visuales y control de inicio, detención y reinicio. Incluye también una demostración pedagógica del anti-patrón de bloqueo.
2. **Parte 2 (Reto Final):** Monitor de procesamiento empresarial con ejecución concurrente e independiente de dos hilos secundarios (Clientes y Productos) con diferentes cargas y tiempos de respuesta, controles independientes y sincronización limpia.

---

## 🛠️ Requisitos del Entorno

- **Sistema Operativo:** Windows, macOS o Linux
- **Java Development Kit (JDK):** Versión 17 o superior (OpenJDK / Oracle JDK)
- **Apache Maven:** Versión 3.8.0 o superior (o utilizar el wrapper incluido `./mvnw`)
- **JavaFX SDK:** Versión 21.0.6 (administrado automáticamente mediante Maven)
- **IDE Recomendado:** IntelliJ IDEA 2023+ (Community o Ultimate Edition)

---

## 🚀 Instrucciones de Ejecución

### Opción 1: Ejecutar desde IntelliJ IDEA
1. Abrir IntelliJ IDEA.
2. Seleccionar **File -> Open...** y elegir la carpeta raíz `JavaFX_Hilos_Practica`.
3. Esperar a que Maven sincronice las dependencias (`pom.xml`).
4. Ubicar la clase `ni.edu.uam.practicahilos.MainApp` o `ni.edu.uam.practicahilos.Launcher`.
5. Hacer clic derecho sobre el archivo y seleccionar **Run 'MainApp.main()'**.

### Opción 2: Ejecutar mediante Maven Wrapper (Línea de Comandos)
Desde la terminal en el directorio raíz del proyecto:

```bash
# En Linux / macOS:
./mvnw clean compile javafx:run

# En Windows (CMD / PowerShell):
mvnw.cmd clean compile javafx:run
```

### Opción 3: Ejecutar pruebas unitarias
```bash
# Ejecución de pruebas JUnit 5 sobre la lógica de concurrencia e interrupción:
./mvnw test
```

---

## 📂 Estructura del Proyecto

```
JavaFX_Hilos_Practica/
├── .gitignore                                 # Configuración de exclusión para Git / Maven / IntelliJ
├── pom.xml                                    # Archivo de configuración Maven con dependencias y plugins
├── README.md                                  # Documentación completa del proyecto
├── mvnw / mvnw.cmd                            # Maven Wrapper para compilación portátil
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── module-info.java               # Configuración modular de JavaFX
    │   │   └── ni/edu/uam/practicahilos/
    │   │       ├── Launcher.java              # Clase de arranque auxiliar
    │   │       ├── MainApp.java               # Entrada principal JavaFX y control de ciclo de vida
    │   │       ├── ProcesadorController.java  # Controlador de la práctica base (Caso NicaData)
    │   │       └── MonitorController.java     # Controlador del reto final (Monitor empresarial)
    │   └── resources/
    │       └── ni/edu/uam/practicahilos/
    │           ├── procesador-view.fxml       # Vista FXML de la práctica base
    │           └── monitor-view.fxml          # Vista FXML del reto final
    └── test/
        └── java/
            └── ni/edu/uam/practicahilos/
                └── ProcesadorHilosTest.java   # Pruebas automatizadas de concurrencia
```

---

## 🧠 Conceptos Teóricos y Concurrencia Implementada

### 1. `Thread` (Hilo de Ejecución)
Representa una unidad de ejecución independiente dentro de la JVM. En esta aplicación se crean hilos secundarios (`new Thread(tarea, "Nombre-Hilo")`) para aislar el trabajo pesado y los retardos simulados (`Thread.sleep()`), evitando sobrecargar el hilo de eventos. Se marcan como hilos demonio (`setDaemon(true)`) para que finalicen automáticamente si la aplicación se cierra.

### 2. `Runnable`
Interfaz funcional que encapsula la tarea que será ejecutada por el hilo. Se implementa mediante expresiones Lambda:
```java
Runnable tarea = () -> {
    // Código ejecutado concurrentemente en segundo plano
};
```

### 3. `Thread.sleep(long millis)`
Suspende la ejecución del hilo actual durante el tiempo especificado en milisegundos, simulando la latencia de procesamiento de registros en bases de datos o servicios remotos (100 ms para clientes, 180 ms para productos).

### 4. `Thread.interrupt()` / `isInterrupted()` y manejo de `InterruptedException`
Java no permite la detención forzada abrupta de hilos por seguridad e integridad de datos. El mecanismo estándar es la interrupción cooperativa:
- `hilo.interrupt()`: Envía una señal de solicitud de detención al hilo.
- `Thread.currentThread().isInterrupted()`: Verifica periódicamente si se ha solicitado detener la tarea.
- `catch (InterruptedException e)`: Captura el momento exacto en que un hilo dormido en `sleep()` es interrumpido, restablece el estado de interrupción con `Thread.currentThread().interrupt()` y finaliza ordenadamente la rutina.

### 5. `Platform.runLater(Runnable)`
La regla de oro de JavaFX establece que **ningún control visual o nodo del Scene Graph puede ser modificado desde un hilo secundario** (regla de hilo único o *Single-Threaded Rule*). `Platform.runLater()` coloca la actualización gráfica en la cola de eventos del `JavaFX Application Thread`, garantizando que la UI se actualice de forma thread-safe y sin excepciones `IllegalStateException`.

---

## ⚠️ ¿Por qué no debemos ejecutar procesos prolongados dentro del hilo de JavaFX?

El **JavaFX Application Thread** es responsable de:
1. Despachar eventos de entrada de usuario (clics de ratón, pulsaciones de teclas).
2. Calcular la posición y disposición (*layout*) de todos los controles visuales.
3. Procesar las animaciones a 60 cuadros por segundo (FPS).
4. Renderizar y repintar la escena gráfica en la tarjeta de video / pantalla.

### Consecuencias de ejecutar tareas pesadas en este hilo:
- **Congelamiento total de la ventana:** La interfaz gráfica deja de responder inmediatamente; no es posible presionar otros botones, redimensionar o mover la ventana.
- **Ventana bloqueada por el SO:** En Windows o Linux, el sistema operativo marcará la ventana como *"No responde"* (con la típica pantalla blanca o cursor de espera).
- **Imposibilidad de mostrar progreso en tiempo real:** Aunque se modifiquen barras de progreso dentro de un bucle en el hilo principal, los cambios nunca se renderizan en pantalla hasta que el método termine por completo, perdiendo todo sentido de feedback interactivo.

> **Demostración en la aplicación:** Se incluyó el botón **"⚠️ Proceso Bloqueante (Anti-patrón)"** que ejecuta `Thread.sleep(5000)` en el hilo de JavaFX para comprobar visual y empíricamente este fallo común de diseño.

---

## 🧪 Pruebas Realizadas y Matriz de Verificación

| # | Caso de Prueba | Procedimiento | Comportamiento Esperado | Resultado |
|---|----------------|---------------|--------------------------|-----------|
| **1** | **Iniciar y fluidez de UI** | Clic en "Iniciar" y arrastrar/mover la ventana durante el conteo. | La barra incrementa fluidamente; la ventana se mueve sin retraso ni parpadeo. | **Aprobado (OK)** |
| **2** | **Detener antes del 100%** | Iniciar y presionar "Detener" alrededor del 35%. | El proceso se detiene inmediatamente, el estado cambia a "Proceso detenido" y se habilitan Iniciar/Reiniciar. | **Aprobado (OK)** |
| **3** | **Reinicio de proceso** | Clic en "Reiniciar" con la barra detenida o finalizada. | La barra vuelve a 0, porcentaje a "0%", estado a "Esperando inicio". | **Aprobado (OK)** |
| **4** | **Finalización completa** | Permitir que el proceso alcance el 100%. | El progreso llega al 100%, estado muestra "Proceso finalizado" y los botones quedan disponibles. | **Aprobado (OK)** |
| **5** | **Prevención de doble ejecución** | Intentar llamadas recurrentes mediante validación `isAlive()`. | No se generan hilos duplicados ni saltos erráticos en la barra. | **Aprobado (OK)** |
| **6** | **Reto Final (Concurrencia dual)** | Iniciar simultáneamente Clientes (100 reg @ 100ms) y Productos (50 reg @ 180ms). | Ambos hilos avanzan a velocidades diferentes de manera asíncrona y sin interferencias. | **Aprobado (OK)** |
| **7** | **Cierre seguro de ventanas** | Iniciar procesos y cerrar la ventana principal o del monitor. | El evento `setOnCloseRequest` interrumpe todos los hilos vivos de forma limpia. | **Aprobado (OK)** |

---

## 📸 Sección para Capturas de Pantalla y Evidencias

### 1. Práctica Base en Ejecución (Caso NicaData Solutions S.A.)
> *Espacio reservado para insertar captura de la ventana principal procesando registros fluidamente:*
```
[ INSERTAR AQUÍ CAPTURA DE PANTALLA: procesador-view en ejecución ]
```

### 2. Demostración de Detención Controlada (`Thread.interrupt()`)
> *Espacio reservado para insertar captura tras presionar el botón "Detener":*
```
[ INSERTAR AQUÍ CAPTURA DE PANTALLA: procesador-view detenido ]
```

### 3. Reto Final: Monitor de Procesamiento Empresarial (Multihilo)
> *Espacio reservado para insertar captura del monitor con los hilos de Clientes y Productos corriendo en paralelo:*
```
[ INSERTAR AQUÍ CAPTURA DE PANTALLA: monitor-view con ambos hilos activos ]
```

### 4. Evidencia de Pruebas Automatizadas (JUnit 5)
> *Espacio reservado para captura de salida de `./mvnw test`:*
```
[INFO] Running ni.edu.uam.practicahilos.ProcesadorHilosTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.284 s
[INFO] BUILD SUCCESS
```

---

## 📤 Guía Paso a Paso para Subir el Proyecto a GitHub

Siga estos comandos en la terminal desde la carpeta raíz del proyecto para publicarlo en GitHub en el repositorio `JavaFX_Hilos_Practica`:

```bash
# 1. Inicializar el repositorio Git local (si aún no está inicializado)
git init

# 2. Agregar todos los archivos fuente, configuración y documentación al área de preparación (staging)
git add .

# 3. Realizar el primer commit descriptivo
git commit -m "feat: Implementación completa de práctica e hilos concurrentes en JavaFX (Base + Reto Final)"

# 4. Establecer la rama principal como 'main'
git branch -M main

# 5. Vincular el repositorio remoto de GitHub (reemplazar TU_USUARIO por su usuario real)
git remote add origin https://github.com/TU_USUARIO/JavaFX_Hilos_Practica.git

# 6. Subir los cambios a la rama principal de GitHub
git push -u origin main
```

---

## 👨‍💻 Autor y Créditos
- **Institución:** Universidad Americana (UAM)
- **Asignatura:** Programación de Sistemas / Desarrollo de Aplicaciones en Java
- **Tema:** Concurrencia, Multithreading y Gestión de Hilos en Interfaces Gráficas con JavaFX
