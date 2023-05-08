
import java.util.logging.Level;
import java.util.logging.Logger;

public class HormigaSoldado extends Hormiga {
    private boolean enGuerra;
    
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
        this.enGuerra = false;
    }
    
    @Override
    public void run() {
        /* Para la interrupción, hacer while (!isInterrupted()) 
        y después el comportamiento correspondiente a la interrupción */
        int num = this.getNum();
        this.getPaso().mirar();
        this.getC().cruzarTunelEntrada(this);
        while (true) {
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
                this.getC().comer(this); //Logger.getLogger(HormigaSoldado.class.getName()).log(Level.SEVERE, null, ex);
                this.getPaso().mirar();
                this.getC().salirDeZonaComer(this); //Logger.getLogger(HormigaSoldado.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        /*while(!this.isInterrupted()) {
            for (int i = 0; i < 6; i++) {
                this.getPaso().mirar();
                this.getC().zonaInstruccion(this);
                this.getPaso().mirar();
                this.getC().zonaDescansoSoldado(this);
            }
            this.getPaso().mirar();
            this.getC().zonaComer(this);
            }
        this.getC().getListaHormigasRepeliendoInsecto().meter(this);
        }*/
    }

    public void setEnGuerra(boolean enGuerra) {
        this.enGuerra = enGuerra;
    }
}
