package com.mycompany.lab5;

/*
 * Laboratorio 5  
 * Autores: Darlan Oliveira Santos e Sandro Pinheiro
 * Ultima atualizacao: 10/06/2024
 */


import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IMensagem extends Remote {
    
    public Mensagem enviar(Mensagem mensagem) throws RemoteException;
    
}
