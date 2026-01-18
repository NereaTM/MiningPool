package src.servidor;

import src.utils.Constantes;

import java.net.ServerSocket;
import java.net.Socket;

public class ServidorMineria {

    private final CoordinadorTareas coordinador;
    private SolucionListener listener;

    // Interface para notificar al controlador
    public interface SolucionListener {
        void onSolucionEncontrada(String hash, String nonce, String datosBloque, int dificultad);
        void onClienteConectado(String mensaje);
    }

    //  compatibilidad con UI
    public ServidorMineria(int dificultad, SolucionListener listener) {
        this.coordinador = new CoordinadorTareas();
        this.listener = listener;

        // Generar bloque de transacciones aleatorias
        String bloqueTransacciones = CoordinadorTareas.generarBloqueTransacciones();
        coordinador.configurarTrabajo(bloqueTransacciones, dificultad);
    }

    public void iniciar() {
        try {
            ServerSocket serverSocket = new ServerSocket(Constantes.PUERTO);
            serverSocket.setSoTimeout(500); // Timeout para poder verificar si ya terminó

            System.out.println("[Servidor] Escuchando en puerto " + Constantes.PUERTO);

            while (!coordinador.estaResuelto()) {
                try {
                    Socket socketCliente = serverSocket.accept();
                    System.out.println("[Servidor] Nuevo cliente conectado");

                    if (listener != null) {
                        listener.onClienteConectado("Nueva conexión");
                    }
                    ManejadorCliente manejador = new ManejadorCliente(socketCliente, coordinador);
                    manejador.start();

                } catch (Exception error) {
                }
            }

            // Solución encontrada
            if (coordinador.estaResuelto()) {

                if (listener != null) {
                    listener.onSolucionEncontrada(
                            coordinador.getHashResuelto(),
                            String.valueOf(coordinador.getNonceResuelto()),
                            coordinador.getDatosBloque(),
                            coordinador.getDificultad()
                    );
                    serverSocket.close();
                    System.out.println("[SERVIDOR] Cerrado");
                }
            }

        } catch (Exception e) {
            System.err.println("[SERVIDOR] Error: " + e.getMessage());

        }
    }
}