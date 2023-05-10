import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

public class Colonia {
    Random r = new Random();
    private Semaphore tunelEntrada, tunelSalida1, tunelSalida2;
    private Semaphore semaforoAlmacenComida;
    private Lock controlComidaAlmacen, controlComidaComer;
    private Condition sinComidaAlmacen, sinComidaComer;
    private int unidadesComidaAlmacen, unidadesComidaComer;
    
    //Para la amenaza del insecto invasor
    private boolean invasionEnCurso;
    private boolean mensajeImpreso;
    private CyclicBarrier hormigasLuchando, hormigasRefugiadas;
    ArrayList<HormigaSoldado> listaHormigasSoldado;
    ArrayList<HormigaCria> listaHormigasCria;
    Lock protegerArrayHormigasSoldado, protegerArrayHormigasCria, protegerMsjImpreso;
    
    private ListaThreads listaHormigasBuscandoComida, listaHormigasAlmacen, listaHormigasLlevandoComida,
                        listaHormigasComiendo, listaHormigasDescansando, listaHormigasHaciendoInstruccion,
                        listaHormigasRepeliendoInsecto, listaHormigasRefugio;
    private JTextField campoUnidadesComidaAlmacen, campoUnidadesComidaComer;
    
    public Colonia(JTextField campoHormigasBuscandoComida, JTextField campoHormigasAlmacen,
            JTextField campoUnidadesComidaAlmacen, JTextField campoHormigasLlevandoComida,
            JTextField campoUnidadesComidaComer, JTextField campoHormigasComiendo, JTextField campoHormigasDescansando,
            JTextField campoHormigasInstruccion, JTextField campoHormigasRepeliendoInsecto, JTextField campoRefugio) {
        this.tunelEntrada = new Semaphore(1);
        this.tunelSalida1 = new Semaphore(1);
        this.tunelSalida2 = new Semaphore(1);
        this.semaforoAlmacenComida = new Semaphore(10);
        this.controlComidaAlmacen = new ReentrantLock();
        this.sinComidaAlmacen = controlComidaAlmacen.newCondition();
        this.unidadesComidaAlmacen = 0;
        this.controlComidaComer = new ReentrantLock();
        this.sinComidaComer = controlComidaComer.newCondition();
        this.unidadesComidaComer = 0;
        this.invasionEnCurso = false;
        this.listaHormigasSoldado = new ArrayList<>();
        this.listaHormigasCria = new ArrayList<>();
        this.protegerArrayHormigasSoldado = new ReentrantLock();
        this.protegerArrayHormigasCria = new ReentrantLock();
        this.mensajeImpreso = false;
        this.protegerMsjImpreso = new ReentrantLock();
        
        this.listaHormigasBuscandoComida = new ListaThreads(campoHormigasBuscandoComida);
        this.listaHormigasAlmacen = new ListaThreads (campoHormigasAlmacen);
        this.campoUnidadesComidaAlmacen = campoUnidadesComidaAlmacen;
        campoUnidadesComidaAlmacen.setText(unidadesComidaAlmacen + "");
        this.listaHormigasLlevandoComida = new ListaThreads(campoHormigasLlevandoComida);
        this.campoUnidadesComidaComer = campoUnidadesComidaComer;
        campoUnidadesComidaComer.setText(unidadesComidaComer + "");
        this.listaHormigasComiendo = new ListaThreads(campoHormigasComiendo);
        this.listaHormigasDescansando = new ListaThreads (campoHormigasDescansando);
        this.listaHormigasHaciendoInstruccion = new ListaThreads(campoHormigasInstruccion);
        this.listaHormigasRepeliendoInsecto = new ListaThreads(campoHormigasRepeliendoInsecto);
        this.listaHormigasRefugio = new ListaThreads(campoRefugio);
    }

    public boolean isInvasionEnCurso() {
        return invasionEnCurso;
    }
    
    public void entrar(Hormiga h) {
        try {
            tunelEntrada.acquire();
            System.out.println("La hormiga " + h.getIdentificador() + " accede al túnel de entrada.");
            Thread.sleep(100);
        } catch (InterruptedException ex) {}
        finally {
            tunelEntrada.release();
            System.out.println("La hormiga " + h.getIdentificador() + " entra en la colonia.");
        }
    }
    
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
        } catch (InterruptedException ex) {}
    }
    
    public void cogerElementosComida(HormigaObrera ho) {
        try {
            Thread.sleep(4000);
            System.out.println("La hormiga " + ho.getIdentificador() + " coge cinco elementos de comida del exterior");
        } catch (InterruptedException ex) {}
    }
    
    public void volverConComida(HormigaObrera ho) {
        try {
            tunelEntrada.acquire();
            listaHormigasBuscandoComida.sacar(ho);
            Thread.sleep(100);
            System.out.println("La hormiga " + ho.getIdentificador() + " vuelve a la Colonia.");
        } catch (InterruptedException ex) {}
        finally {
            tunelEntrada.release();
        }
    } 
    
    //ALMACÉN DE COMIDA
    public void entrarAlmacenComida(HormigaObrera ho) {
        try {
            semaforoAlmacenComida.acquire();
            listaHormigasAlmacen.meter(ho);
            System.out.println("La hormiga " + ho.getIdentificador() + " entra al almacén de comida");
        } catch (InterruptedException ex) {}
    }
    
        //Hormigas obreras IMPARES
    public void dejarComidaEnAlmacen(HormigaObrera ho) {
        controlComidaAlmacen.lock();
        try {
            unidadesComidaAlmacen += 5;
            Thread.sleep(r.nextInt(2000, 4001));
            campoUnidadesComidaAlmacen.setText(unidadesComidaAlmacen + "");
            System.out.println("La hormiga " + ho.getIdentificador() + " deja 5 unidades de comida en el almacén."
                    + " Hay " + unidadesComidaAlmacen + " unidades.");
            sinComidaAlmacen.signal();
        } catch (InterruptedException ex) {}
        finally {
            controlComidaAlmacen.unlock();
        }
    }
    
        //Hormigas obreras PARES
    public void cogerComidaDelAlmacen(HormigaObrera ho) {
        controlComidaAlmacen.lock();
        try {
            while (unidadesComidaAlmacen == 0) {
                sinComidaAlmacen.await();
            }
            unidadesComidaAlmacen -= 5;
            Thread.sleep(r.nextInt(1000, 2001));
            campoUnidadesComidaAlmacen.setText(unidadesComidaAlmacen + "");
            System.out.println("La hormiga " + ho.getIdentificador() + " coge 5 unidades de comida del almacén."
                    + " Hay " + unidadesComidaAlmacen + " unidades.");
        } catch (InterruptedException ex) {}
        finally {
            controlComidaAlmacen.unlock();
        }
    }
    
    public void salirAlmacenComida(HormigaObrera ho) {
        semaforoAlmacenComida.release();
        listaHormigasAlmacen.sacar(ho);
    }
    
    //Viajar a la zona de comer (obreras PARES)
    public void viajarZonaComer(HormigaObrera ho) {
        try {
            listaHormigasLlevandoComida.meter(ho);
            Thread.sleep(r.nextInt(1000, 3001));
            listaHormigasLlevandoComida.sacar(ho);
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void depositarComidaZonaComer(HormigaObrera ho) {
        controlComidaComer.lock();
        try {
            Thread.sleep(r.nextInt(1000, 2001));
            unidadesComidaComer += 5;
            campoUnidadesComidaComer.setText(unidadesComidaComer + "");
            System.out.println("La hormiga " + ho.getIdentificador() + " deja 5 unidades de comida en la zona para comer."
                    + " Hay " + unidadesComidaComer + " unidades.");
            sinComidaComer.signal();
        } catch (InterruptedException ex) {}
        finally {
            controlComidaComer.unlock();
        }
    }
    
    //ZONA PARA COMER
        //Hormigas OBRERAS
    public void entrarEnZonaComer(HormigaObrera ho) {
        System.out.println("La hormiga " + ho.getIdentificador() + " va a comer.");
        listaHormigasComiendo.meter(ho);
    }
    
    public void comer(HormigaObrera ho) {
        controlComidaComer.lock();
        try {
            while (unidadesComidaComer == 0) {
                sinComidaComer.await();
            }
            unidadesComidaComer--;
            campoUnidadesComidaComer.setText(unidadesComidaComer + "");
            System.out.println("La hormiga " + ho.getIdentificador() + " coge una unidad de comida."
                    + " Quedan " + unidadesComidaComer + " unidades.");
        } catch (InterruptedException ex) {}
        finally {
            controlComidaComer.unlock();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {}
        }
    }
    
    public void salirDeZonaComer(HormigaObrera ho) {
        System.out.println("La hormiga " + ho.getIdentificador() + " termina de comer");
        listaHormigasComiendo.sacar(ho);
    }
    
        //Hormigas SOLDADO
    public void entrarEnZonaComer(HormigaSoldado hs) {
        System.out.println("La hormiga " + hs.getIdentificador() + " va a comer.");
        listaHormigasComiendo.meter(hs);
    }
    
    public void comer(HormigaSoldado hs) {
        controlComidaComer.lock();
        try {
            while (unidadesComidaComer == 0) {
                sinComidaComer.await();
            }
            unidadesComidaComer--;
            campoUnidadesComidaComer.setText(unidadesComidaComer + "");
            System.out.println("La hormiga " + hs.getIdentificador() + " coge una unidad de comida."
                    + " Quedan " + unidadesComidaComer + " unidades.");
        } catch (InterruptedException ex) {
            listaHormigasComiendo.sacar(hs);
            comprobarInvasion(hs);
        }
        finally {
            controlComidaComer.unlock();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                listaHormigasComiendo.sacar(hs);
                comprobarInvasion(hs);
            }
        }
    }
    
    public void salirDeZonaComer(HormigaSoldado hs) {
        System.out.println("La hormiga " + hs.getIdentificador() + " termina de comer");
        listaHormigasComiendo.sacar(hs);
    }
    
    //Hormigas CRIA
    public void entrarEnZonaComer(HormigaCria hc) {
        System.out.println("La hormiga " + hc.getIdentificador() + " va a comer.");
        listaHormigasComiendo.meter(hc);
    }
    
    public void comer(HormigaCria hc) {
        controlComidaComer.lock();
        try {
            while (unidadesComidaComer == 0) {
                sinComidaComer.await();
            }
            unidadesComidaComer--;
            campoUnidadesComidaComer.setText(unidadesComidaComer + "");
            System.out.println("La hormiga " + hc.getIdentificador() + " coge una unidad de comida."
                    + " Quedan " + unidadesComidaComer + " unidades.");
        } catch (InterruptedException ex) {
            listaHormigasComiendo.sacar(hc);
            //comprobarInvasion(hc);
        }
        finally {
            controlComidaComer.unlock();
        }
        try {
            Thread.sleep(r.nextInt(3000, 5001));
        } catch (InterruptedException ex) {
            listaHormigasComiendo.sacar(hc);
            //comprobarInvasion(hc);
        }
    }
    
    public void salirDeZonaComer(HormigaCria hc) {
        System.out.println("La hormiga " + hc.getIdentificador() + " termina de comer");
        listaHormigasComiendo.sacar(hc);
    }
    
    //ZONA DE DESCANSO
        //Hormiga OBRERA
    public void entrarEnZonaDescanso(HormigaObrera ho) {
        listaHormigasDescansando.meter(ho);
        System.out.println("La hormiga " + ho.getIdentificador() + " entra en la zona de descanso");
    }
    
    public void descansar(HormigaObrera ho) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
    }
    
    public void salirDeZonaDescanso(HormigaObrera ho) {
        listaHormigasDescansando.sacar(ho);
        System.out.println("La hormiga " + ho.getIdentificador() + " sale de la zona de descanso");
    }
    
        //Hormiga SOLDADO
    public void entrarEnZonaDescanso(HormigaSoldado hs) {
        listaHormigasDescansando.meter(hs);
        System.out.println("La hormiga " + hs.getIdentificador() + " entra en la zona de descanso");
    }
    
    public void descansar(HormigaSoldado hs) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            listaHormigasDescansando.sacar(hs);
            comprobarInvasion(hs);
        }
    }
    
    public void salirDeZonaDescanso(HormigaSoldado hs) {
        listaHormigasDescansando.sacar(hs);
        System.out.println("La hormiga " + hs.getIdentificador() + " sale de la zona de descanso");
    }
    
        //Hormiga CRÍA
    public void entrarEnZonaDescanso(HormigaCria hc) {
        listaHormigasDescansando.meter(hc);
        System.out.println("La hormiga " + hc.getIdentificador() + " entra en la zona de descanso");
    }
    
    public void descansar(HormigaCria hc) {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            listaHormigasDescansando.sacar(hc);
            //comprobarInvasion(hc);
        }
    }
    
    public void salirDeZonaDescanso(HormigaCria hc) {
        listaHormigasDescansando.sacar(hc);
        System.out.println("La hormiga " + hc.getIdentificador() + " sale de la zona de descanso");
    }
    
    
    //ZONA DE INSTRUCCIÓN (hormigas SOLDADO)
    public void entrarEnZonaInstruccion(HormigaSoldado hs) {
        System.out.println("La hormiga " + hs.getIdentificador() + " entra en la zona de instrucción.");
        listaHormigasHaciendoInstruccion.meter(hs);
    }

    public void hacerInstruccion(HormigaSoldado hs) {
        try {
            Thread.sleep(r.nextInt(2000, 8001));
        } catch (InterruptedException ex) {
            listaHormigasHaciendoInstruccion.sacar(hs);
            comprobarInvasion(hs);
            System.out.println("La hormiga " + hs.getIdentificador() + " deja de hacer instrucción");
        }
    }

    public void salirDeZonaInstruccion(HormigaSoldado hs) {
        System.out.println("La hormiga " + hs.getIdentificador() + " sale de la zona de instrucción.");
        listaHormigasHaciendoInstruccion.sacar(hs);
    }
    
    
    //AMENAZA INSECTO INVASOR
        //Hormigas SOLDADO
    public void comprobarInvasion(HormigaSoldado hs) {
        if (invasionEnCurso) {
            reunirHormigas(hs);
        }
    }
    
    public void interrumpirHormigas() {
        while (invasionEnCurso) {
            System.out.println("Ya hay una invasión en curso");
            return;
        }
        System.out.println("¡Invasión detectada!");
        this.invasionEnCurso = true;
        protegerArrayHormigasSoldado.lock();
        this.hormigasLuchando = new CyclicBarrier(listaHormigasSoldado.size());
        try {
            for (HormigaSoldado hs : listaHormigasSoldado) {
                hs.interrupt();
                if (hs.isInterrupted()) {
                    System.out.println("La hormiga " + hs.getIdentificador() + " ha sido interrumpida");
                    try {
                        if (tunelSalida1.tryAcquire()) {
                            System.out.println("La hormiga " + hs.getIdentificador() + " sale a luchar por el túnel de salida 1.");
                            Thread.sleep(100);
                            tunelSalida1.release();
                        } else if (tunelSalida2.tryAcquire()) {
                            System.out.println("La hormiga " + hs.getIdentificador() + " sale a luchar por el túnel de salida 2.");
                            Thread.sleep(100);
                            tunelSalida2.release();
                    } 
                    } catch (InterruptedException ex) {}
                    listaHormigasRepeliendoInsecto.meter(hs);
                    System.out.println("Hormigas listas para luchar: " + listaHormigasRepeliendoInsecto.lista.size());
                }
            }
        } finally {
            protegerArrayHormigasSoldado.unlock();
        }
    }
    
    public void reunirHormigas(HormigaSoldado hs) {
        try {
            hormigasLuchando.await();
            Thread.sleep(20000);
            this.invasionEnCurso = false;
            protegerMsjImpreso.lock();
            try {
                if (!mensajeImpreso) {
                    System.out.println("El insecto invasor ha huido ¡Las hormigas han ganado!");
                    mensajeImpreso = true;
                }
            } finally {
                protegerMsjImpreso.unlock();
            }
            listaHormigasRepeliendoInsecto.sacar(hs);
            protegerMsjImpreso.lock();
            try {
                mensajeImpreso = false;
            } finally {
                protegerMsjImpreso.unlock();
            }
            this.hormigasLuchando.reset();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        //Hormigas CRÍA
    /*public void comprobarInvasion(HormigaCria hc) {
        if (invasionEnCurso) {
            reunirHormigas(hc);
        }
    }
    
    public void interrumpirCrias() {
        //Interrumpir las crías
        protegerArrayHormigasCria.lock();
        this.hormigasRefugiadas = new CyclicBarrier(listaHormigasCria.size());
        try {
            for (HormigaCria hc : listaHormigasCria) {
                hc.interrupt();
                if (hc.isInterrupted()) {
                    System.out.println("La hormiga " + hc.getIdentificador() + " ha sido interrumpida.");
                }
                listaHormigasRefugio.meter(hc);
            }
        } finally {
            protegerArrayHormigasCria.unlock();
        }
    }
    
    public void reunirHormigas(HormigaCria hc) {
        try {
            hormigasRefugiadas.await();
            Thread.sleep(20000);
            listaHormigasRefugio.sacar(hc);
            hormigasRefugiadas.reset();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
}
