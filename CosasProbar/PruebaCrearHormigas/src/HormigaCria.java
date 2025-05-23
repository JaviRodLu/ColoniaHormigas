public class HormigaCria extends Hormiga {

    public HormigaCria(int num, Colonia colonia) {
        super(num, colonia);
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
        
    public void run() {
        int num = this.getNum();
        System.out.println("¡Hola! Soy la hormiga cría " + num);
    }
    
}

