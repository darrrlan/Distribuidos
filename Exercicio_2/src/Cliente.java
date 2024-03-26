
/**
 * Laboratorio 1 de Sistemas Distribuidos
 * 
 *Autor: Darlan Oliveira Santos e Sandro Pinheiro Christe
 * Ultima atualizacao: 26/03/2024
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Cliente {
    
    private static Socket socket;
    private static DataInputStream entrada;
    private static DataOutputStream saida;
    private static String args;
    
    private int porta=1025;
    
    public void iniciar(){
    	System.out.println("Cliente iniciado na porta: "+porta);
    	
    	try {
            
            socket = new Socket("127.0.0.1", porta);
            
            entrada = new DataInputStream(socket.getInputStream());
            saida = new DataOutputStream(socket.getOutputStream());
            
            //Recebe do usuario algum valor
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Digite a opacao que deseja:\n\n 1 - write para escrever uma nova fortuna\n 2 - read para ler uma fortuna aleatoria");
            int valor = Integer.parseInt(br.readLine());
            if(valor == 1) {
            	System.out.print("Digite a fortuna: ");
            	args = br.readLine();
            }else {
            	args = null;
            	
            }
            
            String json_opcao = "{"
            		+ "\"method\": \"" + valor + "\","
            		+ "\"args\": [\"" + args + "\"]"
            		+ "}";
            
            //O valor eh enviado ao servidor
            saida.writeUTF(json_opcao);
            
            //Recebe-se o resultado do servidor
            String resultado = entrada.readUTF();
            
            //Mostra o resultado na tela
            System.out.println(resultado);
            
            socket.close();
            
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Cliente().iniciar();
    }
    
}