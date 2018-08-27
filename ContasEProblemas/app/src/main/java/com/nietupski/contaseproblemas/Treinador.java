package com.nietupski.contaseproblemas;


import static java.lang.Math.round;

public class Treinador {

    public int[] tabuadas = {2, 5, 3, 7, 11};

    private float pesoTempo = 0.4f;
    private float pesoNumAcertos = 0.6f;
    private long limiteTempoSemPenalidade = 180;  // 3min
    private int numeroOperacoesRodada = 4;
    private int modoDificuldade = 0;


    Treinador(int numOperacoesRodada) {
        setNumeroOperacoesRodada(numOperacoesRodada);
    }

    public void setNumeroOperacoesRodada(int numeroOperacoesRodada) {
        this.numeroOperacoesRodada = numeroOperacoesRodada;
    }

    public void setModoDificuldade(int modoDificuldade) {
        this.modoDificuldade = modoDificuldade;
    }

    public devolutivaTreinador dificuldadeLinear() {
        String feedback = "Zzz... Huh..?";
        int acuracia = 0;
        int dificuldade = 0;

        return new devolutivaTreinador(acuracia, dificuldade, feedback);
    }

    devolutivaTreinador dificuldadeLinear(int dificuldadeAtual, long tempo, float errosPorNumeroDeContas, boolean pediuAjuda) {

        // TODO: revisar

        String feedback = "";

        float parcelaTempo = (tempo <= limiteTempoSemPenalidade ? 1f : 0f) * pesoTempo;  // TODO: aperfeiçoar modelo, fazendo-o diferencial
        float parcelaPercentualAcertos = (1f - (errosPorNumeroDeContas)) * pesoNumAcertos;  // TODO: aperfeiçoar modelo

        int acuracia = (int) (100f * (parcelaTempo + parcelaPercentualAcertos));  // TODO: aperfeiçoar modelo, fazendo-o sensível ao número de tentativas

        // int dificuldade = acuracia / 10 - 1;  // TODO: aperfeiçoar modelo, fazendo-o diferencial (delta = [-2 .. 2])

        // por questões de precisão, não uso o valor da variável <int>acuracia diretamente
        // TODO: considerar usar normalização Softmax aqui
        float deltaDificuldade = ((10f * (parcelaTempo + parcelaPercentualAcertos)) - (float) dificuldadeAtual + 1f) / 4;  // normalizando a mudança de dificuldade entre -2 e 2
        int dificuldade = round((float) dificuldadeAtual + deltaDificuldade);

        if (dificuldade < 0) {
            dificuldade = 0;
        } else if (dificuldade > 9) {
            dificuldade = 9;
        }

        return new devolutivaTreinador(acuracia, dificuldade, feedback);

    }

    final class devolutivaTreinador {
        String feedback = "Zzz... Huh..?";
        int acuracia = 0;
        int dificuldade = 0;

        devolutivaTreinador(int acuracia, int dificuldade, String feedback) {
            this.acuracia = acuracia;
            this.dificuldade = dificuldade;
            this.feedback = feedback;
        }
    }
}
