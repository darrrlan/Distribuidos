/**
 * Laboratorio 3  
 * Autor: Lucio Agostinho Rocha
 * Ultima atualizacao: 04/04/2023
 */
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServidorImpl implements IMensagem{
	private static List<String> fortunes;
	String msg;
    
    public ServidorImpl() {
                
    }
    
    //Cliente: invoca o metodo remoto 'enviar'
    //Servidor: invoca o metodo local 'enviar'
    @Override
    public Mensagem enviar(Mensagem mensagem) throws RemoteException {
        Mensagem resposta = null;
        
        try {
        	String msg="";
        	String json_opcao = mensagem.getMensagem();
			System.out.println("\nSolicitação do cliente: "+json_opcao);
			
			json_opcao = json_opcao.substring(1, json_opcao.length() - 1);
			String[] campos = json_opcao.split(",");
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
			if(method == 1) {
				String fortuna = Lerfortuna();
			 msg += "{\n"
				        + "Resultado do servidor: \"" + fortuna.trim() + "\""
				        + "\n}";
		     resposta = new Mensagem(parserJSON(msg));
			}else if(method == 2) {
				write(args);
				msg += "{\n"
						+ "Resultado do servidor: Sucesso em adicionar uma nova fortuna"
						+ "\n}";
				resposta = new Mensagem(parserJSON(msg));
			}
        	
		} catch (Exception e) {
			e.printStackTrace();
			resposta = new Mensagem("{\n" + "\"result\": false\n" + "}");
		}
        
        return resposta;
    }    
    
    public String parserJSON(String json) {
		String result = json;
		
		System.out.println(result);

		return result;
	}
    public void iniciar(){

    try {
            Registry servidorRegistro = LocateRegistry.createRegistry(1024);            
            IMensagem skeleton  = (IMensagem) UnicastRemoteObject.exportObject(this, 0); //0: sistema operacional indica a porta (porta anonima)
            servidorRegistro.rebind("servidorFortunes", skeleton);
            System.out.print("Servidor RMI: Aguardando conexoes...");
                        
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
	    try (BufferedReader reader = new BufferedReader(new FileReader("src\\fortune-br.txt"))) {
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
        try (BufferedWriter escrever = new BufferedWriter(new FileWriter("src\\fortune-br.txt", true))) {
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