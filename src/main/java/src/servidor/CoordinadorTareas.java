package src.servidor;

import src.utils.Constantes;
import src.utils.UtilidadesHash;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoordinadorTareas {

    private final List<ManejadorCliente> clientes = new ArrayList<>();

    // Datos del bloque a minar
    private String datosBloque;
    private int dificultad;
    // Control de estado
    private boolean resuelto = false;
    private boolean mineriaIniciada = false;
    private long siguienteInicio = 0;
    // Resultado
    private String hashResuelto;
    private long nonceResuelto;

    public synchronized void configurarTrabajo(String datos, int dificultad) {
        this.datosBloque = datos;
        this.dificultad = dificultad;
    }

    public synchronized void registrarCliente(ManejadorCliente cliente) {
        clientes.add(cliente);
    }

    public synchronized void desconectarCliente(ManejadorCliente cliente) {
        clientes.remove(cliente);
    }

    public synchronized boolean listoParaIniciar() {
        if (clientes.size() == Constantes.NUM_MINEROS && !mineriaIniciada) {
            mineriaIniciada = true;
            return true;
        }
        return false;
    }

    public synchronized void iniciarMineria() {
        for (ManejadorCliente cliente : clientes) {
            long inicio = siguienteInicio;
            long fin = inicio + Constantes.TAMANO_RANGO;
            siguienteInicio = fin + 1;

            System.out.println("server > " + cliente.getNombreCliente()
                    + ": trabajo " + inicio + "-" + fin + " " + datosBloque);

            String mensaje = "TRABAJO#" + 1 + "|" + datosBloque + "|" + inicio + "|" + fin + "|" + dificultad;
            cliente.enviarMensaje(mensaje);
        }
    }

    public synchronized void procesarSolucion(String nombreCliente, long nonce) {
        if (resuelto) return;

        System.out.println("[Servidor] Bloque minado exitosamente ");

        if (validarSolucion(nonce)) {
            resuelto = true;
            nonceResuelto = nonce;
            hashResuelto = UtilidadesHash.sha256(datosBloque + nonce);

            for (ManejadorCliente cliente : clientes) {
                System.out.println("server: adios " + cliente.getNombreCliente());
                cliente.enviarMensaje("FIN");
            }
        }
    }

    private boolean validarSolucion(long nonce) {
        String prefijo = "0".repeat(dificultad);
        String hash = UtilidadesHash.sha256(datosBloque + nonce);
        return hash.startsWith(prefijo);
    }

    public static String generarBloqueTransacciones() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        // aleatorias
        for (int i = 0; i < Constantes.NUM_TRANSACCIONES; i++) {
            String origen = "CTA" + (1000 + random.nextInt(9000));
            String destino = "CTA" + (1000 + random.nextInt(9000));
            int cantidad = 1 + random.nextInt(500);

            sb.append(origen).append("->").append(destino).append(":").append(cantidad);

            if (i < Constantes.NUM_TRANSACCIONES - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    // para enviarselos al listener
    public synchronized boolean estaResuelto() {
        return resuelto;
    }

    public synchronized String getHashResuelto() {
        return hashResuelto;
    }

    public synchronized long getNonceResuelto() {
        return nonceResuelto;
    }

    public synchronized String getDatosBloque() {
        return datosBloque;
    }

    public synchronized int getDificultad() {
        return dificultad;
    }
}