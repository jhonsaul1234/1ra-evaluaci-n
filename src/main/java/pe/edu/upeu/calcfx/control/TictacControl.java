package pe.edu.upeu.calcfx.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;
import pe.edu.upeu.calcfx.modelo.ResultadoMichi;

import java.util.ArrayList;
import java.util.List;

@Component
public class TictacControl {

    @FXML private Button btn00, btn01, btn02, btn10, btn11, btn12, btn20, btn21, btn22;
    @FXML private Button btnIniciar, btnAnular;
    @FXML private TextField txtJugador1, txtJugador2;
    @FXML private Label lblTurno, lblGanador;
    @FXML private TableView<ResultadoMichi> tablaPuntajes;
    @FXML private TableColumn<ResultadoMichi, String> colNombrePartida, colJugador1, colJugador2, colGanador, colEstado;
    @FXML private TableColumn<ResultadoMichi, Integer> colPunto;

    private Button[][] tablero;
    private boolean turnoX = true;
    private int movimientos = 0;
    private List<ResultadoMichi> resultados = new ArrayList<>();
    private ResultadoMichi partidaActual;

    @FXML
    public void initialize() {
        tablero = new Button[][]{
                {btn00, btn01, btn02},
                {btn10, btn11, btn12},
                {btn20, btn21, btn22}
        };

        configurarTabla();
        btnAnular.setDisable(true);
    }

    private void configurarTabla() {
        colNombrePartida.setCellValueFactory(new PropertyValueFactory<>("nombrePartida"));
        colJugador1.setCellValueFactory(new PropertyValueFactory<>("nombreJugador1"));
        colJugador2.setCellValueFactory(new PropertyValueFactory<>("nombreJugador2"));
        colGanador.setCellValueFactory(new PropertyValueFactory<>("ganador"));
        colPunto.setCellValueFactory(new PropertyValueFactory<>("punto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    @FXML
    void iniciarJuego(ActionEvent event) {
        if (txtJugador1.getText().isEmpty() || txtJugador2.getText().isEmpty()) {
            mostrarAlerta("Por favor, ingrese los nombres de ambos jugadores.");
            return;
        }
        reiniciarTablero();
        btnIniciar.setDisable(true);
        btnAnular.setDisable(false);
        partidaActual = new ResultadoMichi("Partida " + (resultados.size() + 1),
                txtJugador1.getText(),
                txtJugador2.getText(),
                "", 0, "Jugando");
        resultados.add(partidaActual);
        actualizarTabla();
        lblTurno.setText("X");
        lblGanador.setText("");
    }

    @FXML
    void anularJuego(ActionEvent event) {
        if (partidaActual != null) {
            partidaActual.setEstado("Anulado");
            partidaActual.setPunto(0);
            actualizarTabla();
        }
        reiniciarTablero();
        btnIniciar.setDisable(false);
        btnAnular.setDisable(true);
        lblTurno.setText("");
        lblGanador.setText("");
    }

    @FXML
    void accionButon(ActionEvent event) {
        Button b = (Button) event.getSource();
        if (b.getText().isEmpty()) {
            b.setText(turnoX ? "X" : "O");
            movimientos++;
            if (hayGanador(b.getText())) {
                finalizarJuego(turnoX ? txtJugador1.getText() : txtJugador2.getText());
            } else if (movimientos == 9) {
                finalizarJuego("Empate");
            } else {
                turnoX = !turnoX;
                lblTurno.setText(turnoX ? "X" : "O");
            }
        }
    }

    private boolean hayGanador(String jugador) {

        for (int i = 0; i < 3; i++) {
            if (tablero[i][0].getText().equals(jugador) &&
                    tablero[i][1].getText().equals(jugador) &&
                    tablero[i][2].getText().equals(jugador)) return true;
            if (tablero[0][i].getText().equals(jugador) &&
                    tablero[1][i].getText().equals(jugador) &&
                    tablero[2][i].getText().equals(jugador)) return true;
        }
        if (tablero[0][0].getText().equals(jugador) &&
                tablero[1][1].getText().equals(jugador) &&
                tablero[2][2].getText().equals(jugador)) return true;
        if (tablero[0][2].getText().equals(jugador) &&
                tablero[1][1].getText().equals(jugador) &&
                tablero[2][0].getText().equals(jugador)) return true;
        return false;
    }

    private void finalizarJuego(String ganador) {
        if (partidaActual != null) {
            partidaActual.setGanador(ganador);
            partidaActual.setPunto(ganador.equals("Empate") ? 0 : 1);
            partidaActual.setEstado("Finalizado");
            actualizarTabla();
        }
        for (Button[] fila : tablero) {
            for (Button boton : fila) {
                boton.setDisable(true);
            }
        }
        btnIniciar.setDisable(false);
        btnAnular.setDisable(true);
        lblGanador.setText(ganador);
        lblTurno.setText("");
    }

    private void reiniciarTablero() {
        for (Button[] fila : tablero) {
            for (Button boton : fila) {
                boton.setText("");
                boton.setDisable(false);
            }
        }
        turnoX = true;
        movimientos = 0;
    }

    private void actualizarTabla() {
        ObservableList<ResultadoMichi> data = FXCollections.observableArrayList(resultados);
        tablaPuntajes.setItems(data);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}