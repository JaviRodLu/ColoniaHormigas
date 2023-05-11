import java.rmi.*;

public interface Interfaz extends Remote {
    int getNumObrerasExterior() throws RemoteException;
    int getNumObrerasInterior() throws RemoteException;
    int getNumSoldadoInstruccion() throws RemoteException;
    int getNumSoldadoRepeliendoInvasion() throws RemoteException;
    int getNumCriasZonaComer() throws RemoteException;
    int getNumCriasRefugio() throws RemoteException;
    void generarInsectoInvasor() throws RemoteException;
}
