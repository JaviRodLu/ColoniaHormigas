public class HormigaObrera extends Hormiga{
    public HormigaObrera(int num, Colonia colonia) {
        super(num, colonia);
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
        System.out.println("¡Hola! Soy la hormiga obrera " + num);
        this.getC().cruzarTunelEntrada(this);
        if (num % 2 == 0) {
            while (true) {
                this.getC().entrarAlmacenComidaPar(this);
                for (int i = 0; i < 5; i++) {
                    this.getC().cogerComidaParaComer(this);
                    this.getC().viajarZonaComer(this);
                    this.getC().depositarComida(this);
                }
            }
        } else {
            while (true) {
                //this.getC().cruzarTunelSalida(this);
                this.getC().salirPorComida(this);
                this.getC().entrarAlmacenComidaImpar(this);
                for (int i = 0; i < 5; i++) {
                    this.getC().dejarComida(this);
                }
            }
        }
    }
    
}
