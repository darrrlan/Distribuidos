package com.mycompany.lab5;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClienteRMI {

    public void iniciarCliente() {

        List<PeerLista> listaPeers = new ArrayList<PeerLista>();
        for (PeerLista peer : PeerLista.values())
            listaPeers.add(peer);

        try {

            Registry registro = LocateRegistry.getRegistry("127.0.0.1", 1099);


            IMensagem stub = null;
            PeerLista peer = null;

            // Permitir ao usuário escolher o Peer
            System.out.println("Escolha um Peer para se conectar:");
            for (int i = 0; i < listaPeers.size(); i++) {
                System.out.println((i + 1) + ") " + listaPeers.get(i).getNome());
            }
            
            Scanner leitura = new Scanner(System.in);
            int escolha;
            do {
                System.out.print("Digite o número do Peer desejado: ");
                escolha = leitura.nextInt();
            } while (escolha < 1 || escolha > listaPeers.size());

            peer = listaPeers.get(escolha - 1);

            boolean conectou = false;
            while (!conectou) {
                try {
                    stub = (IMensagem) registro.lookup(peer.getNome());
                    conectou = true;
                } catch (java.rmi.ConnectException e) {
                    System.out.println("\n" + peer.getNome() + " indisponivel. ConnectException. Tentando o proximo...");
                } catch (java.rmi.NotBoundException e) {
                    System.out.println("\n" + peer.getNome() + " indisponivel. NotBoundException. Tentando o proximo...");
                }
            }
            System.out.println("Conectado no peer: " + peer.getNome());

            String opcao = "";
            do {
                System.out.println("1) Read");
                System.out.println("2) Write");
                System.out.println("x) Exit");
                System.out.print(">> ");
                opcao = leitura.next();
                switch (opcao) {
                    case "1": {
                        Mensagem mensagem = new Mensagem("", opcao);
                        Mensagem resposta = stub.enviar(mensagem); // dentro da mensagem tem o campo 'read'
                        System.out.println(resposta.getMensagem());
                        break;
                    }
                    case "2": {
                        // Monta a mensagem
                        System.out.print("Add fortune: ");
                        String fortune = leitura.next();

                        Mensagem mensagem = new Mensagem(fortune, opcao);
                        Mensagem resposta = stub.enviar(mensagem); // dentro da mensagem tem o campo 'write'
                        System.out.println(resposta.getMensagem());
                        break;
                    }
                }
            } while (!opcao.equals("x"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ClienteRMI().iniciarCliente();
    }
}
