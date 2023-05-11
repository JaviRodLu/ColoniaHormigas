import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Implementacion extends UnicastRemoteObject implements Interfaz {
    private Colonia colonia;

    public Implementacion(Colonia colonia) throws RemoteException {
        this.colonia = colonia;
    }

    public int getNumObrerasExterior() throws RemoteException {
        //return colonia.getHormigasObrerasExterior().size();
        int num;
        num = this.colonia.getListaHormigasBuscandoComida().lista.size();
        return num;
    }

    public int getNumObrerasInterior() throws RemoteException {
        //return colonia.getHormigasObrerasInterior().size();
        return 0;
    }

    public int getNumSoldadoInstruccion() throws RemoteException {
        //return colonia.getHormigasSoldadoInstruccion().size();
        return 0;
    }

    public int getNumSoldadoRepeliendoInvasion() throws RemoteException {
        //return colonia.getHormigasSoldadoRepeliendo().size();
        return 0;
    }

    public int getNumCriasZonaComer() throws RemoteException {
        //return colonia.getHormigasCriasComiendo().size();
        return 0;
    }

    public int getNumCriasRefugio() throws RemoteException {
        //return colonia.getHormigasCriasRefugio().size();
        return 0;
    }

    public void generarInsectoInvasor() throws RemoteException {
        colonia.interrumpirHormigas();
        colonia.interrumpirCrias();
        
    }
}