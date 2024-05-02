/**
 * Laboratorio 3  
 * Autor: Darlan e Sandro
 * Ultima atualizacao: 28/04/2024
 */
import java.io.Serializable;

public class Mensagem implements Serializable {
    
	private static final long serialVersionUID = 1L;
	String mensagem;
	 private static String args;
	
	//Cliente -> Servidor
    public Mensagem(String mensagem, String opcao){    	
                
        setMensagem(mensagem,opcao);
        
    }
    //Servidor -> Cliente
    public Mensagem(String mensagem){
    	this.mensagem = new String(mensagem);
    }
    public String getMensagem(){
    	return this.mensagem;
    }
    public void setMensagem(String fortune, String opcao){
    	String mensagem="";
    	
    	switch(opcao){
    	case "1": {
    		args = null;
        		
    		mensagem += "{"
            		+ "\"method\": \"" + opcao + "\","
            		+ "\"args\": [\"" + args + "\"]"
            		+ "}";


			break;
		}
    	case "2": {
    		       args = fortune;         		
    		mensagem += "{"
            		+ "\"method\": \"" + opcao + "\","
            		+ "\"args\": [\"" + args + "\"]"
            		+ "}";
    		}
    	}//fim switch
    	this.mensagem = mensagem;
    }
    
}