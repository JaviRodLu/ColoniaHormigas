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
        
    public void run() {
        int num = this.getNum();
        System.out.println("¡Hola! Soy la hormiga obrera " + num);
        if (num % 2 == 0) {
            while (true) {
                
            }
        } else {
            while (true) {
                
            }
        }
    }
    
}
