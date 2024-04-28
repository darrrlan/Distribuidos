/**
 * Laboratorio 3  
 * Autor: Darlan e Sandro
 * Ultima atualizacao: 28/04/2024
 */
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IMensagem extends Remote {
    
    public Mensagem enviar(Mensagem mensagem) throws RemoteException;
    
}