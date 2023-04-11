import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

public class Colonia {
    private Semaphore semaforoAlmacenComida, semaforoZonaComer, semaforoZonaDescanso, 
            tunelEntrada, tunelSalida1, tunelSalida2;
    private Random r;
    private Lock almacenComida, zonaComer;
    private Condition sinComidaAlmacen, sinComidaComer;
    private int unidadesComidaAlmacen, unidadesComidaComer;
    
    private ListaThreads listaHormigasBuscandoComida, listaHormigasRepeliendoInsecto,
            listaHormigasAlmacen, listaHormigasLlevandoComida, listaHormigasHaciendoInstruccion,
            listaHormigasDescansando, listaZonaComer, listaRefugio;
    
    private JTextField comidaAlmacen, comidaZonaComer;
    
    public Colonia(JTextField buscandoComida, JTextField repeliendoInsecto, JTextField hormigasAlmacen,
            JTextField hormigasLlevandoComida, JTextField hormigasHaciendoInstruccion, JTextField hormigasDescansando,
            JTextField unidadesAlmacen, JTextField unidadesZonaComer, JTextField hormigasZonaComer, JTextField hormigasRefugio) {
        this.semaforoAlmacenComida = new Semaphore(10);
        this.semaforoZonaComer = new Semaphore(1);
        this.semaforoZonaDescanso = new Semaphore(1);
        this.tunelEntrada = new Semaphore(1);
        this.tunelSalida1 = new Semaphore(1);
        this.tunelSalida2 = new Semaphore(1);
        this.r = new Random();
        this.almacenComida = new ReentrantLock();
        this.zonaComer = new ReentrantLock();
        this.sinComidaAlmacen = almacenComida.newCondition();
        this.sinComidaComer = zonaComer.newCondition();
        this.unidadesComidaAlmacen = 0;
        this.unidadesComidaComer = 0;
        
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
    
    /*public void cruzarTunelSalida (Hormiga h) {
        // Este método simula la salida de una hormiga de la colonia 
        try {
            // ¿Cómo hago para elegir un túnel u otro?
            tunelEntrada.acquire();
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            
        } finally {
            tunelEntrada.release();
        }
    }*/

    public void salirPorComida(Hormiga h) {
        try {
            if (tunelSalida1.availablePermits() != 0) {
                tunelSalida1.acquire();
                System.out.println("La hormiga " +  h.getIdentificador() + " sale a por comida por el túnel de salida 1.");
                tunelSalida1.release();
            } else if (tunelSalida2.availablePermits() != 0) {
                tunelSalida2.acquire();
                System.out.println("La hormiga " +  h.getIdentificador() + " sale a por comida por el túnel de salida 2.");
                tunelSalida2.release();
            }
            listaHormigasBuscandoComida.meter(h);
            Thread.sleep(4000);
            tunelEntrada.acquire();
            listaHormigasBuscandoComida.sacar(h);
            System.out.println("La hormiga " + h.getIdentificador() + " vuelve a la Colonia.");
            tunelEntrada.release();
        } catch (InterruptedException ex) {
            
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
        
    // ALMACÉN DE COMIDA
    public void entrarAlmacenComidaImpar(Hormiga h) {
        try {
            semaforoAlmacenComida.acquire();
            listaHormigasAlmacen.meter(h);
            System.out.println("La hormiga " + h.getIdentificador() + " entra al almacén de comida");
            Thread.sleep(r.nextInt(2000,4001));
        } catch (InterruptedException ex) {
            
        } finally {
            semaforoAlmacenComida.release();
            listaHormigasAlmacen.sacar(h);
        }
    }
    
    public void dejarComida(Hormiga h) {
        almacenComida.lock();
        try {
            unidadesComidaAlmacen++;
            System.out.println("Unidades de comida: " + unidadesComidaAlmacen);
            comidaAlmacen.setText(unidadesComidaAlmacen + "");
            sinComidaAlmacen.signalAll();
        } finally {
            almacenComida.unlock();
        }
    }
    
    public void entrarAlmacenComidaPar(Hormiga h) {
        try {
            semaforoAlmacenComida.acquire();
            listaHormigasAlmacen.meter(h);
            System.out.println("La hormiga " + h.getIdentificador() + " entra al almacén de comida");
            Thread.sleep(r.nextInt(1000,2001));
        } catch (InterruptedException ex) {
            
        } finally {
            semaforoAlmacenComida.release();
            listaHormigasAlmacen.sacar(h);
        }
    }
    
    public void cogerComidaParaComer (Hormiga h) {
        //ARREGLAR TODO ESTO
        almacenComida.lock();
        try {
            if (unidadesComidaAlmacen == 0) {
                sinComidaAlmacen.await();
            }
            unidadesComidaAlmacen--;
            System.out.println("Unidades de comida: " + unidadesComidaAlmacen);
            comidaAlmacen.setText(unidadesComidaAlmacen + "");
        } catch (InterruptedException ex) {
        
        } finally {
            almacenComida.unlock();
        }
    }
    
    public void viajarZonaComer (Hormiga h) {
        try {
            listaHormigasLlevandoComida.meter(h);
            Thread.sleep(r.nextInt(1000,3001));
            listaHormigasLlevandoComida.sacar(h);
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void depositarComida (Hormiga h) {
        zonaComer.lock();
        try {
            Thread.sleep(r.nextInt(1000,2001));
            unidadesComidaComer++;
            comidaZonaComer.setText(unidadesComidaComer + "");
        } catch (InterruptedException ex) {
            
        } finally {
            zonaComer.unlock();
        }
    }
    
    // ZONA DE INSTRUCCIÓN
    public void zonaInstruccion(Hormiga h) {
        try {
            System.out.println("La hormiga " + h.getIdentificador() + " entra en la zona de instrucción.");
            listaHormigasHaciendoInstruccion.meter(h);
            Thread.sleep(r.nextInt(2000, 8001));
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
