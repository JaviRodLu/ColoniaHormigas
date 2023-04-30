import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HormigaObrera extends Hormiga {
    Random r = new Random();

    public HormigaObrera(int num, Colonia colonia, Paso paso) {
        super(num, colonia, paso);
        if (num < 10) {
            this.setIdentificador("HO000" + num);
        } else if (num < 100) {
            this.setIdentificador("HO00" + num);
        } else if (num < 1000) {
            this.setIdentificador("HO0" + num);
        } else {
            this.setIdentificador("HO" + num);
        }
    }

    @Override
    public void run() {
        int num = this.getNum();
        this.getPaso().mirar();
        this.getC().cruzarTunelEntrada(this);
        if (num % 2 == 0) {
            while (true) {
                for (int i = 0; i < 10; i++) {
                    this.getPaso().mirar();
                    //this.getC().entrarAlmacenComidaPar(this);
                    this.getC().entrarAlmacenComida(this);
                    try {
                        Thread.sleep(r.nextInt(1000,2001));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(HormigaObrera.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    for (int j = 0; j < 5; j++) {
                        this.getPaso().mirar();
                        this.getC().cogerComidaParaComer(this);
                    }
                    this.getPaso().mirar();
                    this.getC().salirDeAlmacenComida(this);
                    this.getPaso().mirar();
                    this.getC().viajarZonaComer(this);
                    try {
                        Thread.sleep(r.nextInt(1000, 2001));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(HormigaObrera.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    for (int j = 0; j < 5; j++) {
                        this.getPaso().mirar();
                        this.getC().depositarComida(this);
                    }
                    this.getPaso().mirar();
                    this.getC().depositarComida(this);
                }
                this.getPaso().mirar();
                this.getC().entrarEnZonaComer(this);
                this.getPaso().mirar();
                this.getC().comer(this);
                //this.getPaso().mirar();
                //this.getC().salirDeZonaComer(this);
                this.getPaso().mirar();
                this.getC().entrarEnZonaDescanso(this);
                this.getPaso().mirar();
                this.getC().descansar(this);
                this.getPaso().mirar();
                this.getC().salirDeZonaDescanso(this);
            }
        } else {
            while (true) {
                for (int i = 0; i < 10; i++) {
                    this.getPaso().mirar();
                    //this.getC().cruzarTunelSalida(this);
                    this.getC().salirPorComida(this);
                    try {
                        sleep(4000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(HormigaObrera.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    for (int j = 0; j < 5; j++) {
                        this.getPaso().mirar();
                        this.getC().recogerComida(this);
                    }
                    this.getPaso().mirar();
                    this.getC().volverAColonia(this);
                    this.getPaso().mirar();
                    //this.getC().entrarAlmacenComidaImpar(this);
                    this.getC().entrarAlmacenComida(this);
                    try {
                        Thread.sleep(r.nextInt(2000,4001));
                    } catch (InterruptedException ex) {
                        Logger.getLogger(HormigaObrera.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    for (int j = 0; j < 5; j++) {
                        this.getPaso().mirar();
                        this.getC().dejarComidaEnAlmacen(this);
                    }
                    this.getPaso().mirar();
                    this.getC().salirDeAlmacenComida(this);
                }
                this.getPaso().mirar();
                this.getC().entrarEnZonaComer(this);
                this.getPaso().mirar();
                this.getC().comer(this);
                //this.getPaso().mirar();
                //this.getC().salirDeZonaComer(this);
                this.getPaso().mirar();
                this.getC().entrarEnZonaDescanso(this);
                this.getPaso().mirar();
                this.getC().descansar(this);
                this.getPaso().mirar();
                this.getC().salirDeZonaDescanso(this);
            }
        }
    }

}
