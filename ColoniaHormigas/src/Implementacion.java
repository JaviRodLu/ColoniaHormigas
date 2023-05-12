/**
 * Implementación de los métodos declarados por la interfaz remota
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Implementacion extends UnicastRemoteObject implements Interfaz {
    private Colonia colonia;

    public Implementacion(Colonia colonia) throws RemoteException {
        this.colonia = colonia;
    }

    public int getNumObrerasExterior() throws RemoteException {
        return colonia.getListaHormigasBuscandoComida().lista.size();
    }

    public int getNumObrerasInterior() throws RemoteException {
        return colonia.getNumObrerasInterior();
    }

    public int getNumSoldadoInstruccion() throws RemoteException {
        return colonia.getListaHormigasHaciendoInstruccion().lista.size();
    }

    public int getNumSoldadoRepeliendoInvasion() throws RemoteException {
        if (colonia.isInvasionEnCurso()) {
            return colonia.getListaHormigasRepeliendoInsecto().lista.size();
        } else {
            return 0;
        }
    }

    public int getNumCriasZonaComer() throws RemoteException {
        return colonia.getNumCriasComiendo();
    }

    public int getNumCriasRefugio() throws RemoteException {
        return colonia.getListaHormigasRefugio().lista.size();
    }

    public void generarInsectoInvasor() throws RemoteException {
        colonia.interrumpirHormigas();
    }
    
}