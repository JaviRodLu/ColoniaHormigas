
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

    @Override
    public void run() {
        int num = this.getNum();
        this.getPaso().mirar();
        this.getC().cruzarTunelEntrada(this);
        if (num % 2 == 0) {
            while (true) {
                for (int i = 0; i < 10; i++) {
                    this.getPaso().mirar();
                    this.getC().entrarAlmacenComidaPar(this);
                    for (int j = 0; j < 5; j++) {
                        this.getPaso().mirar();
                        this.getC().cogerComidaParaComer(this);
                    }
                    this.getPaso().mirar();
                    this.getC().viajarZonaComer(this);
                    for (int j = 0; j < 5; j++) {
                        this.getPaso().mirar();
                        this.getC().depositarComida(this);
                    }
                }
                this.getPaso().mirar();
                this.getC().zonaComer(this);
                this.getPaso().mirar();
                this.getC().zonaDescansoObrera(this);
            }
        } else {
            while (true) {
                for (int i = 0; i < 10; i++) {
                    this.getPaso().mirar();
                    //this.getC().cruzarTunelSalida(this);
                    this.getPaso().mirar();
                    this.getC().salirPorComida(this);
                    this.getPaso().mirar();
                    this.getC().entrarAlmacenComidaImpar(this);
                    for (int j = 0; j < 5; j++) {
                        this.getPaso().mirar();
                        this.getC().dejarComida(this);
                    }
                }
                this.getPaso().mirar();
                this.getC().zonaComer(this);
                this.getPaso().mirar();
                this.getC().zonaDescansoObrera(this);
            }
        }
    }

}
