package com.svalero.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import src.servidor.ServidorMineria;
import src.utils.Constantes;

public class ServidorController implements ServidorMineria.SolucionListener {

    @FXML
    private TextField txtDificultad;

    @FXML
    private Button btnIniciar;

    @FXML
    private TextArea txtLog;

    @FXML
    private void onIniciar() {
        int dificultad;

        try {
            dificultad = Integer.parseInt(txtDificultad.getText());

            if (dificultad < 2) {
                agregarLog("Error dificultad debe ser mayor a 1");
                return;
            }
        } catch (NumberFormatException e) {
            agregarLog("Error dificultad. Usa del 2-10 ");
            return;
        }

        // desactivar botoncillos fxml
        btnIniciar.setDisable(true);
        txtDificultad.setDisable(true);

        new Thread(() -> {
            try {
                agregarLog("INICIANDO SERVIDOR");
                agregarLog("Puerto: " + Constantes.PUERTO);
                agregarLog("Dificultad: " + dificultad);
                agregarLog("Transacciones: " + Constantes.NUM_TRANSACCIONES);

                // Crear servidor
                ServidorMineria servidor = new ServidorMineria(dificultad, this);
                agregarLog("Servidor listo. Esperando conexiones de clientes");
                servidor.iniciar();

            } catch (Exception e) {
                agregarLog("ERROR: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void onClienteConectado(String mensaje) {
        agregarLog(mensaje);
    }

    @Override
    public void onSolucionEncontrada(String hash, String nonce, String datosBloque, int dificultad) {
        Platform.runLater(() -> {
            agregarLog("\nSOLUCION ENCONTRADA");
            agregarLog("Hash: " + hash);
            agregarLog("Nonce: " + nonce);
            agregarLog("Dificultad: " + dificultad);
            agregarLog("Servidor finalizado");
        });
    }

    public void agregarLog(String mensaje) {
        Platform.runLater(() -> {
            txtLog.appendText(mensaje + "\n");
            txtLog.setScrollTop(Double.MAX_VALUE);
        });
    }

}