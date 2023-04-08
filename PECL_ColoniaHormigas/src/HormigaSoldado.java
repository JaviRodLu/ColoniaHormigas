public class HormigaSoldado extends Hormiga {
    public HormigaSoldado(int num, Colonia colonia) {
        super(num, colonia);
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
        int num = this.getNum();
        System.out.println("¡Hola! Soy la hormiga soldado " + num);
        this.getC().cruzarTunelEntrada(this);
        while (true) {
            this.getC().zonaInstruccion(this);
        }
    }
    
}
