package com.mycompany.lab5;

/*
 * Laboratorio 5  
 * Autores: Bruno Uhlmann Marcato e Thomas Oliveira Rocha Sampaio Silva
 * Ultima atualizacao: 19/11/2023
 */
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Peer implements IMensagem {

    ArrayList<PeerLista> alocados;

    public Peer() {
        alocados = new ArrayList<>();
    }

    //Cliente: invoca o metodo remoto 'enviar'
    //Servidor: invoca o metodo local 'enviar'
    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta;
        try {
            System.out.println("Mensagem recebida: " + mensagem.getMensagem());
            resposta = new Mensagem(parserJSON(mensagem.getMensagem()));
        } catch (Exception e) {
            e.printStackTrace();
            resposta = new Mensagem("{\n" + "\"result\": false\n" + "}");
        }
        return resposta;
    }

    public String parserJSON(String json) {
        String result = "false";

        String fortune = "-1";

        String[] v = json.split(":");
        System.out.println(">>>" + v[1]);
        String[] v1 = v[1].split("\"");
        System.out.println(">>>" + v1[1]);
        if (v1[1].equals("write")) {
            String[] p = json.split("\\[");
            System.out.println(p[1]);
            String[] p1 = p[1].split("]");
            System.out.println(p1[0]);
            String[] p2 = p1[0].split("\"");
            System.out.println(p2[1]);
            fortune = p2[1];

            // Write in file
            Principal pv2 = new Principal();
            pv2.write(fortune);
        } else if (v1[1].equals("read")) {
            // Read file
            Principal pv2 = new Principal();
            fortune = pv2.read();
        }

        result = "{\n" + "\"result\": \"" + fortune + "\"" + "}";
        System.out.println(result);

        return result;
    }

    public void iniciar() {
    try {
        // Adquire aleatoriamente um ID do PeerList
        List<PeerLista> listaPeers = new ArrayList<>();
        for (PeerLista peer : PeerLista.values()) {
            listaPeers.add(peer);
        }

        Registry servidorRegistro = LocateRegistry.createRegistry(1099);
        servidorRegistro = LocateRegistry.getRegistry(); // Registro é único para todos os peers

        // Peça ao usuário para escolher o peer
        System.out.println("Escolha um Peer para iniciar o servidor:");
        for (int i = 0; i < listaPeers.size(); i++) {
            System.out.println((i + 1) + ") " + listaPeers.get(i).getNome());
        }

        Scanner leitura = new Scanner(System.in);
        int escolha;
        do {
            System.out.print("Digite o número do Peer desejado: ");
            escolha = leitura.nextInt();
        } while (escolha < 1 || escolha > listaPeers.size());

        PeerLista peerEscolhido = listaPeers.get(escolha - 1);

        IMensagem skeleton = (IMensagem) UnicastRemoteObject.exportObject(this, 0);
        servidorRegistro.rebind(peerEscolhido.getNome(), skeleton);
        System.out.println(peerEscolhido.getNome() + " Servidor RMI: Aguardando conexões...");

        //---Cliente RMI
        new ClienteRMI().iniciarCliente();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public static void main(String[] args) {
        Peer servidor = new Peer();
        servidor.iniciar();
    }
}
