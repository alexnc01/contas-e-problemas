package com.nietupski.contaseproblemas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Avaliacao {
    Set<Integer> indicesSentencasProblemasValores = new HashSet<>();
    Set<Integer> indicesSentencasProblemasOperadores = new HashSet<>();
    boolean solucaoIncompleta = false;
    boolean solucaoMuitoGrande = false;

    Avaliacao() {
        ressetaAvaliacao();
    }

    public boolean verificaTerminoAvaliacao() {  // verifica se um estado final de solução já teria sido atingido
        return !solucaoIncompleta && !solucaoMuitoGrande && indicesSentencasProblemasValores.isEmpty() && indicesSentencasProblemasOperadores.isEmpty();
    }

    private void ressetaAvaliacao() {
        indicesSentencasProblemasValores.clear();
        indicesSentencasProblemasOperadores.clear();
        solucaoIncompleta = false;
        solucaoMuitoGrande = false;
    }

    public int verificaTerminoAvaliacaoVerboso() {  // retorna um código numérico correspondente a um certo estado da solução

        // 0: solução final atingida
        if (!solucaoIncompleta && !solucaoMuitoGrande && indicesSentencasProblemasValores.isEmpty() && indicesSentencasProblemasOperadores.isEmpty())
            return 0;

        // 3: solução tem passos demais
        if (solucaoMuitoGrande) return 3;

        // 2: solução não tem passos suficientes
        if (solucaoIncompleta) return 2;

        // TODO: revisar a lógica
        // 1: solução tem passos suficientes, porém detém erros em operadores ou valores
        if (!(indicesSentencasProblemasValores.isEmpty() && indicesSentencasProblemasOperadores.isEmpty()))
            return 1;

        return 2;  // default
    }

    public String geraRelatorio() {
        ArrayList<String> frases = new ArrayList<>();
        frases.add("Solução incompleta = " + (solucaoIncompleta ? "sim" : "não") + ";\n");
        frases.add("Solução longa demais = " + (solucaoMuitoGrande ? "sim" : "não") + ";\n");
        frases.add("Valores parecem incorretos = " + (!indicesSentencasProblemasValores.isEmpty() ? "sim" : "não") + ";\n");
        frases.add("Operadores parecem incorretos = " + (!indicesSentencasProblemasOperadores.isEmpty() ? "sim" : "não") + ";\n");

        StringBuilder construtor = new StringBuilder("Sentencas com problemas nos OPERADORES =");
        Iterator iterador = indicesSentencasProblemasOperadores.iterator();
        String valor = "";
        while (iterador.hasNext()) {
            valor = iterador.next().toString();
            construtor.append(" ").append(valor);
        }
        construtor.append(";\n");
        frases.add(construtor.toString());

        construtor = new StringBuilder("Sentencas com problemas nos VALORES =");
        iterador = indicesSentencasProblemasValores.iterator();
        valor = "";
        while (iterador.hasNext()) {
            valor = iterador.next().toString();
            construtor.append(" ").append(valor);
        }
        construtor.append(";\n");
        frases.add(construtor.toString());

        construtor = new StringBuilder("AVALIAÇÃO: \n");
        for (String i : frases) {
            construtor.append(i);
        }

        return construtor.toString();
    }

}
