package com.mycompany.lab5;

/*
 * Laboratorio 5  
 * Autores: Bruno Uhlmann Marcato e Thomas Oliveira Rocha Sampaio Silva
 * Ultima atualizacao: 19/11/2023
 */

public enum PeerLista {
    
    PEER1 {
        @Override
        public String getNome() {
            return "PEER1";
        }        
    },
    PEER2 {
        public String getNome() {
            return "PEER2";
        }        
    },
    PEER3 {
        public String getNome() {
            return "PEER3";
        }        
    },
    // Adicione um novo peer abaixo:
    PEER4 {
        public String getNome() {
            return "PEER4";
        }        
    };

    public String getNome(){
        return "NULO";
    }    
}
