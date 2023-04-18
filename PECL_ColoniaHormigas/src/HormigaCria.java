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
        int num = this.getNum();
        this.getPaso().mirar();
        this.getC().cruzarTunelEntrada(this);
        while(true) {
            this.getPaso().mirar();
            this.getC().zonaComerCria(this);
            this.getPaso().mirar();
            this.getC().zonaDescansoCria(this);
        }
    }
    
}

