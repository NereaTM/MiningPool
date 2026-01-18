package com.svalero.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import src.utils.Constantes;

import java.util.ArrayList;
import java.util.List;

public class LanzadorUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Servidor
        FXMLLoader servidorLoader = new FXMLLoader(
                getClass().getResource("/com/svalero/ui/servidor-view.fxml")
        );
        Scene servidorScene = new Scene(servidorLoader.load(), 700, 500);
        Stage servidorStage = new Stage();
        servidorStage.setTitle("Servidor de Minería");
        servidorStage.setScene(servidorScene);
        servidorStage.setX(50);
        servidorStage.setY(50);
        servidorStage.show();

        // Esperar un poco para que el servidor se inicialice
        Thread.sleep(500);

        int NUM_CLIENTES = Constantes.NUM_MINEROS;

        List<Stage> clientes = new ArrayList<>();

        for (int i = 1; i <= NUM_CLIENTES; i++) {
            FXMLLoader clienteLoader = new FXMLLoader(
                    getClass().getResource("/com/svalero/ui/cliente-view.fxml")
            );
            Scene clienteScene = new Scene(clienteLoader.load(), 600, 400);
            Stage clienteStage = new Stage();
            clienteStage.setTitle("Cliente " + i);
            clienteStage.setScene(clienteScene);
            clienteStage.setX(800);
            clienteStage.setY(400);
            clienteStage.show();
            clientes.add(clienteStage);
        }

        servidorStage.setOnCloseRequest(event -> {
            clientes.forEach(Stage::close);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}