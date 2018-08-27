package com.nietupski.contaseproblemas;

public class ProblemaEnunciado {
    private String enunciado;
    private String stringValoresIniciais;
    private String stringSolucao;
    private int modoValores = 7;

    ProblemaEnunciado(String enunciado, String stringValoresIniciais, String stringSolucao, int modoValores) {
        this.enunciado = enunciado;
        this.stringValoresIniciais = stringValoresIniciais;
        this.stringSolucao = stringSolucao;
        this.modoValores = modoValores;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getStringValoresIniciais() {
        return stringValoresIniciais;
    }

    public void setStringValoresIniciais(String stringValoresIniciais) {
        this.stringValoresIniciais = stringValoresIniciais;
    }

    public String getStringSolucao() {
        return stringSolucao;
    }

    public void setStringSolucao(String stringSolucao) {
        this.stringSolucao = stringSolucao;
    }

    public int getModoValores() {
        return modoValores;
    }

    public void setModoValores(int modoValores) {
        this.modoValores = modoValores;
    }
}
