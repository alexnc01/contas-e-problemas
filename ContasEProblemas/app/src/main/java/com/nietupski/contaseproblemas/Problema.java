package com.nietupski.contaseproblemas;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class Problema {

    private Map<Character, Integer> dicionarioValores = new HashMap<>();  // TODO: se, a cada chave, eu pudesse ter um par de valores possíveis, poderia contornar a deficiência com operações comutativas...
    private List<Character> operacoesGabaritoSequencia = new ArrayList<>();
    private List<Operacao> operacoesGabaritoLista = new ArrayList<Operacao>();
    private String enunciado;
    private String feedback;
    private int modo = 4;  // o modo 'default' para o sorteio de valores, para os problemas
    private String gabarito = "XY+Z;=Z";  // TODO: documentar isso!
    private char resultadoFinal = 'Z';
    private String regraValoresIniciais = "XY+;=Z";  // TODO: documentar isso! Regra simples, sintaxe imutável, contemplando uma única condição, dada por uma única operação.
    private com.nietupski.contaseproblemas.Conta conta = new com.nietupski.contaseproblemas.Conta();
    List<ProblemaEnunciado> enunciados = new ArrayList<>();

    public void escolherProblema(boolean useProblemaExemplo) {
        inicializaProblema();
        if (useProblemaExemplo) {
            this.setEnunciado("Paulo e Tina foram à feira, \nPaulo com R$#A e Tina, com R$#B. \nQuantas dúzias de bananas comprariam juntos, sabendo-se que uma dúzia de bananas custa R$#C?");
            this.setGabarito("AB+J;JC/K;=K");  // TODO: implementar o parser
            this.setRegraValoresIniciais("AB+;>C");
            this.setModo(7);
        } else {
            this.populaListaEnunciados();
            Random rand = new Random();
            int posicao = rand.nextInt(enunciados.size());
            ProblemaEnunciado enunciadoSorteado = enunciados.get(posicao);
            this.setEnunciado(enunciadoSorteado.getEnunciado());
            this.setGabarito(enunciadoSorteado.getStringSolucao());
            this.setRegraValoresIniciais(enunciadoSorteado.getStringValoresIniciais());
            this.setModo(enunciadoSorteado.getModoValores());
        }
        this.sorteiaValoresIniciais(this.getRegraValoresIniciais());
        this.parseGabarito(getGabarito());
        this.preparaEnunciado(this.getEnunciado(), this.sorteiaValoresIniciais(this.getRegraValoresIniciais()));
    }

    public String getEnunciado() {
        return enunciado;
    }

    private void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    private String getGabarito() {
        return gabarito;
    }

    private void setGabarito(String gabarito) {
        this.gabarito = gabarito;
    }

    private void setModo(int modo) {
        this.modo = modo;
    }

    private String getRegraValoresIniciais() {
        return regraValoresIniciais;
    }

    private void setRegraValoresIniciais(String regraValoresIniciais) {
        this.regraValoresIniciais = regraValoresIniciais;
    }


    /**
     * Sorteia os valores iniciais com base em uma regra de validação.
     * A sintaxe é, necessariamente, "XY+;=Z"
     *
     * @param regraValoresIniciais String traduzindo a regra de validação usada para os valores iniciais
     */
    private Map<Character, Integer> sorteiaValoresIniciais(String regraValoresIniciais) {
        Map<Character, Integer> valoresIniciais = new HashMap<Character, Integer>();

        String[] regra = regraValoresIniciais.split(";");  // divide a string de regra em regras individuais

        int[] valores = new int[3];
        int referencia = 0;
        conta.sorteiaTermos(modo);
        valores[0] = conta.getPrimeiroTermo() + 2;  // Somo 2 para evitar erros na determinação do bias; irrelevante, para efeito dos problemas.
        valores[1] = conta.getSegundoTermo() + 2;
        Arrays.sort(valores);  // aqui, 'valores' será [0, v1, v2], onde v1 <= v2
        int bias = Conta.inteiroRandomico(1, valores[1] - 1);  // 'bias' a ser utilizado nas regras que contém inequações.  // TODO: melhorar o uso do bias, nas inequações abaixo

        char tokenTesteLogico = regra[1].charAt(0);  // extrai o comparador da expressão de resultado, da regra
        char tokenReferencia = regra[1].charAt(1);  // extrai o token que denota a variável de referência

        char tokenPrimeiroValor = regra[0].charAt(0);
        char tokenSegundoValor = regra[0].charAt(1);
        char tokenOperador = regra[0].charAt(2);

        // Validando os valores, frente à regra.
        // Ao final, os valores = [referencia, primeiroValor, segundoValor]
        switch (tokenOperador) {
            case '+':  // soma
            {
                switch (tokenTesteLogico) {
                    case '=':  // igual
                        valores[0] = valores[1] + valores[2];
                        break;
                    case '>':  // maior que
                    case '+':  // o valor, eventualmente, virá de um XML, onde os sinais de < e > são reservados
                        valores[0] = valores[1] + valores[2] - 2 * bias; // subtrai bias da soma
                        break;
                    case '<':  // menor que
                    case '-':  // o valor, eventualmente, virá de um XML, onde os sinais de < e > são reservados
                        valores[0] = valores[1] + valores[2] + 2 * bias; // soma bias à somas
                        break;
                }
                break;
            }
            case '-':  // subtração
            {
                valores[0] = valores[2];  // invertendo valores[2] com valores[1]...
                valores[2] = valores[1];
                valores[1] = valores[0];
                switch (tokenTesteLogico) {
                    case '=':  // igual
                        valores[0] = valores[1] - valores[2];
                        break;
                    case '>':  // maior que
                    case '+':  // o valor, eventualmente, virá de um XML, onde os sinais de < e > são reservados
                        valores[0] = valores[1] - valores[2] - bias;
                        break;
                    case '<':  // menor que
                    case '-':  // o valor, eventualmente, virá de um XML, onde os sinais de < e > são reservados
                        valores[0] = valores[1] - valores[2] + bias;
                        break;
                }
                break;
            }
            case '*':  // multiplicação
            {
                switch (tokenTesteLogico) {
                    case '=':  // igual
                        valores[0] = valores[1] * valores[2];
                        break;
                    case '>':  // maior que
                    case '+':  // o valor, eventualmente, virá de um XML, onde os sinais de < e > são reservados
                        valores[0] = valores[1] * (valores[2] - bias);
                        break;
                    case '<':  // menor que
                    case '-':  // o valor, eventualmente, virá de um XML, onde os sinais de < e > são reservados
                        valores[0] = valores[1] * (valores[2] + bias);
                        break;
                }
                break;
            }
            case '/':  // divisão
            {
                valores[0] = valores[2];  // invertendo valores[2] com valores[1]...
                valores[2] = valores[1];
                valores[1] = valores[0];
                switch (tokenTesteLogico) {
                    case '=':  // igual
                        valores[0] = valores[1] / valores[2];
                        break;
                    case '>':  // maior que
                    case '+':  // o valor, eventualmente, virá de um XML, onde os sinais de < e > são reservados
                        valores[0] = valores[1] / (valores[2] + bias);
                        break;
                    case '<':  // menor que
                    case '-':  // o valor, eventualmente, virá de um XML, onde os sinais de < e > são reservados
                        valores[0] = valores[1] / (valores[2] - bias);
                        break;
                }
                break;
            }
        }

        // TODO: redundância!

        valoresIniciais.put(tokenPrimeiroValor, valores[1]);
        valoresIniciais.put(tokenSegundoValor, valores[2]);
        valoresIniciais.put(tokenReferencia, valores[0]);

        dicionarioValores = new HashMap<>();
        dicionarioValores.put(tokenPrimeiroValor, valores[1]);
        dicionarioValores.put(tokenSegundoValor, valores[2]);
        dicionarioValores.put(tokenReferencia, valores[0]);

        return valoresIniciais;
    }

    private void parseGabarito(String configuracaoEsquemaContas) {
        // "Decomporá" operandos, operadores e resultados, tal como descritos na regra, em duas estruturas de dados:
        // Uma lista de Strings, que se prestará à comparação do esquema armado para resultados e
        // Uma lista, com as operações individuais, para apoio.  TODO: avaliar a necessidade disto
        // Exemplo: "AB+J;JC/K;=K"
        String[] sentencas = configuracaoEsquemaContas.split(";");
        for (String sentenca : sentencas) {
            if (sentenca.charAt(0) != '=') {
                Operacao operacao = new Operacao();
                operacao.setPrimeiroOperando(sentenca.charAt(0));
                operacoesGabaritoSequencia.add(sentenca.charAt(0));
                operacao.setSegundoOperando(sentenca.charAt(1));
                operacoesGabaritoSequencia.add(sentenca.charAt(1));
                operacao.setOperador(sentenca.charAt(2));
                operacoesGabaritoSequencia.add(sentenca.charAt(2));
                operacao.setResultado(sentenca.charAt(3));
                operacoesGabaritoSequencia.add(sentenca.charAt(3));
                this.operacoesGabaritoLista.add(operacao);
            } else {
                this.resultadoFinal = sentenca.charAt(1);
            }
        }
    }

    public Avaliacao verificaEntradasUsuario(Stack<String> entradas) {
        // comparará a sequência de entradas do usuário com o gabarito. Se operador, simplesmente compara;
        // se valor, confronta-o com o dicionário de símbolos 'dicionarioValores', que será criado aqui.
        // TODO: corrigir

        Avaliacao avaliacao = new Avaliacao();
        int numTokensChecar = 0;  // o número de tokens a verificar
        Character gabaritoValor = ' ';  // inicializado com <espaço> porque uma das comparações possíveis é com os operadores
        String entradaOperador = "";  // inicializado com string nula, por conta da checagem de operadores
        int entradaValor = 0;

        // String[] quatroOperacoes = getResources().getStringArray(R.array.simbolosQuatroOperacoes);
        Character[] quatroOperacoes = {'+', '-', '*', '/'};  // TODO: rever isto!

        Stack<Character> pilha = empilhaLista(this.operacoesGabaritoSequencia);
        Stack<Character> gabarito_pilha = invertePilhaCaracteres(pilha);
        Stack<Integer> pilhaAuxiliarComutativaValores = new Stack<Integer>();
        Stack<Character> pilhaAuxiliarComutativaTokens = new Stack<Character>();
        entradas = invertePilha(entradas);
        boolean valoresIncorretos = false;
        boolean operadorIncorreto = false;

        int tamanho_gabarito_pilha = gabarito_pilha.size();
        int tamanho_entradas = entradas.size();

        if (tamanho_gabarito_pilha > tamanho_entradas) {  // a entrada é menor do que o gabarito?
            avaliacao.solucaoIncompleta = true;
            numTokensChecar = tamanho_entradas;
        } else if (tamanho_entradas > tamanho_gabarito_pilha) {  // a entrada é maior do que o gabarito?
            avaliacao.solucaoMuitoGrande = true;
            numTokensChecar = tamanho_gabarito_pilha;
        } else {
            numTokensChecar = tamanho_gabarito_pilha;
        }

        // "descarrega" as pilhas, montando o dicionário de tokens e checando os valores, contra as reincidências.
        // considerar que, para cada operação, desempilho quatro tokens; logo, ordinalOperacaoCorrente = indiceTokenAvaliado / 4 + 1
        for (int indiceToken = 0; indiceToken < numTokensChecar; indiceToken++) {

            int ordinalOperacaoCorrente = indiceToken / 4 + 1;
            Log.d("ordinalOperacaoCorrente", Integer.toString(ordinalOperacaoCorrente));  // TODO: remover; desenvolvimento apenas

            if (!Arrays.asList(quatroOperacoes).contains(gabarito_pilha.peek())) {  // não é uma operação
                entradaValor = Integer.parseInt(entradas.pop());
                gabaritoValor = gabarito_pilha.pop();
                if (!((indiceToken + 1) % 4 == 0)) {  // para que não sejam postos nas pilhas auxiliares tokens de resultados de expressão
                    pilhaAuxiliarComutativaValores.push(entradaValor);  // guarda o valor que será desempilhado, para checagem de comutação de fatores, daqui a pouco
                    pilhaAuxiliarComutativaTokens.push(gabaritoValor);  // guarda o token que será desempilhado, para checagem de comutação de fatores, daqui a pouco.
                }
                Log.d("entradaValor", Integer.toString(entradaValor));  // TODO: remover; desenvolvimento apenas
                Log.d("gabaritoValor", gabaritoValor.toString());  // TODO: remover; desenvolvimento apenas

                if (!dicionarioValores.containsKey(gabaritoValor)) {  // TODO: verificar se o valor já consta no dicionário!
                    dicionarioValores.put(gabaritoValor, entradaValor);
                } else {
                    if (!dicionarioValores.get(gabaritoValor).equals(entradaValor)) {
                        valoresIncorretos = true;
                        avaliacao.indicesSentencasProblemasValores.add(ordinalOperacaoCorrente);
                        Log.d("Valores Incorretos", Integer.toString(entradaValor) + ", para token " + gabaritoValor.toString() + ", esperado " + dicionarioValores.get(gabaritoValor).toString());  // TODO: remover; desenvolvimento apenas
                    }
                }
            } else {
                // é um operador
                pilhaAuxiliarComutativaTokens = invertePilhaCaracteres(pilhaAuxiliarComutativaTokens);  // inverte a pilha auxiliar de tokens, posicionando-os para checagem de comutação
                entradaOperador = entradas.pop();
                gabaritoValor = gabarito_pilha.pop();
                operadorIncorreto = false;  // resseta o flag "operadorIncorreto"
                if (!entradaOperador.equals(gabaritoValor.toString())) {
                    operadorIncorreto = true;
                    avaliacao.indicesSentencasProblemasOperadores.add(ordinalOperacaoCorrente);
                }
                // e se a operação for comutativa?
                if (valoresIncorretos &&
                        !operadorIncorreto &&
                        (entradaOperador.equals("+") || entradaOperador.equals("*"))) {
                    // TODO: "DRY - Don't Repeat Yourself!"
                    boolean primeiroTermoInvertido = dicionarioValores.get(pilhaAuxiliarComutativaTokens.pop()).equals(pilhaAuxiliarComutativaValores.pop());
                    boolean segundoTermoInvertido = dicionarioValores.get(pilhaAuxiliarComutativaTokens.pop()).equals(pilhaAuxiliarComutativaValores.pop());


                    if (primeiroTermoInvertido && segundoTermoInvertido) {
                        valoresIncorretos = false;
                        avaliacao.indicesSentencasProblemasValores.remove(ordinalOperacaoCorrente);
                    }
                }

                // limpando pilhas auxiliares, mediante a passagem por um operador, após a checagem de comutatividade.
                pilhaAuxiliarComutativaTokens.clear();
                pilhaAuxiliarComutativaValores.clear();
            }

            // TODO: remover: desenvolvimento apenas
            for (Character c : dicionarioValores.keySet()) {
                Log.d(c.toString(), dicionarioValores.get(c).toString());
            }

        }

        return avaliacao;
    }

    private Stack<Character> empilhaLista(List<Character> lista) {
        Stack<Character> pilha = new Stack<>();
        for (Character token : lista) {
            pilha.push(token);
        }
        return pilha;
    }

    private Stack<Character> invertePilhaCaracteres(Stack<Character> pilha) {
        Stack<Character> pilhaInvertida = new Stack<>();
        while (!pilha.empty()) {
            pilhaInvertida.push(pilha.pop());
        }
        return pilhaInvertida;
    }

    private Stack<String> invertePilha(Stack<String> pilha) {
        Stack<String> pilhaInvertida = new Stack<>();
        while (!pilha.empty()) {
            pilhaInvertida.push(pilha.pop());
        }
        return pilhaInvertida;
    }

    private void preparaEnunciado(String enunciado, Map<Character, Integer> valoresIniciais) {
        // modifica o enunciado, substituindo as tags dos valores iniciais pelos valores de fato
        // TODO: pressupõe que o enunciado esteja setado, bem como os valores iniciais, sorteados. Tratar em "escolherProblema"
        String enunciadoFinalizado = enunciado;
        for (Character chave : valoresIniciais.keySet()) {
            enunciadoFinalizado = enunciadoFinalizado.replace("#" + chave, valoresIniciais.get(chave).toString());
        }
        this.setEnunciado(enunciadoFinalizado);
    }

    private void inicializaProblema() {
        dicionarioValores = new HashMap<>();
        operacoesGabaritoSequencia = new ArrayList<>();
        operacoesGabaritoLista = new ArrayList<Operacao>();
        enunciado = "";
        feedback = "";
        gabarito = "XY+Z;=Z";
        resultadoFinal = 'Z';
        regraValoresIniciais = "XY+;=Z";
    }

    private void populaListaEnunciados() {
        // TODO: deselegante; rever, sobretudo porque pode-se iniciar a partir da carga de um XML.
        // TODO: deselegante; rever, porque também há um procedimento identico, criando uma tabela com este conteúdo no DB da aplicação.
        // TODO: há mais de uma forma de resolver um problema, quase sempre. Contemplar esta verdade aqui, de algum modo.
        enunciados = new ArrayList<>();
        enunciados.add(new ProblemaEnunciado("Paulo e Tina foram à feira, Paulo com R$#A e Tina, com R$#B. Quantas dúzias de bananas comprariam juntos, sabendo-se que uma dúzia de bananas custa R$#C?", "AB+;+C", "AB+J;JC/K;=K", 0));
        enunciados.add(new ProblemaEnunciado("Para a Festa de Natal da escola, Maria trouxe #A brindes e Bárbara, #B brindes. Quantos brindes trouxeram, ao todo? Depois de dividir os brindes entre #D crianças, quantos brindes sobraram?", "AB+;+D", "AB+C;CD/E;ED*F;CF-G;=G", 7));
        enunciados.add(new ProblemaEnunciado("Sobre a mesa há #A garrafas de suco. Se pusermos #B garrafas na geladeira e #C garrafas no freezer, quantas garrafas permanecerão sobre a mesa?", "BC+;-A", "AB-J;JC-K;=K", 7));
        enunciados.add(new ProblemaEnunciado("José comprou #A batatas. Usou #B para fritar e #C para a sopa. Quantas batatas sobraram?", "BC+;-A", "BC+J;AJ-K;=K", 7));
        enunciados.add(new ProblemaEnunciado("Joaquim tinha #A canetas. Perdeu #B e deu #C a seu amigo. Com quantas ficou?", "BC+;-A", "AB-J;JC-K;=K", 7));
        enunciados.add(new ProblemaEnunciado("Na casa de Patrícia há #A cachorros. Na casa de Augusto há #B vezes essa quantidade. Quantos cachorros Patrícia e Augusto têm, juntos?", "AB*;+A", "AB*J;JA+K;=K", 0));
        enunciados.add(new ProblemaEnunciado("Quero guardar pares de sapatos em caixas onde cabem #A pares em cada uma. Quantos pares conseguirei guardar em #B caixas dessas?", "AB*;+A", "AB*J;=J", 0));
        enunciados.add(new ProblemaEnunciado("Os irmãos João e José querem doar #A brinquedos para crianças pobres. João conseguiu juntar #B brinquedos e José, #C brinquedos. Quantos brinquedos ainda faltam?", "BC+;-A", "BC+J;AJ-K;=K", 7));
    }

    // classe Operacao
    public static class Operacao {
        boolean habilitada = false;
        char primeiroOperando = 'X';
        char segundoOperando = 'Y';
        char resultado = 'Z';
        char operador = '+';

        Operacao() {
        }

        public void setHabilitada(boolean habilitada) {
            this.habilitada = habilitada;
        }

        void setOperador(char operador) {
            this.operador = operador;
        }

        void setPrimeiroOperando(char primeiroOperando) {
            this.primeiroOperando = primeiroOperando;
        }

        void setSegundoOperando(char segundoOperando) {
            this.segundoOperando = segundoOperando;
        }

        void setResultado(char resultado) {
            this.resultado = resultado;
        }

    }
}
