package com.nietupski.contaseproblemas;

import java.util.Random;

/* classe: Conta
   Implementa o comportamento de uma conta, em particular */

public class Conta {

    private int primeiroTermo = 0;
    private int segundoTermo = 0;
    private int resultado = 0;
    private char operadorDefault = '+';
    private char operador = operadorDefault;
    private int indice_operador = 0;
    private boolean habilitada = false;

    Conta() {
        inicializaConta();
    }

    static int inteiroRandomico(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    int getIndice_operador() {
        return indice_operador;
    }

    private void setIndice_operador(char operador) {
        switch (operador) {
            case '+':
                this.indice_operador = 0;
                break;
            case '-':
                this.indice_operador = 1;
                break;
            case '*':
                this.indice_operador = 2;
                break;
            case '/':
                this.indice_operador = 3;
                break;
        }
    }

    int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    private void setResultado() {
        switch (this.indice_operador) {
            case 0:  // soma
                this.resultado = this.primeiroTermo + this.segundoTermo;
                break;
            case 1:  // subtração
                this.resultado = this.primeiroTermo - this.segundoTermo;
                break;
            case 2:  // multiplicação
                this.resultado = this.primeiroTermo * this.segundoTermo;
                break;
            case 3:  // divisão
                this.resultado = this.primeiroTermo / this.segundoTermo;
                break;
            default:
                this.resultado = 0;
        }
    }

    int getPrimeiroTermo() {
        return primeiroTermo;
    }

    void setPrimeiroTermo(int primeiroTermo) {
        this.primeiroTermo = primeiroTermo;
    }

    int getSegundoTermo() {
        return segundoTermo;
    }

    void setSegundoTermo(int segundoTermo) {
        this.segundoTermo = segundoTermo;
    }

    void setOperador(char operador) {
        this.operador = operador;
        setIndice_operador(operador);
    }

    boolean isHabilitada() {
        return habilitada;
    }

    private void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }

    void sorteiaTermos(int modo) {

        // TODO; concluir

        /*
        modo : traduz o modo de sorteio, relação direta com o grau de dificuldade desejado, para a operação
            0: operandos de um dígito de 0 a 9, i
            1: operandos produzidos a partir da tabuada de 2
            2: operandos produzidos a partir da tabuada de 5
            3: operandos produzidos a partir da tabuada de 3
            4: operandos produzidos a partir das tabuadas de 2, 5 e 3
            5: operandos produzidos a partir das tabuadas de 2, 5, 3, x10
            6: operandos produzidos a partir da tabuada de 7
            7: operandos produzidos a partir das tabuadas de 2, 5, 3 e 7
            8: operandos produzidos a partir das tabuadas de 2, 5, 3, 7, x10
            9: operandos produzidos a partir da tabuadas de 11

         */

        int[] tabuadas = {2, 5, 3, 7, 11};
        int primeiroOperando = 0;
        int segundoOperando = 0;

        switch (modo) {
            case 0:
                primeiroOperando = inteiroRandomico(0, 9);
                segundoOperando = inteiroRandomico(0, 9);
                break;
            case 1:
                primeiroOperando = 2 * inteiroRandomico(0, 9);
                segundoOperando = 2 * inteiroRandomico(0, 9);
                break;
            case 2:
                primeiroOperando = 5 * inteiroRandomico(0, 9);
                segundoOperando = 5 * inteiroRandomico(0, 9);
                break;
            case 3:
                primeiroOperando = 3 * inteiroRandomico(0, 9);
                segundoOperando = 3 * inteiroRandomico(0, 9);
                break;
            case 4:
                primeiroOperando = tabuadas[inteiroRandomico(0, 2)] * inteiroRandomico(0, 9);
                segundoOperando = tabuadas[inteiroRandomico(0, 2)] * inteiroRandomico(0, 9);
                break;
            case 5:
                primeiroOperando = tabuadas[inteiroRandomico(0, 2)] * inteiroRandomico(0, 9) * (10 ^ inteiroRandomico(0, 1));
                segundoOperando = tabuadas[inteiroRandomico(0, 2)] * inteiroRandomico(0, 9) * (10 ^ inteiroRandomico(0, 1));
                break;
            case 6:
                primeiroOperando = 7 * inteiroRandomico(0, 9);
                segundoOperando = 7 * inteiroRandomico(0, 9);
                break;
            case 7:
                primeiroOperando = tabuadas[inteiroRandomico(0, 3)] * inteiroRandomico(0, 9);
                segundoOperando = tabuadas[inteiroRandomico(0, 3)] * inteiroRandomico(0, 9);
                break;
            case 8:
                primeiroOperando = tabuadas[inteiroRandomico(0, 3)] * inteiroRandomico(0, 9) * (10 ^ inteiroRandomico(0, 1));
                segundoOperando = tabuadas[inteiroRandomico(0, 3)] * inteiroRandomico(0, 9) * (10 ^ inteiroRandomico(0, 1));
                break;
            case 9:
                primeiroOperando = 11 * inteiroRandomico(0, 9);
                segundoOperando = 11 * inteiroRandomico(0, 9);
                break;
            default:  // o modo 'default' será o #0
                primeiroOperando = inteiroRandomico(0, 9);
                segundoOperando = inteiroRandomico(0, 9);
                break;

        }

        setPrimeiroTermo(primeiroOperando);
        setSegundoTermo(segundoOperando);
        // TODO: finalizar
    }

    private void sorteiaOperador() {
        this.indice_operador = inteiroRandomico(0, 3);
        int t1 = getPrimeiroTermo();
        int t2 = getSegundoTermo();

        switch (this.indice_operador) {
            case 0:
                setOperador('+');
                break;
            case 1:
                setOperador('-');
                if (t1 < t2) {  // para evitar resultados menores do que zero
                    setPrimeiroTermo(t2);
                    setSegundoTermo(t1);
                }
                break;
            case 2:
                setOperador('*');
                break;
            case 3:  // TODO: checar
                setOperador('/');
                if (t2 == 0) {
                    if (t1 != 0) {
                        setPrimeiroTermo(t2);
                        setSegundoTermo(t1);
                    } else {
                        setOperador(operadorDefault);
                    }
                }
                break;
            default:
                setOperador(operadorDefault);
        }
    }

    boolean validaContaOperadorTextual() {
        int resultado = getResultado();
        if (this.primeiroTermo == 0 && this.segundoTermo == 0 && resultado == 0) {
            return false;  // campos nulos
        } else {
            switch (this.operador) {
                case '+':
                    return (this.primeiroTermo + this.segundoTermo) == resultado;
                case '-':
                    return (this.primeiroTermo - this.segundoTermo) == resultado;
                case '*':
                    return (this.primeiroTermo * this.segundoTermo) == resultado;
                case '/':
                    return (this.primeiroTermo / this.segundoTermo) == resultado;
                default:
                    return false;
            }
        }
    }


    void sorteiaConta(int modo) {
        this.sorteiaTermos(modo);
        this.sorteiaOperador();  // nunca deve ser usado antes da definição dos termos, por conta da divisão
        setResultado();
        this.setHabilitada(true);
    }

    private void inicializaConta() {
        this.primeiroTermo = 0;
        this.segundoTermo = 0;
        this.resultado = 0;
        this.operador = '+';
    }
}
