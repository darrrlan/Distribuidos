package com.mycompany.lab5;



/*
 * Laboratorio 3  
 * Autor: Darlan e Sandro
 * Ultima atualizacao: 28/04/2024
 * Laboratorio 5  
 * Autores: Bruno Uhlmann Marcato e Thomas Oliveira Rocha Sampaio Silva
 * Ultima atualizacao: 19/11/2023
 */
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ServidorImpl implements IMensagem {

    private static List<String> fortunes;
    ArrayList<Peer> alocados;

    public ServidorImpl() {
        alocados = new ArrayList<>();
    }

    // Cliente: invoca o metodo remoto 'enviar'
    // Servidor: invoca o metodo local 'enviar'
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
        String msg = "";
        System.out.println("\nSolicitação do cliente: " + json);

        json = json.substring(1, json.length() - 1);
        String[] campos = json.split(",");
        int method = 0;
        String args = null;

        for (String campo : campos) {
            String[] partes = campo.split(":");
            String chave = partes[0].trim().replaceAll("\"", "");
            String valor = partes[1].trim().replaceAll("\"", "");
            if (chave.equals("method")) {
                method = Integer.parseInt(valor);
            } else if (chave.equals("args")) {
                args = valor;
            }
        }

        if (method == 1) {
            String fortuna = lerFortuna();
            msg += "{\n" + "\"result\": \"" + fortuna.trim() + "\"" + "\n}";
        } else if (method == 2) {
            write(args);
            msg += "{\n" + "\"result\": \"Sucesso em adicionar uma nova fortuna\"" + "\n}";
        }
        return msg;
    }

    public void iniciar() {
        try {
            // Adquire aleatoriamente um ID do PeerList
            List<PeerLista> listaPeers = new ArrayList<>();
            for (PeerLista peer : PeerLista.values()) {
                listaPeers.add(peer);
            }

            Registry servidorRegistro;
            try {
                servidorRegistro = LocateRegistry.createRegistry(1099);
            } catch (java.rmi.server.ExportException e) { // Registro já iniciado
                System.out.println("Registro já iniciado. Usar o ativo.");
            }
            servidorRegistro = LocateRegistry.getRegistry(); // Registro é único para todos os peers
            String[] listaAlocados = servidorRegistro.list();
            for (String alocado : listaAlocados) {
                System.out.println(alocado + " ativo.");
            }

            SecureRandom sr = new SecureRandom();
            PeerLista peerEscolhido = listaPeers.get(sr.nextInt(listaPeers.size()));

            int tentativas = 0;
            boolean repetido;
            boolean cheio = false;
            do {
                repetido = false;
                peerEscolhido = listaPeers.get(sr.nextInt(listaPeers.size()));
                for (String alocado : listaAlocados) {
                    if (alocado.equals(peerEscolhido.getNome())) {
                        System.out.println(peerEscolhido.getNome() + " ativo. Tentando próximo...");
                        repetido = true;
                        tentativas++;
                        break;
                    }
                }

                // Verifica se o registro está cheio (todos alocados)
                if (listaAlocados.length > 0 && tentativas == listaPeers.size()) {
                    cheio = true;
                }
            } while (repetido && !cheio);

            if (cheio) {
                System.out.println("Sistema cheio. Tente mais tarde.");
                System.exit(1);
            }

            IMensagem skeleton = (IMensagem) UnicastRemoteObject.exportObject(this, 0); // 0: sistema operacional indica a porta (porta anônima)
            servidorRegistro.rebind(peerEscolhido.getNome(), skeleton);
            System.out.println(peerEscolhido.getNome() + " Servidor RMI: Aguardando conexões...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String lerFortuna() {
        carregarFortunas();
        Random random = new Random();
        int index = random.nextInt(fortunes.size());
        return fortunes.get(index);
    }

    private static void carregarFortunas() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/fortunes-openbsd.txt"))) {
            fortunes = new ArrayList<>();
            StringBuilder texto = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.contains("%")) {
                    texto.append(line).append("\n");
                } else {
                    fortunes.add(texto.toString());
                    texto.setLength(0);
                }
            }

            if (texto.length() > 0) {
                fortunes.add(texto.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String novaFortuna) {
        try (BufferedWriter escrever = new BufferedWriter(new FileWriter("src/fortunes-openbsd.txt", true))) {
            escrever.write(novaFortuna);
            escrever.newLine();
            escrever.write("%");
            escrever.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServidorImpl servidor = new ServidorImpl();
        servidor.iniciar();
    }
}
