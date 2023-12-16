
package compilador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PythonRunner {
    public static String ejecutarScriptPython(String rutaScript) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("python", rutaScript);
        pb.redirectErrorStream(true);
        Process proceso = pb.start();

        // Capturar la salida del proceso
        InputStream inputStream = proceso.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder salida = new StringBuilder();
        String linea;
        while ((linea = br.readLine()) != null) {
            salida.append(linea).append("\n");
        }

        // Esperar a que el proceso termine
        try {
            proceso.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cerrar los recursos
        br.close();
        inputStream.close();

        // Devolver la salida como una cadena
        return salida.toString();
    }
}


