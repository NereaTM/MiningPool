package src.cliente;

import src.utils.Constantes;
import src.utils.UtilidadesHash;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Cliente {

    private volatile boolean detener = false;
    private final String nombre;
    private ProgresoListener progresoListener;

    public interface ProgresoListener {
        void onProgreso(double progreso); // valor entre 0.0 y 1.0
    }

    public void setProgresoListener(ProgresoListener listener) {
        this.progresoListener = listener;
    }

    public Cliente(String nombre) {
        this.nombre = nombre;
    }

    public void ejecutar(String host, int puerto) {
        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(host, puerto));

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);

            // LOG 1: Conexion al server
            imprimirCliente("Conectado al servidor");
            salida.println("CONECTAR#" + nombre);

            String linea;
            while ((linea = entrada.readLine()) != null) {

                // LOG 2: recibo el trabajo
                if (linea.startsWith("TRABAJO#")) {
                    // TRABAJO#id|datos|inicio|fin|dificultad
                    String[] p = linea.substring(8).split("\\|");
                    String datos = p[1];
                    long inicio = Long.parseLong(p[2]);
                    long fin = Long.parseLong(p[3]);
                    int dificultad = Integer.parseInt(p[4]);

                    // minar en otro hilo para poder leer FIN al instante y que no me saque 2 sol
                    new Thread(() -> {
                        long sol = minar(datos, inicio, fin, dificultad);

                        // LOG 3: solucion :)
                        if (sol != -1 && !detener) {
                            imprimirCliente("sol " + sol);
                            salida.println("SOLUCION#" + sol);
                        }
                    }).start();
                }

                if (linea.equals("FIN")) {
                    detener = true;
                    break;
                }
            }
            salida.println("DESCONECTAR");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // busca un nonce que genere un hash
    private long minar(String datos, long inicio, long fin, int dificultad) {
        String prefijo = "0".repeat(dificultad);

        for (long nonce = inicio; nonce <= fin && !detener; nonce++) {
            if (nonce % Constantes.TAMANO_RANGO == 0 && progresoListener != null) {
                double progreso = (double)(nonce - inicio) / (double)(fin - inicio);
                progresoListener.onProgreso(progreso);
            }

            String hash = UtilidadesHash.sha256(datos + nonce);
            if (hash.startsWith(prefijo)) {
                // barra al 100%
                if (progresoListener != null) {
                    progresoListener.onProgreso(1.0);
                }
                return nonce;
            }
        }
        return -1;
    }

    private void imprimirCliente(String texto) {
        System.out.println(nombre + ": " + texto);
    }

}
