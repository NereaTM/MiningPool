package com.svalero.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import src.cliente.Cliente;
import src.utils.Constantes;

import java.io.OutputStream;
import java.io.PrintStream;

public class ClienteController {

    @FXML
    private TextField txtNombre;

    @FXML
    private Button btnConectar;

    @FXML
    private TextArea txtLog;

    @FXML
    private ProgressBar progressBar;

    private PrintStream consolaOriginal;
    private static int contadorClientes = 0;

    @FXML
    public void initialize() {
        // Generar nombre automatico
        contadorClientes++;
        txtNombre.setText("Minero" + contadorClientes);

        // Redirigir System para capturar los logs del cliente
        consolaOriginal = System.out;
        redirigirConsola();
    }

    private void redirigirConsola() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                consolaOriginal.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) {
                String texto = new String(b, off, len);
                Platform.runLater(() -> txtLog.appendText(texto));
                consolaOriginal.write(b, off, len);
            }
        };
        System.setOut(new PrintStream(out, true));
    }

    @FXML
    private void onConectar() {
        String nombre = txtNombre.getText().trim();

        btnConectar.setDisable(true);
        txtNombre.setDisable(true);

        new Thread(() -> {
            try {
                Cliente cliente = new Cliente(nombre);

                cliente.setProgresoListener(progreso ->
                        Platform.runLater(() -> // lanzar desde un hilo secundario
                                progressBar.setProgress(progreso)
                        )
                );

                cliente.ejecutar(Constantes.HOST, Constantes.PUERTO);

            } catch (Exception e) {
                Platform.runLater(() ->
                        txtLog.appendText("ERROR: " + e.getMessage() + "\n")
                );
            }

            Platform.runLater(() -> {
                btnConectar.setDisable(false);
                txtNombre.setDisable(false);
            });
        }).start();
    }
}