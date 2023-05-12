import java.io.*;
import java.time.LocalDateTime;

/**
 * Esta clase contiene los métodos usados para generar el fichero Log.
 * Este se guardará en un fichero de formato .txt
 */

public class GeneradorLog {
    FileWriter archivo;
    PrintWriter imprimir;
    
    public GeneradorLog() throws IOException {
        FileWriter guardar = new FileWriter("EvolucionColonia.txt");
        //Creación del archivo .txt
    }
    
    public void imprimir (String mensaje) throws IOException {
        try (FileWriter guardar = new FileWriter("EvolucionColonia.txt", true);
            BufferedWriter escribir = new BufferedWriter(guardar);
            PrintWriter salida = new PrintWriter(escribir)) {
            salida.println(LocalDateTime.now() + ": " + mensaje);
        } catch (IOException e) {}
    }
}
