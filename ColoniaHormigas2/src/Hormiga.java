public class Hormiga extends Thread {

    private String identificador;
    private int num;
    private Colonia c;
    private Paso paso;

    public Hormiga(int num, Colonia colonia, Paso p) {
        this.num = num;
        this.c = colonia;
        this.paso = p;
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

    public Paso getPaso() {
        return paso;
    }

    public void setPaso(Paso paso) {
        this.paso = paso;
    }
    
}
