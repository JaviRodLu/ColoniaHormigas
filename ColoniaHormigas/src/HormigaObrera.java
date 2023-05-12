import java.io.IOException;

/**
 * HORMIGA OBRERA
 * Llegarán a la colonia.
 * Entonces, si su número es impar, saldrán al exterior a recoger 
 * cinco elementos de comida, tardando en ello cuatro segundos, 
 * volverán a la colonia y los depositarán en el almacén.
 * En cambio, si su número es par, accederán al almacén
 * a recoger cinco elementos de comida para llevarlos a la zona de comer.
 * Tras hacer estas actividades diez veces, irán a alimentarse a la zona de comer
 * y posteriormente a descansar 1 segundo en la zona de descanso.
 * A continuación, iniciarán de nuevo este comportamiento, el cual repetirán
 * indefinidamente.
 */

public class HormigaObrera extends Hormiga {
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
    
    public void run() {
        try {
            int num = this.getNum();
            this.getPaso().mirar();
            this.getC().entrar(this);
            if ((num % 2) == 0) {
                while(true) {
                    for (int i = 0; i < 10; i++) {
                        this.getPaso().mirar();
                        this.getC().entrarAlmacenComida(this);
                        this.getPaso().mirar();
                        this.getC().cogerComidaDelAlmacen(this);
                        this.getPaso().mirar();
                        this.getC().salirAlmacenComida(this);
                        this.getPaso().mirar();
                        this.getC().viajarZonaComer(this);
                        this.getPaso().mirar();
                        this.getC().depositarComidaZonaComer(this);
                    }
                    //Tras hacer 10 iteraciones, comer y descansar
                    this.getPaso().mirar();
                    this.getC().entrarEnZonaComer(this);
                    this.getPaso().mirar();
                    this.getC().comer(this);
                    this.getPaso().mirar();
                    this.getC().salirDeZonaComer(this);
                    this.getPaso().mirar();
                    this.getC().entrarEnZonaDescanso(this);
                    this.getPaso().mirar();
                    this.getC().descansar(this);
                    this.getPaso().mirar();
                    this.getC().salirDeZonaDescanso(this);
                }
            } else {
                while(true) {
                    for (int i = 0; i < 10; i++) {
                        this.getPaso().mirar();
                        this.getC().salirPorComida(this);
                        this.getPaso().mirar();
                        this.getC().cogerElementosComida(this);
                        this.getPaso().mirar();
                        this.getC().volverConComida(this); //Vuelve a la Colonia
                        this.getPaso().mirar();
                        this.getC().entrarAlmacenComida(this);
                        this.getPaso().mirar();
                        this.getC().dejarComidaEnAlmacen(this);
                        this.getPaso().mirar();
                        this.getC().salirAlmacenComida(this);
                    }
                    //Tras hacer 10 iteraciones, comer y descansar
                    this.getPaso().mirar();
                    this.getC().entrarEnZonaComer(this);
                    this.getPaso().mirar();
                    this.getC().comer(this);
                    this.getPaso().mirar();
                    this.getC().salirDeZonaComer(this);
                    this.getPaso().mirar();
                    this.getC().entrarEnZonaDescanso(this);
                    this.getPaso().mirar();
                    this.getC().descansar(this);
                    this.getPaso().mirar();
                    this.getC().salirDeZonaDescanso(this);
                }
            }
        } catch (IOException ex) {}
    }
}
