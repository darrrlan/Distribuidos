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

public class Servidor {

	private static Socket socket;
	private static ServerSocket server;

	private static DataInputStream entrada;
	private static DataOutputStream saida;
	 private static List<String> fortunes;

	private int porta = 1025;

	public void iniciar() {
		System.out.println("Servidor iniciado na porta: " + porta);
		try {

			// Criar porta de recepcao
			server = new ServerSocket(porta);
			socket = server.accept();  //Processo fica bloqueado, ah espera de conexoes

			// Criar os fluxos de entrada e saida
			entrada = new DataInputStream(socket.getInputStream());
			saida = new DataOutputStream(socket.getOutputStream());
			
			String json_opcao = entrada.readUTF();
			System.out.println(json_opcao);
			
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
				write(args);
				saida.writeUTF("{\n"
						+ "Resultado do servidor: Sucesso em adicionar uma nova fortuna"
						+ "\n}");
				
			}else if(method == 2) {
				String fortuna = Lerfortuna();
				saida.writeUTF("{\n"
						+ "Resultado do servidor: \"" + fortuna.trim() + "\""
						+ "\n}");
			}else {
				saida.writeUTF("{\n"
						+ "Resultado do servidor: false"
						+ "\n}");
			}
		
	


			socket.close();

		} catch (Exception e) {
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

		new Servidor().iniciar();

	}

}