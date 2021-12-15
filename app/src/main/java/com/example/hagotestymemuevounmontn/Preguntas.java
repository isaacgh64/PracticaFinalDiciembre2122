package com.example.hagotestymemuevounmontn;

public class Preguntas {
    String id, pregunta, respuestaC, respuestaI, respuestaI2;


    public Preguntas( String id, String pregunta,String  respuestaC, String respuestaI, String respuestaI2) {
        this.id=id;
        this.pregunta=pregunta;
        this.respuestaC=respuestaC;
        this.respuestaI=respuestaI;
        this.respuestaI2=respuestaI2;
    }

    public Preguntas(){

    }

    public String getId(){
        return id;
    }
    public String getPregunta(){
        return pregunta;
    }
    public String getRespuestaC(){
        return respuestaC;
    }
    public String getRespuestaI1(){
        return  respuestaI;
    }
    public String getRespuestaI2(){
        return respuestaI2;
    }

}
