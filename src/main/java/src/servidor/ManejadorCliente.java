package src.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManejadorCliente extends Thread {

    private final Socket socket;
    private final CoordinadorTareas coordinador;
    private PrintWriter salida;
    private String nombreCliente;

    public ManejadorCliente(Socket socket, CoordinadorTareas coordinador) {
        this.socket = socket;
        this.coordinador = coordinador;
    }

    @Override
    public void run() {
        try {
            salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String linea;
            while ((linea = entrada.readLine()) != null) {
                procesarMensaje(linea);
            }
        } catch (Exception error) {
        } finally {
            cerrarConexion();
        }
    }

    private void procesarMensaje(String linea) {
        if (linea.startsWith("CONECTAR#")) {
            nombreCliente = linea.substring(9);
            coordinador.registrarCliente(this);

            // stop para los clientes
            if (coordinador.listoParaIniciar()) {
                coordinador.iniciarMineria();
            }
            return;
        }
        if (linea.startsWith("SOLUCION#")) {
            long nonce = Long.parseLong(linea.substring(9));
            coordinador.procesarSolucion(nombreCliente, nonce);
            return;
        }

        if (linea.equals("DESCONECTAR")) {
            coordinador.desconectarCliente(this);
        }
    }

    public void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    private void cerrarConexion() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {
        }
    }

    public String getNombreCliente() {
        return nombreCliente;
    }
}