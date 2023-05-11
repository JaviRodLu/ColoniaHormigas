import java.io.IOException;

/**
 * HORMIGA SOLDADO
 * Una vez entren en la colonia, irán a la zona de instrucción 
 * permaneciendo allí entre 2 y 8 segundos, yendo entonces
 * a la zona de descanso, donde permanecerán 2 segundos.
 * Repetirán este comportamiento seis veces, y entonces
 * irán a comer, tardando en ello 3 segundos.
 * Repetirán este comportamiento indefinidamente.
 * No obstante, si se produce la amenaza del insecto invasor, 
 * las hormigas que estén creadas en ese momento saldrán al exterior
 * a repeler la invasión, que durará 20 segundos; y a continuación
 * volverán al interior de la colonia y retomarán su actividad normal.
 */

public class HormigaSoldado extends Hormiga {

    public HormigaSoldado(int num, Colonia colonia, Paso paso) {
        super(num, colonia, paso);
        if (num < 10) {
            this.setIdentificador("HS000" + num);
        } else if (num < 100) {
            this.setIdentificador("HS00" + num);
        } else if (num < 1000) {
            this.setIdentificador("HS0" + num);
        } else {
            this.setIdentificador("HS" + num);
        }
    }
    
    @Override
    public void run() {
        try {
            int num = this.getNum();
            this.getPaso().mirar();
            this.getC().entrar(this);
            while(true) {
                while(!this.isInterrupted()) {
                    for (int i = 0; i < 6; i++) {
                        this.getPaso().mirar();
                        this.getC().entrarEnZonaInstruccion(this);
                        this.getPaso().mirar();
                        this.getC().hacerInstruccion(this);
                        this.getPaso().mirar();
                        this.getC().salirDeZonaInstruccion(this);
                        this.getPaso().mirar();
                        this.getC().entrarEnZonaDescanso(this);
                        this.getPaso().mirar();
                        this.getC().descansar(this);
                        this.getPaso().mirar();
                        this.getC().salirDeZonaDescanso(this);
                    }
                    this.getPaso().mirar();
                    this.getC().entrarEnZonaComer(this);
                    this.getPaso().mirar();
                    this.getC().comer(this);
                    this.getPaso().mirar();
                    this.getC().salirDeZonaComer(this);
                }
            }
        } catch (IOException ex) {}
    }
    
}
