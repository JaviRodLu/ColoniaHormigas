import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

public class Colonia {
    private static final Semaphore semaforoAlmacenComida = new Semaphore(10);
    private static final Semaphore semaforoZonaComer = new Semaphore(1);
    private static final Semaphore semaforoZonaDescanso = new Semaphore(1);
    private static final Semaphore tunelEntrada = new Semaphore(1);
    private static final Semaphore tunelSalida1 = new Semaphore(1);
    private static final Semaphore tunelSalida2 = new Semaphore(1);
    private static final Random random = new Random();
    private Lock almacenComida = new ReentrantLock();
    private int unidadesComida = 0;
    
    private ListaThreads listaHormigasBuscandoComida, listaHormigasRepeliendoInsecto,
            listaHormigasAlmacen, listaHormigasLlevandoComida, listaHormigasHaciendoInstruccion,
            listaHormigasDescansando, listaZonaComer, listaRefugio;
    
    private JTextField comidaAlmacen, comidaZonaComer;
    
    public Colonia(JTextField buscandoComida, JTextField repeliendoInsecto, JTextField hormigasAlmacen,
            JTextField hormigasLlevandoComida, JTextField hormigasHaciendoInstruccion, JTextField hormigasDescansando,
            JTextField unidadesAlmacen, JTextField unidadesZonaComer, JTextField hormigasZonaComer, JTextField hormigasRefugio) {
        this.listaHormigasBuscandoComida = new ListaThreads(buscandoComida);
        this.listaHormigasRepeliendoInsecto = new ListaThreads(repeliendoInsecto);
        this.listaHormigasAlmacen = new ListaThreads(hormigasAlmacen);
        this.listaHormigasLlevandoComida = new ListaThreads(hormigasLlevandoComida);
        this.listaHormigasHaciendoInstruccion = new ListaThreads(hormigasHaciendoInstruccion);
        this.listaHormigasDescansando = new ListaThreads(hormigasDescansando);
        this.comidaAlmacen = unidadesAlmacen;
        this.comidaZonaComer = unidadesZonaComer;
        this.listaZonaComer = new ListaThreads(hormigasZonaComer);
        this.listaRefugio = new ListaThreads(hormigasRefugio);
    }
    
    
    // Constantes de tiempo
    
    // Métodos para entrar y salir de la Colonia
    public void cruzarTunelEntrada (Hormiga h) {
        /**
         * Este método simula el acceso de una hormiga a la colonia
         */
        try {
            tunelEntrada.acquire();
            Thread.sleep(100);
            System.out.println("La hormiga " + h.getIdentificador() + " entra al túnel." );
        } catch (InterruptedException ex) {
            
        } finally {
            tunelEntrada.release();
            System.out.println("La hormiga " + h.getIdentificador() + " entra en la colonia." );
        }
    }
    
    public void cruzarTunelSalida (Hormiga h) {
        /**
         * Este método simula la salida de una hormiga de la colonia
         */
        try {
            // ¿Cómo hago para elegir un túnel u otro?
            tunelEntrada.acquire();
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            
        } finally {
            tunelEntrada.release();
        }
    }
    
    public void recogerComida(Hormiga h) {
        try {
            Thread.sleep(4000);
            System.out.println("La hormiga " + h.getIdentificador() +  " recoge una unidad de comida.");
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void dejarComida(Hormiga h) {
        almacenComida.lock();
        try {
            unidadesComida++;
            System.out.println("Unidades de comida: " + unidadesComida);
            comidaZonaComer.setText(unidadesComida + "");
        } finally {
            almacenComida.unlock();
        }
    }
    
    // ALMACÉN DE COMIDA
    public void entrarAlmacenComida(Hormiga h) {
        try {
            semaforoAlmacenComida.acquire();
            listaHormigasAlmacen.meter(h);
            System.out.println("La hormiga " + h.getIdentificador() + " entra al almacén de comida");
            Thread.sleep(random.nextInt(2000,4001));
        } catch (InterruptedException ex) {
            
        } finally {
            semaforoAlmacenComida.release();
            listaHormigasAlmacen.sacar(h);
        }
    }
    
    // ZONA DE INSTRUCCIÓN
    public void zonaInstruccion(Hormiga h) {
        try {
            System.out.println("La hormiga " + h.getIdentificador() + " entra en la zona de instrucción.");
            listaHormigasHaciendoInstruccion.meter(h);
            Thread.sleep(random.nextInt(2000, 8001));
            System.out.println("La hormiga " + h.getIdentificador() + " sale de la zona de instrucción.");
            listaHormigasHaciendoInstruccion.sacar(h);
        } catch (InterruptedException ex) {
            
        }
    }
    
    // REFUGIO
    public void zonaRefugio (Hormiga h) {
        listaRefugio.meter(h);
        
    }
    
    // ZONA DE COMER
    public void servirComida(Hormiga h) {
        
    }
    
    public void zonaComerObrera (Hormiga h) {
        
    }
    
    
    
    // ZONA DE DESCANSO
    
}
