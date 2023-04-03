public class Hormiga extends Thread {
    private String identificador;
    private int num;
    private Colonia c;

    public Hormiga(int num, Colonia colonia) {
        this.num = num;
        this.c = colonia;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Colonia getC() {
        return c;
    }

    public void setC(Colonia c) {
        this.c = c;
    }
    
}
