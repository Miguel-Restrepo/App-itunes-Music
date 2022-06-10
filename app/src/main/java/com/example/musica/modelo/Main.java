package com.example.musica.modelo;


public class Main {


    private int resultCount;
    private Cancion[] results;

    public int getNumero() {return resultCount;}

    public void setNumero(int numero) {
        this.resultCount=numero;
    }
    public Cancion[] getResults() { return results;}
    public void setResults(Cancion[] results){ this.results=results;}
}

