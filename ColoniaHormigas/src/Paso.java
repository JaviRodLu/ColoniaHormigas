import java.util.concurrent.locks.*;

public class Paso {
    private boolean cerrado;
    private Lock cerrojo;
    private Condition parar;
    
    public Paso() {
        this.cerrado = false;
        this.cerrojo = new ReentrantLock();
        this.parar = cerrojo.newCondition();
    }
    
    public void mirar() {
        try {
            cerrojo.lock();
            while (cerrado) {
                try {
                    parar.await();
                } catch (InterruptedException e) {}
            }
        } finally {
            cerrojo.unlock();
        }
    }
    
    public void abrir() {
        try {
            cerrojo.lock();
            cerrado = false;
            parar.signalAll();
        } finally {
            cerrojo.unlock();
        }
    }
    
    public void cerrar() {
        try {
            cerrojo.lock();
            cerrado = true;
        } finally {
            cerrojo.unlock();
        }
    }
    
}
