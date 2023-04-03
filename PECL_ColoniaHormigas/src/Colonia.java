import java.util.Random;
import java.util.concurrent.Semaphore;

public class Colonia {
    private static final Semaphore semaforoAlmacenComida = new Semaphore(10);
    private static final Semaphore semaforoZonaComer = new Semaphore(1);
    private static final Semaphore semaforoZonaDescanso = new Semaphore(1);
    private static final Semaphore tunelEntrada = new Semaphore(1);
    private static final Semaphore tunelSalida1 = new Semaphore(1);
    private static final Semaphore tunelSalida2 = new Semaphore(1);
    private static final Random random = new Random();
    
    // Constantes de tiempo
    
}
