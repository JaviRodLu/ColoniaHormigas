import java.io.IOException;

/**
 * HORMIGA CRÍA
 * Una vez lleguen a la colonia, irán a la zona de comer,
 * pasando allí un tiempo de entre 3 y 5 segundos, y acto seguido
 * irán a la zona de descanso, donde pasarán un tiempo de 4 segundos;
 * repitiendo todo este comportamiento de forma ininterrumpida.
 * No obstante, si se produce una amenaza por la aparición de un insecto invasor,
 * deberán ir inmediatamente al refugio, permaneciendo allí hasta que la invasión 
 * haya terminado.
 * Además, si llegan a la colonia en plena invasión, deberán ir al refugio 
 * directamente.
 */

public class HormigaCria extends Hormiga {
    public HormigaCria(int num, Colonia colonia, Paso paso) {
        super(num, colonia, paso);
        if (num < 10) {
            this.setIdentificador("HC000" + num);
        } else if (num < 100) {
            this.setIdentificador("HC00" + num);
        } else if (num < 1000) {
            this.setIdentificador("HC0" + num);
        } else {
            this.setIdentificador("HC" + num);
        }
    }
    
    @Override
    public void run() {
        try {
            int num = this.getNum();
            this.getPaso().mirar();
            this.getC().entrar(this);
            if (this.getC().isInvasionEnCurso()) {
                this.getPaso().mirar();
                this.getC().reunirCriasNuevas(this);
            }
            while(true) {
                while(!this.isInterrupted()) {
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
