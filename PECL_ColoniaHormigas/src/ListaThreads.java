import java.util.ArrayList;
import javax.swing.JTextField;

public class ListaThreads {
    ArrayList<Hormiga> lista;
    JTextField campo;

    public ListaThreads(JTextField campo) {
        lista = new ArrayList<>();
        this.campo = campo;
    }
    
    public synchronized void meter(Hormiga h) {
        lista.add(h);
        imprimir();
    }
    
    public synchronized void sacar(Hormiga h) {
        lista.remove(h);
        imprimir();
    }
    
    public void imprimir() {
        String contenido = "";
        for (int i = 0; i < lista.size(); i++) {
            contenido += lista.get(i).getIdentificador() + " ";
        }
        campo.setText(contenido);
    }
    
}
