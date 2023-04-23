
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private CyclicBarrier hormigasLuchando;
    //private AtomicBoolean invasorDetectado;
    //private ArrayList<HormigaSoldado> listaHormigasSoldado;
    ArrayList<HormigaSoldado> listaHormigasSoldado;
    private ArrayList<HormigaCria> listaHormigasCria;
    private boolean invasionEnCurso;

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
        //this.invasorDetectado = new AtomicBoolean(false);
        this.listaHormigasSoldado = new ArrayList<>();
        this.listaHormigasCria = new ArrayList<>();
        this.invasionEnCurso = false;

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
    // Getter y Setter para los ArrayList
    public ArrayList<HormigaCria> getListaHormigasCria() {
        return listaHormigasCria;
    }

    // Métodos para entrar y salir de la Colonia
    public void cruzarTunelEntrada(Hormiga h) {
        //Este método simula el acceso de una hormiga a la colonia
        try {
            tunelEntrada.acquire();
            System.out.println("La hormiga " + h.getIdentificador() + " accede al túnel.");
            Thread.sleep(100);
        } catch (InterruptedException ex) {

        } finally {
            tunelEntrada.release();
            System.out.println("La hormiga " + h.getIdentificador() + " entra en la colonia.");
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
    public void salirPorComida(HormigaObrera ho) {
        try {
            if (tunelSalida1.tryAcquire()) {
                //tunelSalida1.acquire();
                System.out.println("La hormiga " + ho.getIdentificador() + " sale a por comida por el túnel de salida 1.");
                Thread.sleep(100);
                tunelSalida1.release();
            } else if (tunelSalida2.tryAcquire()) {
                //tunelSalida2.acquire();
                System.out.println("La hormiga " + ho.getIdentificador() + " sale a por comida por el túnel de salida 2.");
                Thread.sleep(100);
                tunelSalida2.release();
            }
            listaHormigasBuscandoComida.meter(ho);
        } catch (InterruptedException ex) {

        }
    }

    public void recogerComida(HormigaObrera ho) {
        /*try {
            Thread.sleep(4000/5); //Tiene que tardar 4 segundos y recoger 5 unidades
            System.out.println("La hormiga " + ho.getIdentificador() +  " recoge una unidad de comida.");
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        System.out.println("La hormiga " + ho.getIdentificador() + " recoge una unidad de comida.");
    }

    public void volverAColonia(HormigaObrera ho) {
        try {
            tunelEntrada.acquire();
            listaHormigasBuscandoComida.sacar(ho);
            Thread.sleep(100);
            System.out.println("La hormiga " + ho.getIdentificador() + " vuelve a la Colonia.");
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            tunelEntrada.release();
        }
    }

    // ALMACÉN DE COMIDA
    /*public void entrarAlmacenComidaImpar(HormigaObrera ho) {
        try {
            semaforoAlmacenComida.acquire();
            listaHormigasAlmacen.meter(ho);
            System.out.println("La hormiga " + ho.getIdentificador() + " entra al almacén de comida");
            Thread.sleep(r.nextInt(2000,4001));
        } catch (InterruptedException ex) {
            
        } 
    }*/
    public void entrarAlmacenComida(HormigaObrera ho) {
        try {
            semaforoAlmacenComida.acquire();
            listaHormigasAlmacen.meter(ho);
            System.out.println("La hormiga " + ho.getIdentificador() + " entra al almacén de comida");
        } catch (InterruptedException ex) {

        }
    }

    public void dejarComidaEnAlmacen(HormigaObrera ho) {
        almacenComida.lock();
        try {
            unidadesComidaAlmacen++;
            System.out.println("La hormiga " + ho.getIdentificador() + " deja una unidad de comida en el almacén. "
                    + "Hay " + unidadesComidaAlmacen + " unidades.");
            comidaAlmacen.setText(unidadesComidaAlmacen + "");
            sinComidaAlmacen.signalAll();
        } finally {
            almacenComida.unlock();
        }
    }

    public void salirDeAlmacenComida(HormigaObrera ho) {
        semaforoAlmacenComida.release();
        listaHormigasAlmacen.sacar(ho);
    }

    /*public void entrarAlmacenComidaPar(HormigaObrera ho) {
        // ¡CUIDADO! Mirar que hay comida disponible antes de entrar en el almacén, no solo el aforo
        try {
            semaforoAlmacenComida.acquire();
            listaHormigasAlmacen.meter(ho);
            System.out.println("La hormiga " + ho.getIdentificador() + " entra al almacén de comida");
            Thread.sleep(r.nextInt(1000,2001));
        } catch (InterruptedException ex) {
            
        }
    }*/
    public void cogerComidaParaComer(HormigaObrera ho) {
        almacenComida.lock();
        try {
            if (unidadesComidaAlmacen <= 0) {
                sinComidaAlmacen.await();
            }
            unidadesComidaAlmacen--;
            System.out.println("La hormiga " + ho.getIdentificador() + " coge una unidad de comida del almacén. "
                    + "Quedan " + unidadesComidaAlmacen + " unidades.");
            comidaAlmacen.setText(unidadesComidaAlmacen + "");
        } catch (InterruptedException ex) {

        } finally {
            almacenComida.unlock();
        }
    }

    public void viajarZonaComer(HormigaObrera ho) {
        try {
            listaHormigasLlevandoComida.meter(ho);
            Thread.sleep(r.nextInt(1000, 3001));
            listaHormigasLlevandoComida.sacar(ho);
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void depositarComida(HormigaObrera ho) {
        zonaComer.lock();
        try {
            unidadesComidaComer++;
            Thread.sleep(r.nextInt(1000, 2001));
            System.out.println("La hormiga " + ho.getIdentificador() + " deja una unidad de comida en zona de comer."
                    + " Hay " + unidadesComidaComer + " unidades.");
            comidaZonaComer.setText(unidadesComidaComer + "");
            sinComidaComer.signalAll();
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            zonaComer.unlock();
        }
    }

    // ZONA DE INSTRUCCIÓN
    public void entrarEnZonaInstruccion(HormigaSoldado hs) {
        System.out.println("La hormiga " + hs.getIdentificador() + " entra en la zona de instrucción.");
        listaHormigasHaciendoInstruccion.meter(hs);
    }

    public void hacerInstruccion(HormigaSoldado hs) {
        try {
            Thread.sleep(r.nextInt(2000, 8001));
        } catch (InterruptedException ex) {
            listaHormigasHaciendoInstruccion.sacar(hs);
            if (invasionEnCurso) {
                comprobarInvasion(hs);
                System.out.println("La hormiga " + hs.getIdentificador() + " deja de hacer instrucción");
            }
        }
    }

    public void salirDeZonaInstruccion(HormigaSoldado hs) {
        System.out.println("La hormiga " + hs.getIdentificador() + " sale de la zona de instrucción.");
        listaHormigasHaciendoInstruccion.sacar(hs);
    }

    // REFUGIO
    public void zonaRefugio(HormigaCria hc) {
        listaRefugio.meter(hc);
    }

    // ZONA DE COMER
    public void entrarEnZonaComer(Hormiga h) {
        listaZonaComer.meter(h);
        zonaComer.lock();
        try {
            if (unidadesComidaComer <= 0) {
                sinComidaComer.await();
            }
            unidadesComidaComer--;
            System.out.println("La hormiga " + h.getIdentificador() + " coge una unidad de comida. "
                    + "Quedan " + unidadesComidaComer + " unidades");
            comidaZonaComer.setText(unidadesComidaComer + "");
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            zonaComer.unlock();
            System.out.println("La hormiga " + h.getIdentificador() + " empieza a comer");
        }
    }

    public void comer(HormigaObrera ho) {
        // Para las hormigas Obrera
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void comer(HormigaSoldado hs) {
        // Para las hormigas Soldado
        //DUDA: ¿Por qué no se quita la hormiga del campo de texto de la zona para comer?
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            listaZonaComer.sacar(hs);
            if (invasionEnCurso) {
                comprobarInvasion(hs);
                System.out.println("La hormiga " + hs.getIdentificador() + " deja de comer");
            }
        }
        
        /*boolean interrumpida = false;
        while(!interrumpida) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                interrumpida = true;
                listaZonaComer.sacar(hs);
                if (invasionEnCurso) {
                    comprobarInvasion(hs);
                    System.out.println("La hormiga " + hs.getIdentificador() + " deja de comer");
                }
            }
        }
        listaZonaComer.sacar(hs);*/
        
    }

    public void comer(HormigaCria hc) {
        // Para las hormigas Cría
        try {
            Thread.sleep(r.nextInt(3000, 5001));
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void salirDeZonaComer(Hormiga h) {
        System.out.println("La hormiga " + h.getIdentificador() + " termina de comer");
        listaZonaComer.sacar(h);
    }

    // ZONA DE DESCANSO
    public void entrarEnZonaDescanso(Hormiga h) {
        listaHormigasDescansando.meter(h);
        System.out.println("La hormiga " + h.getIdentificador() + " entra en la zona de descanso");
    }

    public void descansar(HormigaObrera ho) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void descansar(HormigaSoldado hs) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            listaHormigasDescansando.sacar(hs);
            if (invasionEnCurso) {
                comprobarInvasion(hs);
                System.out.println("La hormiga " + hs.getIdentificador() + " deja de descansar");
            }
        }
    }

    public void descansar(HormigaCria hc) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void salirDeZonaDescanso(Hormiga h) {
        listaHormigasDescansando.sacar(h);
        System.out.println("La hormiga " + h.getIdentificador() + " sale de la zona de descanso");
    }

    //AMENAZA INSECTO INVASOR
    public void comprobarInvasion(HormigaSoldado h) {
        while (true) {
            if (!invasionEnCurso) {
                hacerInstruccion(h);
                comer(h);
                descansar(h);
            } else {
                /*if (listaHormigasRepeliendoInsecto(h)) {
                    // la hormiga soldado está lista para luchar
                    invasionEnCurso = false;
                } else {
                    // la hormiga soldado se une a las demás hormigas soldado para luchar
                    reunirHormigas(this);
                }*/
                reunirHormigas(h);
            }
        }
    }

    public void interrumpirHormigas(int numHormigasSoldado) {
        System.out.println("¡Invasión detectada!");
        this.invasionEnCurso = true;
        this.hormigasLuchando = new CyclicBarrier(numHormigasSoldado);
        for (HormigaSoldado hs : listaHormigasSoldado) {
            hs.interrupt();
            if (hs.isInterrupted()) {
                System.out.println("La hormiga " + hs.getIdentificador() + " ha sido interrumpida");
                listaHormigasRepeliendoInsecto.meter(hs);
                System.out.println("Hormigas listas para luchar: " + listaHormigasRepeliendoInsecto.lista.size());
            }
        }
    }

    public void reunirHormigas(HormigaSoldado hs) {
        try {
            hormigasLuchando.await();
            Thread.sleep(2000);
            hacerInstruccion(hs); //¿Cómo podemos reanudar el comportamiento de la hormiga tras la lucha?
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BrokenBarrierException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refugiarCria(HormigaCria hc) {
        listaRefugio.meter(hc);
    }

    public ListaThreads getListaHormigasRepeliendoInsecto() {
        return listaHormigasRepeliendoInsecto;
    }

    public ListaThreads getListaRefugio() {
        return listaRefugio;
    }

}
