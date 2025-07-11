
    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uisil.proyecto_juego_cartas.logic;

import com.uisil.proyecto_juego_cartas.model.Carta;
import com.uisil.proyecto_juego_cartas.model.CartaNormal;
import com.uisil.proyecto_juego_cartas.model.CartaBonus;
import com.uisil.proyecto_juego_cartas.model.CartaPenalizacion;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import com.uisil.proyecto_juego_cartas.controllers.MainController;

import java.util.*;

public class Tablero {

    private Dificultad dificultad;
    private Juego juego;
    private GridPane tablero;
    private List<Carta> cartas;
    private List<Image> imagenesCartas = new ArrayList<>();
    private Image imagenReverso = new Image(getClass().getResource("/com/uisil/proyecto_juego_cartas/img/Signo_Interrogacion.png").toExternalForm());

    private final List<Button> botonesSeleccionados = new ArrayList<>();
    private final List<Carta> cartasSeleccionadas = new ArrayList<>();
    private boolean cartasHabilitadas = true;
    private int movimientos = 0;
    private int contadorBonus = 0;
    private int contadorPenalizaciones = 0;
    private boolean esperando = false;
    private int puntos =0;
    private MainController mainController;
    private Map<Carta, Button> cartaBotonMap = new HashMap<>();
    private List<Movimiento> movimientosRealizados = new ArrayList<>();
    private Repeticion ultimaRepeticion;
    private Carta[][] estructuraCartas;
    private int indiceActual = 0;

    public Tablero(Dificultad dificultad, Juego juego, MainController mainController) {
        this.dificultad = dificultad;
        this.juego = juego;
        this.mainController = mainController;
        this.cartas = generarCartas();
        this.tablero = construirTablero();
        this.juego.setTablero(this);
    }

    public GridPane getTablero() {
        return tablero;
    }

    public List<Carta> getCartas() {
        return cartas;
    }
    public List<Movimiento> getMovimientosRealizados() {
    return movimientosRealizados;
}

    private List<Carta> generarCartas() {
        int totalCartas = dificultad.filas * dificultad.columnas;
        List<Carta> lista = new ArrayList<>();
        int paresNormales = (totalCartas / 2) - 2;

        for (int i = 0; i < paresNormales; i++) {
            Carta c1 = new CartaNormal(i);
            Carta c2 = new CartaNormal(i);
            lista.add(c1);
            lista.add(c2);
            String ruta = ((CartaNormal) c1).getImagenRuta();
            Image img = new Image(getClass().getResource(ruta).toExternalForm());
            imagenesCartas.add(img);
        }

        List<CartaBonus.TipoBonus> bonusDisponibles;
        List<CartaPenalizacion.TipoPenalizacion> penalDisponibles;
        switch (dificultad) {
            case FACIL:
                bonusDisponibles = Arrays.asList(CartaBonus.TipoBonus.MAS_CINCO_SEG);
                penalDisponibles = Arrays.asList(CartaPenalizacion.TipoPenalizacion.VER_UNA_CARTA);
                break;
            case MEDIO:
                bonusDisponibles = Arrays.asList(CartaBonus.TipoBonus.MAS_CINCO_SEG, CartaBonus.TipoBonus.PUNTOS_DOBLES);
                penalDisponibles = Arrays.asList(CartaPenalizacion.TipoPenalizacion.MENOS_DIEZ_SEG, CartaPenalizacion.TipoPenalizacion.VER_UNA_CARTA, CartaPenalizacion.TipoPenalizacion.MENOS_UN_PUNTO);
                break;
            case DIFICIL:
                bonusDisponibles = Arrays.asList(CartaBonus.TipoBonus.MAS_CINCO_SEG, CartaBonus.TipoBonus.MOSTRAR_PAREJA, CartaBonus.TipoBonus.MOSTRAR_TRESPAREJAS);
                penalDisponibles = Arrays.asList(CartaPenalizacion.TipoPenalizacion.MENOS_TREINTA_SEG, CartaPenalizacion.TipoPenalizacion.MEZCLAR_CARTAS);
                break;
            default:
                bonusDisponibles = Arrays.asList(CartaBonus.TipoBonus.MAS_CINCO_SEG);
                penalDisponibles = Arrays.asList(CartaPenalizacion.TipoPenalizacion.MENOS_CINCO_SEG);
        }

        Collections.shuffle(bonusDisponibles);
        Collections.shuffle(penalDisponibles);
        CartaBonus bonus1 = new CartaBonus(100, juego, bonusDisponibles.get(0));
        CartaBonus bonus2 = new CartaBonus(100, juego, bonusDisponibles.size() > 1 ? bonusDisponibles.get(1) : bonusDisponibles.get(0));
        lista.add(bonus1);
        lista.add(bonus2);
        CartaPenalizacion penal1 = new CartaPenalizacion(200, juego, penalDisponibles.get(0));
        CartaPenalizacion penal2 = new CartaPenalizacion(200, juego, penalDisponibles.size() > 1 ? penalDisponibles.get(1) : penalDisponibles.get(0));
        lista.add(penal1);
        lista.add(penal2);
        Collections.shuffle(lista);
        return lista;
    }

    private GridPane construirTablero() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(1);
    grid.setVgap(1);

    for (int i = 0; i < dificultad.columnas; i++) {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(cc);
    }

    for (int i = 0; i < dificultad.filas; i++) {
        RowConstraints rc = new RowConstraints();
        rc.setVgrow(Priority.ALWAYS);
        grid.getRowConstraints().add(rc);
    }

    // ✅ Inicializar estructuraCartas correctamente
    estructuraCartas = new Carta[dificultad.filas][dificultad.columnas];

    estructuraCartas = new Carta[dificultad.filas][dificultad.columnas]; // 👈 INICIALIZA

    int index = 0;
    for (int fila = 0; fila < dificultad.filas; fila++) {
        for (int col = 0; col < dificultad.columnas; col++) {
        Carta carta = cartas.get(index++);
        carta.setPosicion(fila, col);
        estructuraCartas[fila][col] = carta; // 👈 LLENA la estructura
        StackPane celda = crearCeldaResponsive(carta);
        grid.add(celda, col, fila);
    }
}

    return grid;
}
    public void reconstruirDesdeEstado(PartidaGuardada partida) {
    List<CartaEstado> estados = partida.getCartas();
    for (CartaEstado estado : estados) {
        for (Carta carta : cartas) {
            if (carta.getId() == estado.getId()) {
                carta.setColocada(estado.isColocada());
                carta.setBocaArriba(estado.isBocaArriba());

                // 🔁 Mostrar visualmente cómo quedó la carta
                voltearVisualmenteCarta(carta, estado.isBocaArriba());

                // 🔒 Si está colocada, tal vez deberías desactivar su botón
                if (estado.isColocada()) {
                    Button boton = cartaBotonMap.get(carta);
                    if (boton != null) {
                        boton.setDisable(true);
                    }
                }
            }
        }
    }

    this.movimientos = partida.getMovimientos();
    this.contadorBonus = partida.getContadorBonus();
    this.contadorPenalizaciones = partida.getContadorPenalizaciones();
    this.puntos = partida.getPuntos(); // Si guardaste esto

    mainController.actualizarPuntaje(puntos); // Asegúrate de mostrar puntos correctamente
}
    public void reconstruirDesdeRepeticion(Repeticion repeticion) {
    this.movimientosRealizados = new ArrayList<>(repeticion.getMovimientos());
    this.estructuraCartas = repeticion.getEstructuraFinal();
    this.cartas = new ArrayList<>();
    
    // Reconstruir lista de cartas desde la estructura
    for (int fila = 0; fila < dificultad.filas; fila++) {
        for (int col = 0; col < dificultad.columnas; col++) {
            cartas.add(estructuraCartas[fila][col]);
        }
    }
    
    // Reconstruir el tablero visual
    cartaBotonMap.clear();
    tablero.getChildren().clear();
    
    // Reconfigurar constraints del grid
    tablero.getColumnConstraints().clear();
    tablero.getRowConstraints().clear();
    
    for (int i = 0; i < dificultad.columnas; i++) {
        ColumnConstraints cc = new ColumnConstraints();
        cc.setHgrow(Priority.ALWAYS);
        tablero.getColumnConstraints().add(cc);
    }
    
    for (int i = 0; i < dificultad.filas; i++) {
        RowConstraints rc = new RowConstraints();
        rc.setVgrow(Priority.ALWAYS);
        tablero.getRowConstraints().add(rc);
    }
    
    // Añadir las celdas
    for (int fila = 0; fila < dificultad.filas; fila++) {
        for (int col = 0; col < dificultad.columnas; col++) {
            Carta carta = estructuraCartas[fila][col];
            StackPane celda = crearCeldaParaRepeticion(carta);
            tablero.add(celda, col, fila);
        }
    }
}
 public void reproducirRepeticionVisual() {
    if (movimientosRealizados == null || movimientosRealizados.isEmpty()) return;
    cartasHabilitadas = false; // Deshabilita interacción durante la repetición
    reproducirSiguienteMovimiento(); // Inicia desde el primer movimiento
}



    public StackPane crearCeldaResponsive(Carta carta) {
        StackPane contenedor = new StackPane();

        Image imagenCarta;

        if (carta instanceof CartaBonus) {
            imagenCarta = new Image(getClass().getResource("/com/uisil/proyecto_juego_cartas/img/Carta_Bonus.png").toExternalForm());
        } else if (carta instanceof CartaPenalizacion) {
            imagenCarta = new Image(getClass().getResource("/com/uisil/proyecto_juego_cartas/img/Carta_Penalizacion.png").toExternalForm());
        } else {
            imagenCarta = imagenesCartas.get(carta.getId());
        }

        String imagenReversoURL = imagenReverso.getUrl();
        String imagenCartaURL = imagenCarta.getUrl();

        Button boton = new Button();
        boton.setPrefSize(180, 180);
        boton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cartaBotonMap.put(carta, boton);

        boton.setStyle("-fx-background-image: url('" + imagenReversoURL + "'); " +
               "-fx-background-size: 80% 80%; " +
               "-fx-background-repeat: no-repeat; " +
               "-fx-background-position: center center; " +
               "-fx-border-color: black;");

        boton.setOnAction(e -> {
        if (!cartasHabilitadas || esperando || carta.isBocaArriba() || carta.isColocada()) return;

        carta.setBocaArriba(true);
        boton.setStyle("-fx-background-image: url('" + imagenCartaURL + "'); " +
           "-fx-background-size: 80% 80%; " +
           "-fx-background-repeat: no-repeat; " +
           "-fx-background-position: center center; " +
           "-fx-border-color: black;");
        cartasSeleccionadas.add(carta);
        botonesSeleccionados.add(boton);

        if (cartasSeleccionadas.size() == 2) {
            movimientos++;
            Carta c1 = cartasSeleccionadas.get(0);
            Carta c2 = cartasSeleccionadas.get(1);

            esperando = true;

            if (c1.getId() == c2.getId()) {
                // Emparejadas: marcar como colocadas
                c1.colocar();
                c2.colocar();
                if (juego.isPuntosDoblesActivos()) {
                puntos += 20;
                juego.desactivarPuntosDobles(); // Aplica solo una vez
}               else {
                puntos += 10;
                }  // o la cantidad que desees por match
                mainController.actualizarPuntaje(puntos);  // Mostrarlo en pantalla

                // Si son cartas Bonus o Penalización, aplicar efecto
                if (c1 instanceof CartaBonus) {
                CartaBonus bonus = (CartaBonus) c1;
                bonus.activarBonus();
                contadorBonus++;
                //registrarEventoEnArchivo("BONUS", bonus.getTipo().name());
                mostrarMensajeBonus(bonus.getTipo().name());
                } else if (c1 instanceof CartaPenalizacion) {
                CartaPenalizacion penal = (CartaPenalizacion) c1;
                penal.activarPenalizacion();
                contadorPenalizaciones++;
                mostrarMensajePenalizacion(penal.getTipo().name());
                }
                //registrarEventoEnArchivo("PENALIZACIÓN", penal.getTipo().name());
                
                movimientosRealizados.add(new Movimiento(
                movimientosRealizados.size() + 1,
                c1.getFila(), c1.getColumna(),
                c2.getFila(), c2.getColumna(),
                true // si se emparejaron
                        ));

                cartasSeleccionadas.clear();
                botonesSeleccionados.clear();
                esperando = false;
            } else {
                // No hacen match: regresarlas a ocultas
                PauseTransition pausa = new PauseTransition(Duration.seconds(0.5));
                pausa.setOnFinished(ev -> {
                    c1.setBocaArriba(false);
                    c2.setBocaArriba(false);

                    for (Button b : botonesSeleccionados) {
                        b.setStyle("-fx-background-image: url('" + imagenReversoURL + "'); " +
                            "-fx-background-size: 80% 80%; " +
                            "-fx-background-repeat: no-repeat; " +
                            "-fx-background-position: center center; " +
                            "-fx-border-color: black;");
                    }

                    cartasSeleccionadas.clear();
                    botonesSeleccionados.clear();
                    esperando = false;
                });
                pausa.play();
            }
        }
    });

        contenedor.getChildren().add(boton);
        StackPane.setAlignment(boton, Pos.CENTER);
        StackPane.setMargin(boton, new Insets(5));

        return contenedor;
        }
    
   public StackPane crearCeldaParaRepeticion(Carta carta) {
    StackPane contenedor = new StackPane();
    contenedor.setAlignment(Pos.CENTER);
    
    // Obtener imágenes igual que en crearCeldaResponsive()
    Image imagenCarta;
    if (carta instanceof CartaBonus) {
        imagenCarta = new Image(getClass().getResource("/com/uisil/proyecto_juego_cartas/img/Carta_Bonus.png").toExternalForm());
    } else if (carta instanceof CartaPenalizacion) {
        imagenCarta = new Image(getClass().getResource("/com/uisil/proyecto_juego_cartas/img/Carta_Penalizacion.png").toExternalForm());
    } else {
        imagenCarta = imagenesCartas.get(carta.getId());
    }
    
    String imagenReversoURL = imagenReverso.getUrl();
    String imagenCartaURL = imagenCarta.getUrl();
    
    Button boton = new Button();
    boton.setDisable(true); // Deshabilitado para interacción
    boton.setPrefSize(180, 180);
    boton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    
    // Estado visual inicial
    String imagenMostrar = carta.isBocaArriba() || carta.isColocada() ? imagenCartaURL : imagenReversoURL;
    boton.setStyle("-fx-background-image: url('" + imagenMostrar + "'); " +
                 "-fx-background-size: 80% 80%; " +
                 "-fx-background-repeat: no-repeat; " +
                 "-fx-background-position: center center; " +
                 "-fx-border-color: black; " +
                 "-fx-opacity: " + (carta.isColocada() ? "0.7" : "1.0") + ";");
    
    cartaBotonMap.put(carta, boton);
    contenedor.getChildren().add(boton);
    StackPane.setAlignment(boton, Pos.CENTER);
    StackPane.setMargin(boton, new Insets(5));
    
    return contenedor;
}
    
    public Dificultad getDificultad() {
    return dificultad;
}
    public void iniciarRepeticionDesdePartida(List<Movimiento> movimientos) {
    if (movimientos == null || movimientos.isEmpty()) return;

    cartasHabilitadas = false;
    this.movimientosRealizados = movimientos;

    reproducirSiguienteMovimiento();
}

private void reproducirSiguienteMovimiento() {
    if (indiceActual >= movimientosRealizados.size()) {
        mostrarMensajeBonus("Repetición completada");
        return;
    }

    Movimiento mov = movimientosRealizados.get(indiceActual);
    Carta c1 = estructuraCartas[mov.getFila1()][mov.getCol1()];
    Carta c2 = estructuraCartas[mov.getFila2()][mov.getCol2()];

    // Voltear primera carta
    c1.setBocaArriba(true);
    voltearVisualmenteCarta(c1, true);

    // Pausa antes de voltear segunda carta
    PauseTransition pausaSegundaCarta = new PauseTransition(Duration.seconds(0.7));
    pausaSegundaCarta.setOnFinished(e -> {
        c2.setBocaArriba(true);
        voltearVisualmenteCarta(c2, true);

        // Pausa antes de manejar el resultado
        PauseTransition pausaResultado = new PauseTransition(Duration.seconds(1.0));
        pausaResultado.setOnFinished(ev -> {
            if (mov.fueEmparejado()) {
                // Si fue match, marcarlas como colocadas
                c1.colocar();
                c2.colocar();
                voltearVisualmenteCarta(c1, true);
                voltearVisualmenteCarta(c2, true);
            } else {
                // Si no fue match, voltearlas de nuevo
                c1.setBocaArriba(false);
                c2.setBocaArriba(false);
                voltearVisualmenteCarta(c1, false);
                voltearVisualmenteCarta(c2, false);
            }

            // Pasar al siguiente movimiento
            indiceActual++;
            PauseTransition pausaSiguiente = new PauseTransition(Duration.seconds(0.5));
            pausaSiguiente.setOnFinished(event -> reproducirSiguienteMovimiento());
            pausaSiguiente.play();
        });
        pausaResultado.play();
    });
    pausaSegundaCarta.play();
}
    

    public MainController getMainController() {
        return mainController;
    }

    public void mostrarMensajeBonus(String mensaje) {
        if (mainController != null) {
            mainController.mostrarMensaje("BONUS: " + mensaje);
        }
    }
    public void mostrarMensajePenalizacion(String mensaje) {
        if (mainController != null) {
            mainController.mostrarMensaje("PENALIZACIÓN: " + mensaje);
        }
    }

    public void voltearVisualmenteCarta(Carta carta, boolean bocaArriba) {
        Button boton = cartaBotonMap.get(carta);
        if (boton == null) return;
        Image imagenCarta;
        if (carta instanceof com.uisil.proyecto_juego_cartas.model.CartaBonus) {
            imagenCarta = new Image(getClass().getResource("/com/uisil/proyecto_juego_cartas/img/Carta_Bonus.png").toExternalForm());
        } else if (carta instanceof com.uisil.proyecto_juego_cartas.model.CartaPenalizacion) {
            imagenCarta = new Image(getClass().getResource("/com/uisil/proyecto_juego_cartas/img/Carta_Penalizacion.png").toExternalForm());
        } else {
            imagenCarta = imagenesCartas.get(carta.getId());
        }
        String imagenReversoURL = imagenReverso.getUrl();
        String imagenCartaURL = imagenCarta.getUrl();
        if (bocaArriba) {
            boton.setStyle("-fx-background-image: url('" + imagenCartaURL + "'); " +
                    "-fx-background-size: 80% 80%; " +
                    "-fx-background-repeat: no-repeat; " +
                    "-fx-background-position: center center; " +
                    "-fx-border-color: black;");
        } else {
            boton.setStyle("-fx-background-image: url('" + imagenReversoURL + "'); " +
                    "-fx-background-size: 80% 80%; " +
                    "-fx-background-repeat: no-repeat; " +
                    "-fx-background-position: center center; " +
                    "-fx-border-color: black;");
        }
    }public int getMovimientos() {
    return movimientos;
}

public int getContadorBonus() {
    return contadorBonus;
}

public int getContadorPenalizaciones() {
    return contadorPenalizaciones;
}
    

public Map<Carta, Button> getCartaBotonMap() {
    return cartaBotonMap;
    }
    public void setCartasHabilitadas(boolean estado) {
    this.cartasHabilitadas = estado;
    
}
    public int getPuntos() {
    return puntos;
}
public void mostrarParejaTemporal() {
    System.out.println("🔍 Activando mostrarParejaTemporal()");
    List<Carta> disponibles = new ArrayList<>(juego.getCartasNoEmparejadas());
    System.out.println("Cartas no emparejadas disponibles: " + disponibles.size());
    
    if (disponibles.size() < 2) return;

    Carta c1 = null, c2 = null;
    Collections.shuffle(disponibles);
    for (int i = 0; i < disponibles.size(); i++) {
        for (int j = i + 1; j < disponibles.size(); j++) {
            if (disponibles.get(i).getId() == disponibles.get(j).getId()) {
                c1 = disponibles.get(i);
                c2 = disponibles.get(j);
                break;
            }
        }
        if (c1 != null) break;
    }

    if (c1 == null || c2 == null) {
        System.out.println("❌    No se encontró pareja.");
        return;
    }

    System.out.println("🎴 Pareja seleccionada: " + c1.getId());

    c1.setBocaArriba(true);
    c2.setBocaArriba(true);
    voltearVisualmenteCarta(c1, true);
    voltearVisualmenteCarta(c2, true);
    mostrarMensajeBonus("¡Se reveló una pareja al azar!");

    Carta finalC1 = c1;
    Carta finalC2 = c2;

    PauseTransition pausa = new PauseTransition(Duration.seconds(1.5));
    pausa.setOnFinished(e -> {
        System.out.println("⏳ Ocultando pareja...");
        javafx.application.Platform.runLater(() -> {
            finalC1.setBocaArriba(false);
            finalC2.setBocaArriba(false);
            voltearVisualmenteCarta(finalC1, false);
            voltearVisualmenteCarta(finalC2, false);
        });
    });
    pausa.play();
}
public Repeticion getUltimaRepeticion() {
    return ultimaRepeticion;
}

public void setUltimaRepeticion(Repeticion rep) {
    this.ultimaRepeticion = rep;
}
public Carta[][] getEstructuraCartas() {
    return estructuraCartas;
}
     

public void mezclarCartasNoEmparejadasVisual(List<Carta> noEmparejadas) {
        // Mezclar la lista
        Collections.shuffle(noEmparejadas);
        // Quitar todas las cartas no emparejadas del grid
        for (Carta carta : noEmparejadas) {
            Button boton = cartaBotonMap.get(carta);
            if (boton != null) {
                StackPane celda = (StackPane) boton.getParent();
                if (celda != null) {
                    GridPane grid = (GridPane) celda.getParent();
                    if (grid != null) {
                        grid.getChildren().remove(celda);
                    }
                }
            }
        }
        // Volver a agregarlas en nuevas posiciones
        int index = 0;
        for (int fila = 0; fila < dificultad.filas; fila++) {
            for (int col = 0; col < dificultad.columnas; col++) {
                if (index >= cartas.size()) break;
                Carta carta = cartas.get(index);
                if (!carta.isColocada() && noEmparejadas.contains(carta)) {
                    Button boton = cartaBotonMap.get(carta);
                    if (boton != null) {
                        StackPane celda = (StackPane) boton.getParent();
                        if (celda != null) {
                            tablero.add(celda, col, fila);
                        }
                    }
                }
                index++;
            }
        }
    }

}    
    

    