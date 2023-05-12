import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 * Esta clase contiene todos los recursos compartidos a los que accederán 
 * todas las hormigas que se crean desde el Servidor.
 */

public class Colonia {
    GeneradorLog generadorLog;
    Random r = new Random();
    private Semaphore tunelEntrada, tunelSalida1, tunelSalida2;
    private Semaphore semaforoAlmacenComida;
    private Lock controlComidaAlmacen, controlComidaComer, controlInvasion, controlObrerasInterior, controlCriasComiendo;
    private Condition sinComidaAlmacen, sinComidaComer;
    private int unidadesComidaAlmacen, unidadesComidaComer;
    private int numObrerasInterior, numCriasComiendo;
    
    //Para la amenaza del insecto invasor
    private boolean invasionEnCurso;
    private boolean mensajeImpreso;
    private CyclicBarrier hormigasLuchando;
    private CountDownLatch hormigasRefugiadas;
    ArrayList<HormigaSoldado> listaHormigasSoldado;
    ArrayList<HormigaCria> listaHormigasCria;
    Lock protegerArrayHormigasSoldado, protegerArrayHormigasCria, protegerMsjImpreso;
    
    private ListaThreads listaHormigasBuscandoComida, listaHormigasAlmacen, listaHormigasLlevandoComida,
                        listaHormigasComiendo, listaHormigasDescansando, listaHormigasHaciendoInstruccion,
                        listaHormigasRepeliendoInsecto, listaHormigasRefugio;
    private JTextField campoUnidadesComidaAlmacen, campoUnidadesComidaComer;
    
    public Colonia(GeneradorLog gl, JTextField campoHormigasBuscandoComida, JTextField campoHormigasAlmacen,
            JTextField campoUnidadesComidaAlmacen, JTextField campoHormigasLlevandoComida,
            JTextField campoUnidadesComidaComer, JTextField campoHormigasComiendo, JTextField campoHormigasDescansando,
            JTextField campoHormigasInstruccion, JTextField campoHormigasRepeliendoInsecto, JTextField campoRefugio) {
        this.generadorLog = gl;
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
        this.numObrerasInterior = 0;
        this.controlObrerasInterior = new ReentrantLock();
        this.numCriasComiendo = 0;
        this.controlCriasComiendo = new ReentrantLock();
        this.listaHormigasSoldado = new ArrayList<>();
        this.listaHormigasCria = new ArrayList<>();
        this.protegerArrayHormigasSoldado = new ReentrantLock();
        this.protegerArrayHormigasCria = new ReentrantLock();
        this.mensajeImpreso = false;
        this.protegerMsjImpreso = new ReentrantLock();
        this.controlInvasion = new ReentrantLock();
        
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
    
    public void entrar(Hormiga h) throws IOException {
        /**
         * Este método simula la entrada de la hormiga en la colonia.
         * Hay un único túnel de entrada, y la hormiga tarda 0,1 segundos en cruzarlo.
         */
        try {
            tunelEntrada.acquire();
            System.out.println("La hormiga " + h.getIdentificador() + " accede al túnel de entrada.");
            generadorLog.imprimir("La hormiga " + h.getIdentificador() + " accede al túnel de entrada.");
            Thread.sleep(100);
        } catch (InterruptedException | Error ex) {}
        finally {
            tunelEntrada.release();
            System.out.println("La hormiga " + h.getIdentificador() + " entra en la colonia.");
            generadorLog.imprimir("La hormiga " + h.getIdentificador() + " entra en la colonia.");
        }
    }
    
    public void entrar(HormigaObrera ho) throws IOException {
        /**
         * Este método es prácticamente idéntico al anterior.
         * La diferencia con el anterior es que este solo sirve para las hormigas OBRERAS, 
         * dado que se incrementa el contador de hormigas dentro de la colonia tras atravesar el túnel.
         */
        try {
            tunelEntrada.acquire();
            System.out.println("La hormiga " + ho.getIdentificador() + " accede al túnel de entrada.");
            generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " accede al túnel de entrada.");
            Thread.sleep(100);
        } catch (InterruptedException | Error ex) {}
        finally {
            tunelEntrada.release();
            System.out.println("La hormiga " + ho.getIdentificador() + " entra en la colonia.");
            controlObrerasInterior.lock();
            try {
                numObrerasInterior++;
            } finally {
                controlObrerasInterior.unlock();
            }
        }
    }
    
    public void salirPorComida(HormigaObrera ho) throws IOException {
        /**
         * Este método simula la salida de las hormigas obreras de la colonia para ir a por comida.
         * Hay dos túneles de salida: si el 1 está libre, saldrá por él; si no, saldrá por el 2.
         * Al igual que con el túnel de entrada, la hormiga tardará 0,1 segundos en atravesarlo; 
         * y decrementará el contador correspondiente (numObrerasInterior).
         */
        try {
            if (tunelSalida1.tryAcquire()) {
                //tunelSalida1.acquire();
                System.out.println("La hormiga " + ho.getIdentificador() + " sale a por comida por el túnel de salida 1.");
                generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " sale a por comida por el túnel de salida 1.");
                Thread.sleep(100);
                tunelSalida1.release();
            } else if (tunelSalida2.tryAcquire()) {
                //tunelSalida2.acquire();
                System.out.println("La hormiga " + ho.getIdentificador() + " sale a por comida por el túnel de salida 2.");
                generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " sale a por comida por el túnel de salida 2.");
                Thread.sleep(100);
                tunelSalida2.release();
            }
            controlObrerasInterior.lock();
            try {
                numObrerasInterior--;
            } finally {
                controlObrerasInterior.unlock();
            }
            listaHormigasBuscandoComida.meter(ho);
        } catch (InterruptedException | Error ex) {}
    }
    
    public void cogerElementosComida(HormigaObrera ho) throws IOException {
        /**
         * Este método simula la recogida de comida en el exterior, tarea de las hormigas obreras IMPARES,
         * tarea en la que invierten 4 segundos.
         */
        try {
            Thread.sleep(4000);
            System.out.println("La hormiga " + ho.getIdentificador() + " coge cinco elementos de comida del exterior");
            generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " coge cinco elementos de comida del exterior");
        } catch (InterruptedException | Error ex) {}
    }
    
    public void volverConComida(HormigaObrera ho) throws IOException {
        /**
         * Este método simula el regreso de la hormiga obrera IMPAR a la colonia una vez ha recogido cinco elementos de comida.
         * Básicamente, volverá a atravesar el túnel de entrada, invirtiendo 0,1 segundos en ello.
         */
        try {
            tunelEntrada.acquire();
            listaHormigasBuscandoComida.sacar(ho);
            Thread.sleep(100);
            System.out.println("La hormiga " + ho.getIdentificador() + " vuelve a la Colonia.");
            generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " vuelve a la Colonia.");
        } catch (InterruptedException | Error ex) {}
        finally {
            tunelEntrada.release();
            controlObrerasInterior.lock();
            try {
                numObrerasInterior++;
            } finally {
                controlObrerasInterior.unlock();
            }
        }
    } 
    
    /**
     * ALMACÉN DE COMIDA
     * Aquí, las hormigas obreras IMPARES entrarán a dejar los cinco elementos de comida
     * que han recogido en el exterior; y las PARES los cogerán para llevarlos a la zona para comer. 
     */
    public void entrarAlmacenComida(HormigaObrera ho) throws IOException {
        /**
         * Este método hace que la hormiga entre al almacén de comida.
         * Dicho almacén tiene un aforo de 10 hormigas (modelado con un semáforo inicializado con 10 permisos), 
         * por lo que al entrar se apropiarán de un permiso.
         */
        try {
            semaforoAlmacenComida.acquire();
            listaHormigasAlmacen.meter(ho);
            System.out.println("La hormiga " + ho.getIdentificador() + " entra al almacén de comida");
            generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " entra al almacén de comida");
        } catch (InterruptedException | Error ex) {}
    }
    
        //Hormigas obreras IMPARES
    public void dejarComidaEnAlmacen(HormigaObrera ho) throws IOException {
        /**
         * Aquí, la hormiga obrera IMPAR deposita en el almacén los cinco elementos de comida 
         * que ha traido del exterior, tardando en la tarea entre 2 y 4 segundos.
         * Para ello, accederá en exclusión mutua mediante un cerrojo a la variable
         * unidadesComidaAlmacen, incrementando su valor en 5.
         */
        controlComidaAlmacen.lock();
        try {
            unidadesComidaAlmacen += 5;
            Thread.sleep(r.nextInt(2000, 4001));
            campoUnidadesComidaAlmacen.setText(unidadesComidaAlmacen + "");
            System.out.println("La hormiga " + ho.getIdentificador() + " deja 5 unidades de comida en el almacén."
                    + " Hay " + unidadesComidaAlmacen + " unidades.");
            generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " deja 5 unidades de comida en el almacén."
                    + " Hay " + unidadesComidaAlmacen + " unidades.");
            sinComidaAlmacen.signal();
        } catch (InterruptedException | Error ex) {}
        finally {
            controlComidaAlmacen.unlock();
        }
    }
    
        //Hormigas obreras PARES
    public void cogerComidaDelAlmacen(HormigaObrera ho) throws IOException {
        /**
         * Aquí, la hormiga obrera PAR recogerá del almacén cinco elementos de comida
         * para llevarlos a la zona para comer, para lo cual invierte entre 1 y 2 segundos.
         * Para ello, accederá en exclusión mutua mediante un cerrojo a la variable
         * unidadesComidaAlmacen, decrementando su valor en 5.
         */
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
            generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " coge 5 unidades de comida del almacén."
                    + " Hay " + unidadesComidaAlmacen + " unidades.");
        } catch (InterruptedException | Error ex) {}
        finally {
            controlComidaAlmacen.unlock();
        }
    }
    
    public void salirAlmacenComida(HormigaObrera ho) {
        /**
         * Este método hará que la hormiga salga del almacén de comida,
         * liberando el permiso que había adquirido del semáforo.
         */
        semaforoAlmacenComida.release();
        listaHormigasAlmacen.sacar(ho);
    }
    
    //Viajar a la zona de comer (obreras PARES)
    public void viajarZonaComer(HormigaObrera ho) {
        /**
         * Este método simulará el tiempo que invierte la hormiga obrera PAR en desplazarse 
         * del almacén de comida a la zona para comer y así dejar allí cinco elementos de comida.
         * Tarda un tiempo de entre 1 y 3 segundos.
         */
        try {
            listaHormigasLlevandoComida.meter(ho);
            Thread.sleep(r.nextInt(1000, 3001));
            listaHormigasLlevandoComida.sacar(ho);
        } catch (InterruptedException | Error ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void depositarComidaZonaComer(HormigaObrera ho) throws IOException {
        /**
         * En este método, la hormiga obrera PAR depositará los cinco elementos de comida
         * que ha llevado hasta la zona para comer.
         * Para ello, tardará un tiempo de entre 1 y 2 segundos; y accederá en exclusión mutua
         * a la variable unidadesComidaComer, incrementando su valor en 5.
         */
        controlComidaComer.lock();
        try {
            Thread.sleep(r.nextInt(1000, 2001));
            unidadesComidaComer += 5;
            campoUnidadesComidaComer.setText(unidadesComidaComer + "");
            System.out.println("La hormiga " + ho.getIdentificador() + " deja 5 unidades de comida en la zona para comer."
                    + " Hay " + unidadesComidaComer + " unidades.");
            generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " deja 5 unidades de comida en la zona para comer."
                    + " Hay " + unidadesComidaComer + " unidades.");
            sinComidaComer.signal();
        } catch (InterruptedException | Error ex) {}
        finally {
            controlComidaComer.unlock();
        }
    }
    
    /**
     * ZONA PARA COMER
     * Aquí irán las hormigas obreras tras hacer 10 iteraciones de su actividad habitual; 
     * las soldado, tras 6 iteraciones; y las crías, constantemente.
     * Hay tres métodos, entrarEnZonaComer, que únicamente introducirá a la hormiga
     * en el campo de texto correspondiente a la zona para comer; comer, que accederá
     * en exclusión mutua a la variable unidadesComidaComer decrementando su valor en 1 
     * y posteriormente dedicando 3 segundos a comer (entre 3 y 5 en el caso de las crías);
     * y salirDeZonaComer, que únicamente saca a la hormiga del campo de texto.
     * En caso de que no haya comida (unidadesComidaComer == 0), la hormiga esperará hasta que las
     * obreras pares repongan los elementos de comida.
     * Estos métodos tienen una versión para cada tipo de hormiga, cada uno con sus particularidades
     * según lo indicado en el enunciado, las cuales se explicarán en cada método.
     */
    
        //Hormigas OBRERAS
    public void entrarEnZonaComer(HormigaObrera ho) throws IOException {
        System.out.println("La hormiga " + ho.getIdentificador() + " va a comer.");
        generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " va a comer.");
        listaHormigasComiendo.meter(ho);
    }
    
    public void comer(HormigaObrera ho) throws IOException {
        controlComidaComer.lock();
        try {
            while (unidadesComidaComer == 0) {
                sinComidaComer.await();
            }
            unidadesComidaComer--;
            campoUnidadesComidaComer.setText(unidadesComidaComer + "");
            System.out.println("La hormiga " + ho.getIdentificador() + " coge una unidad de comida."
                    + " Quedan " + unidadesComidaComer + " unidades.");
            generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " coge una unidad de comida."
                    + " Quedan " + unidadesComidaComer + " unidades.");
        } catch (InterruptedException | Error ex) {}
        finally {
            controlComidaComer.unlock();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException | Error ex) {}
        }
    }
    
    public void salirDeZonaComer(HormigaObrera ho) throws IOException {
        System.out.println("La hormiga " + ho.getIdentificador() + " termina de comer");
        generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " termina de comer");
        listaHormigasComiendo.sacar(ho);
    }
    
        //Hormigas SOLDADO
    public void entrarEnZonaComer(HormigaSoldado hs) throws IOException {
        System.out.println("La hormiga " + hs.getIdentificador() + " va a comer.");
        generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " va a comer.");
        listaHormigasComiendo.meter(hs);
    }
    
    public void comer(HormigaSoldado hs) throws IOException {
        /**
         * La particularidad de este método, a diferencia del usado por las hormigas obreras,
         * es que, en caso de producirse una invasión, sacará inmediatamente a la hormiga
         * de la zona de comer para mandarla a luchar.
         */
        controlComidaComer.lock();
        try {
            while (unidadesComidaComer == 0) {
                sinComidaComer.await();
            }
            unidadesComidaComer--;
            campoUnidadesComidaComer.setText(unidadesComidaComer + "");
            System.out.println("La hormiga " + hs.getIdentificador() + " coge una unidad de comida."
                    + " Quedan " + unidadesComidaComer + " unidades.");
            generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " coge una unidad de comida."
                    + " Quedan " + unidadesComidaComer + " unidades.");
        } catch (InterruptedException | Error ex) {
            listaHormigasComiendo.sacar(hs);
            System.out.println("La hormiga " + hs.getIdentificador() + " deja de comer y se va a luchar.");
            generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " deja de comer y se va a luchar.");
            comprobarInvasion(hs);
        }
        finally {
            controlComidaComer.unlock();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException | Error ex) {
                listaHormigasComiendo.sacar(hs);
                comprobarInvasion(hs);
            }
        }
    }
    
    public void salirDeZonaComer(HormigaSoldado hs) throws IOException {
        System.out.println("La hormiga " + hs.getIdentificador() + " termina de comer");
        generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " termina de comer");
        listaHormigasComiendo.sacar(hs);
    }
    
    //Hormigas CRIA
    public void entrarEnZonaComer(HormigaCria hc) throws IOException {
        System.out.println("La hormiga " + hc.getIdentificador() + " va a comer.");
        generadorLog.imprimir("La hormiga " + hc.getIdentificador() + " va a comer.");
        listaHormigasComiendo.meter(hc);
        controlCriasComiendo.lock();
        try {
            numCriasComiendo++;
        } finally {
            controlCriasComiendo.unlock();
        }
    }
    
    public void comer(HormigaCria hc) throws IOException {
        /**
         * En este caso, al producirse la amenaza del insecto invasor, la hormiga cría
         * dejará de comer inmediatamente para dirigirse al refugio; además de decrementar
         * la variable numCriasComiendo (accediendo a ella en exclusión mutua), usada 
         * en la aplicación Cliente-Servidor.
         */
        controlComidaComer.lock();
        try {
            while (unidadesComidaComer == 0) {
                sinComidaComer.await();
            }
            unidadesComidaComer--;
            campoUnidadesComidaComer.setText(unidadesComidaComer + "");
            System.out.println("La hormiga " + hc.getIdentificador() + " coge una unidad de comida."
                    + " Quedan " + unidadesComidaComer + " unidades.");
            generadorLog.imprimir("La hormiga " + hc.getIdentificador() + " coge una unidad de comida."
                    + " Quedan " + unidadesComidaComer + " unidades.");
        } catch (InterruptedException | Error ex) {
            listaHormigasComiendo.sacar(hc);
            System.out.println("La hormiga " + hc.getIdentificador() + " deja de comer y se va al refugio.");
            generadorLog.imprimir("La hormiga " + hc.getIdentificador() + " deja de comer y se va al refugio.");
            controlCriasComiendo.lock();
            try {
                numCriasComiendo--;
            } finally {
                controlCriasComiendo.unlock();
            }
            comprobarInvasion(hc);
            controlCriasComiendo.lock();
            try {
                numCriasComiendo++;
            } finally {
                controlCriasComiendo.unlock();
            }
        }
        finally {
            controlComidaComer.unlock();
        }
        try {
            Thread.sleep(r.nextInt(3000, 5001));
        } catch (InterruptedException | Error ex) {
            listaHormigasComiendo.sacar(hc);
            System.out.println("La hormiga " + hc.getIdentificador() + " deja de comer y se va al refugio.");
            generadorLog.imprimir("La hormiga " + hc.getIdentificador() + " deja de comer y se va al refugio.");
            controlCriasComiendo.lock();
            try {
                numCriasComiendo--;
            } finally {
                controlCriasComiendo.unlock();
            }
            comprobarInvasion(hc);
            controlCriasComiendo.lock();
            try {
                numCriasComiendo++;
            } finally {
                controlCriasComiendo.unlock();
            }
        }
    }
    
    public void salirDeZonaComer(HormigaCria hc) throws IOException {
        System.out.println("La hormiga " + hc.getIdentificador() + " termina de comer");
        generadorLog.imprimir("La hormiga " + hc.getIdentificador() + " termina de comer");
        listaHormigasComiendo.sacar(hc);
        controlCriasComiendo.lock();
        try {
            numCriasComiendo--;
        } finally {
            controlCriasComiendo.unlock();
        }
    }
    
    /**
     * ZONA DE DESCANSO
     * A esta zona irán las hormigas obreras tras haber comido cuando han hecho
     * las 10 iteraciones de su actividad normal y las soldado tras haber hecho
     * su instrucción. Las obreras pasarán 1 segundo aquí, las soldado 2; 
     * y las crías irán continuamente, después de comer, pasando en esta zona
     * un tiempo de 4 segundos.
     * Hay tres métodos, entrarEnZonaDescanso, para meter a la hormiga en el campo de texto;
     * descansar, que "duerme" el hilo (la hormiga) durante el tiempo ya indicado; y 
     * salirDeZonaDescanso, que saca a la hormiga del campo de texto.
     * Cada uno de estos métodos tiene una versión para cada clase de hormiga, y así tratar
     * las particularidades de cada una de ellas.
     */
    
        //Hormiga OBRERA
    public void entrarEnZonaDescanso(HormigaObrera ho) throws IOException {
        listaHormigasDescansando.meter(ho);
        System.out.println("La hormiga " + ho.getIdentificador() + " entra en la zona de descanso");
        generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " entra en la zona de descanso");
    }
    
    public void descansar(HormigaObrera ho) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException | Error ex) {}
    }
    
    public void salirDeZonaDescanso(HormigaObrera ho) throws IOException {
        listaHormigasDescansando.sacar(ho);
        System.out.println("La hormiga " + ho.getIdentificador() + " sale de la zona de descanso");
        generadorLog.imprimir("La hormiga " + ho.getIdentificador() + " sale de la zona de descanso");
    }
    
        //Hormiga SOLDADO
    public void entrarEnZonaDescanso(HormigaSoldado hs) throws IOException {
        listaHormigasDescansando.meter(hs);
        System.out.println("La hormiga " + hs.getIdentificador() + " entra en la zona de descanso");
        generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " entra en la zona de descanso");
    }
    
    public void descansar(HormigaSoldado hs) throws IOException {
        /**
         * Las hormigas soldado tendrán que interrumpir su descanso en caso de que aparezca
         * el insecto invasor, por lo que tendrán que abandonar inmediatamente la zona de descanso.
         */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException | Error ex) {
            listaHormigasDescansando.sacar(hs);
            System.out.println("La hormiga " + hs.getIdentificador() + " deja de descansar y se va a luchar.");
            generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " deja de descansar y se va a luchar.");
            comprobarInvasion(hs);
        }
    }
    
    public void salirDeZonaDescanso(HormigaSoldado hs) throws IOException {
        listaHormigasDescansando.sacar(hs);
        System.out.println("La hormiga " + hs.getIdentificador() + " sale de la zona de descanso");
        generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " sale de la zona de descanso");
    }
    
        //Hormiga CRÍA
    public void entrarEnZonaDescanso(HormigaCria hc) throws IOException {
        listaHormigasDescansando.meter(hc);
        System.out.println("La hormiga " + hc.getIdentificador() + " entra en la zona de descanso");
        generadorLog.imprimir("La hormiga " + hc.getIdentificador() + " entra en la zona de descanso");
    }
    
    public void descansar(HormigaCria hc) throws IOException {
        /**
         * Las hormigas cría, por su parte, deberán irse al refugio cuando se produzca la invasión.
         */
        try {
            Thread.sleep(4000);
        } catch (InterruptedException | Error ex) {
            listaHormigasDescansando.sacar(hc);
            System.out.println("La hormiga " + hc.getIdentificador() + " deja de descansar y se va al refugio.");
            generadorLog.imprimir("La hormiga " + hc.getIdentificador() + " deja de descansar y se va al refugio.");
            comprobarInvasion(hc);
        }
    }
    
    public void salirDeZonaDescanso(HormigaCria hc) throws IOException {
        listaHormigasDescansando.sacar(hc);
        System.out.println("La hormiga " + hc.getIdentificador() + " sale de la zona de descanso");
        generadorLog.imprimir("La hormiga " + hc.getIdentificador() + " sale de la zona de descanso");
    }
    
    
    /**
     * ZONA DE INSTRUCCIÓN (hormigas SOLDADO)
     * En esta zona estarán las hormigas Soldado un tiempo de entre 2 y 8 segundos,
     * debiendo abandonarla si se produce la amenaza del insecto invasor.
     * Hay tres métodos: entrarEnZonaInstruccion, para meter a la hormiga en el campo de texto;
     * hacerInstruccion, para que la hormiga "haga instrucción" durante el tiempo indicado; y 
     * salirDeZonaInstruccion, para que la hormiga salga del campo de texto.
     */
    public void entrarEnZonaInstruccion(HormigaSoldado hs) throws IOException {
        System.out.println("La hormiga " + hs.getIdentificador() + " entra en la zona de instrucción.");
        generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " entra en la zona de instrucción.");
        listaHormigasHaciendoInstruccion.meter(hs);
    }

    public void hacerInstruccion(HormigaSoldado hs) throws IOException {
        try {
            Thread.sleep(r.nextInt(2000, 8001));
        } catch (InterruptedException | Error ex) {
            listaHormigasHaciendoInstruccion.sacar(hs);
            comprobarInvasion(hs);
            System.out.println("La hormiga " + hs.getIdentificador() + " deja de hacer instrucción y se va a luchar");
            generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " deja de hacer instrucción y se va a luchar");
        }
    }

    public void salirDeZonaInstruccion(HormigaSoldado hs) throws IOException {
        System.out.println("La hormiga " + hs.getIdentificador() + " sale de la zona de instrucción.");
        generadorLog.imprimir("La hormiga " + hs.getIdentificador() + " sale de la zona de instrucción.");
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
        this.hormigasRefugiadas = new CountDownLatch (listaHormigasSoldado.size());
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
            for (HormigaCria hc : listaHormigasCria){
                hc.interrupt();
                listaHormigasRefugio.meter(hc);
            }
        } finally {
            protegerArrayHormigasSoldado.unlock();
        }
    }
    
    public void reunirHormigas(HormigaSoldado hs) {
        try {
            hormigasLuchando.await();
            Thread.sleep(20000);
            hormigasRefugiadas.countDown();
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
    public void comprobarInvasion(HormigaCria hc) {
        if (invasionEnCurso) {
            reunirHormigasCria(hc);
            
        }
    }
    
    public void reunirHormigasCria(HormigaCria hc){
        try {
            hormigasRefugiadas.await();
            System.out.println("hormiga cria " + hc.getIdentificador() + " esta en el refugio");
            
            listaHormigasRefugio.sacar(hc);
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }
    
    public void reunirCriasNuevas(HormigaCria hc) {
        try {
            listaHormigasRefugio.meter(hc);
            hormigasRefugiadas.await();
            System.out.println("hormiga cria " + hc.getIdentificador() + " esta en el refugio");
            listaHormigasRefugio.sacar(hc);
        } catch (InterruptedException ex) {
            Logger.getLogger(Colonia.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    //Getters para la parte Distribuida
    public ArrayList<HormigaSoldado> getListaHormigasSoldado() {
        return listaHormigasSoldado;
    }

    public ArrayList<HormigaCria> getListaHormigasCria() {
        return listaHormigasCria;
    }

    public ListaThreads getListaHormigasBuscandoComida() {
        return listaHormigasBuscandoComida;
    }

    public ListaThreads getListaHormigasAlmacen() {
        return listaHormigasAlmacen;
    }

    public ListaThreads getListaHormigasLlevandoComida() {
        return listaHormigasLlevandoComida;
    }

    public ListaThreads getListaHormigasComiendo() {
        return listaHormigasComiendo;
    }

    public ListaThreads getListaHormigasDescansando() {
        return listaHormigasDescansando;
    }

    public ListaThreads getListaHormigasHaciendoInstruccion() {
        return listaHormigasHaciendoInstruccion;
    }

    public ListaThreads getListaHormigasRepeliendoInsecto() {
        return listaHormigasRepeliendoInsecto;
    }

    public ListaThreads getListaHormigasRefugio() {
        return listaHormigasRefugio;
    }
    
    public int getNumObrerasInterior() {
        controlObrerasInterior.lock();
        try {
            return numObrerasInterior;
        } finally {
            controlObrerasInterior.unlock();
        }
    }
    
    public int getNumCriasComiendo() {
        controlCriasComiendo.lock();
        try {
            return numCriasComiendo;
        } finally {
            controlCriasComiendo.unlock();
        }
    }
    
}
