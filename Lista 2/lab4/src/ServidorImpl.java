/**
 * Laboratorio 3  
 * Autor: Darlan e Sandro
 * Ultima atualizacao: 28/04/2024
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

public class ServidorImpl implements IMensagem{
	
	private static List<String> fortunes;
    
	ArrayList<Peer> alocados;
	
    public ServidorImpl() {
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
    	
		
    	String msg = "";
        System.out.println("\nSolicitação do cliente: " + json);
        
        json = json.substring(1, json.length() - 1);
        String[] campos = json.split(",");
        int method = 0;
        String args = null;
       
        for (String campo : campos) {
            String[] partes = campo.split(":");
            String chave = partes[0].trim();
            String valor = partes[1].trim();
            chave = chave.substring(1, chave.length() - 1);
            valor = valor.substring(1, valor.length() - 1);
            if (chave.equals("method")) {
                method = Integer.parseInt(valor);
            } else if (chave.equals("args")) {
                args = valor.replaceAll("\"", "");
            }
        }
        System.out.println(method);
		if(method == 1) {
			String fortuna = Lerfortuna();
		 msg += "{\n"
			        + "Resultado do servidor: \"" + fortuna.trim() + "\""
			        + "\n}";
		}else if(method == 2) {
			write(args);
			msg += "{\n"
					+ "Resultado do servidor: Sucesso em adicionar uma nova fortuna"
					+ "\n}";
		}
		return msg;
    }
    
    public void iniciar(){

    try {
    		//DONE: Adquire aleatoriamente um 'nome' do arquivo Peer.java
    	List<Peer> listaPeers = new ArrayList<>();
        for (Peer peer : Peer.values()) {
            listaPeers.add(peer);
        }
    		
    		Registry servidorRegistro;
    		try {
    			servidorRegistro = LocateRegistry.createRegistry(1099);
    		} catch (java.rmi.server.ExportException e){ //Registro jah iniciado 
    			System.out.print("Registro jah iniciado. Usar o ativo.\n");
    		}
    		servidorRegistro = LocateRegistry.getRegistry(); //Registro eh unico para todos os peers
    		String [] listaAlocados = servidorRegistro.list();
    		for(int i=0; i<listaAlocados.length;i++)
    			System.out.println(listaAlocados[i]+" ativo.");
    		
    		SecureRandom sr = new SecureRandom();
    		Peer peer = listaPeers.get(sr.nextInt(listaPeers.size()));
    		
    		int tentativas=0;
    		boolean repetido = true;
    		boolean cheio = false;
    		while(repetido && !cheio){
    			repetido=false;    			
    			peer = listaPeers.get(sr.nextInt(listaPeers.size()));
    			for(int i=0; i<listaAlocados.length && !repetido; i++){
    				
    				if(listaAlocados[i].equals(peer.getNome())){
    					System.out.println(peer.getNome() + " ativo. Tentando proximo...");
    					repetido=true;
    					tentativas=i+1;
    				}    			  
    				
    			}
    			//System.out.println(tentativas+" "+listaAlocados.length);
    			    			
    			//Verifica se o registro estah cheio (todos alocados)
    			if(listaAlocados.length>0 && //Para o caso inicial em que nao ha servidor alocado,
    					                     //caso contrario, o teste abaixo sempre serah true
    				tentativas==listaPeers.size()){ 
    				cheio=true;
    			}
    		}
    		
    		if(cheio){
    			System.out.println("Sistema cheio. Tente mais tarde.");
    			System.exit(1);
    		}
    		
            IMensagem skeleton  = (IMensagem) UnicastRemoteObject.exportObject(this, 0); //0: sistema operacional indica a porta (porta anonima)
            servidorRegistro.rebind(peer.getNome(), skeleton);
            System.out.print(peer.getNome() +" Servidor RMI: Aguardando conexoes...");
                        
        } catch(Exception e) {
            e.printStackTrace();
        }        

    }
    
    private static String Lerfortuna() {
  		CarregarFortunas();
  		Random random = new Random();
  		int index = random.nextInt(fortunes.size());
  		 return fortunes.get(index);		
  	}
      private static void CarregarFortunas() {
  	    try (BufferedReader reader = new BufferedReader(new FileReader("src\\fortunes-openbsd.txt"))) {
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
          try (BufferedWriter escrever = new BufferedWriter(new FileWriter("src\\fortunes-openbsd.txt", true))) {
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