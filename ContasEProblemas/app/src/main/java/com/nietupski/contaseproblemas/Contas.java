package com.nietupski.contaseproblemas;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.YELLOW;

public class Contas extends Activity {

    static private int quantidadeMaximaContas_default = 4;  // TODO: considerar a utilidade que há em poder mudar o número máximo de contas
    public List<com.nietupski.contaseproblemas.Conta> contas = new ArrayList<>();
    long referenciaTempo = 0;
    long tempoAteUltimaRodada = 0;

    boolean cronometroAtivo = false;
    com.nietupski.contaseproblemas.Treinador.devolutivaTreinador devolutiva;
    private int quantidadeMaximaContas = quantidadeMaximaContas_default;
    com.nietupski.contaseproblemas.Treinador treinador = new com.nietupski.contaseproblemas.Treinador(quantidadeMaximaContas);
    private int numRodada = 1;
    private int dificuldade = 0;
    private int acuracia = 100;
    private int numTentativas = 0;
    private int numErros = 0;
    private float errosPorNumeroContas = 0;
    // TODO: concluir!

    // DatabaseHelper db_helper = new DatabaseHelper(this);
    // SQLiteDatabase db = db_helper.getWritableDatabase();
    // inicializa o acesso ao banco de dados local


    public long getTempoAteUltimaRodada() {
        return tempoAteUltimaRodada;
    }

    public void setTempoAteUltimaRodada(long tempoAteUltimaRodada) {
        this.tempoAteUltimaRodada = tempoAteUltimaRodada;
    }

    public float getErrosPorNumeroContas() {
        return errosPorNumeroContas;
    }

    public void setErrosPorNumeroDeContas(float errosPorTentativa) {
        this.errosPorNumeroContas = errosPorTentativa;
    }


    public int getQuantidadeMaximaContas() {
        return quantidadeMaximaContas;
    }

    public void setQuantidadeMaximaContas(int quantidadeMaximaContas) {
        this.quantidadeMaximaContas = quantidadeMaximaContas;
    }

    public int getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(int dificuldade) {
        this.dificuldade = dificuldade;
    }

    public int getAcuracia() {
        return acuracia;
    }

    public void setAcuracia(int acuracia) {
        this.acuracia = acuracia;
    }

    public void setNumTentativas(int numTentativas) {
        this.numTentativas = numTentativas;
    }

    public void setNumErros(int numErros) {
        this.numErros = numErros;
    }

    private int mainActivity_encontraId(String identificador) {
        return getResources().getIdentifier(identificador, "id", getPackageName());
    }

    public void mainActivity_setBarraProgresso(String identificador, int valor) {
        ProgressBar p = findViewById(mainActivity_encontraId(identificador));
        p.setProgress(valor);
    }

    public void mainActivity_setTextoFeedback(String texto, boolean mostraToast) {
        TextView t = findViewById(mainActivity_encontraId("text_feedback"));
        t.setText(texto);

        // reproduz a mensagem na forma de uma "toast"
        if (mostraToast) {
            Context contexto = getApplicationContext();
            int duracao = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(contexto, texto, duracao);
            toast.show();
        }
    }

    public void mainActivity_inicializaBarrasProgresso() {
        // resseta barras de progresso
        mainActivity_setBarraProgresso("progressBar_dificuldade", this.dificuldade);
        mainActivity_setBarraProgresso("progressBar_acuracia", this.acuracia);
    }

    public void mainActivity_ocultaSinalIgual(String indice_comoTexto) {
        // oculta os sinais de igual
        TextView sinalIgual = findViewById(mainActivity_encontraId("sinalIgual" + indice_comoTexto));
        sinalIgual.setVisibility(View.INVISIBLE);
    }

    public void mainActivity_inicializaOperador(String indice_comoTexto) {
        // inicializa Spinner que conterá o operador
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.simbolosQuatroOperacoes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(mainActivity_encontraId("operador" + indice_comoTexto));
        spinner.setEnabled(false);
        spinner.setClickable(false);
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.INVISIBLE);
        mainActivity_setSpinnerOperador(indice_comoTexto, 0);  // inicializa com o sinal de '+';
    }

    public void mainActivity_setSpinnerOperador(String indice_comoTexto, int indiceOperador) {
        Spinner spinner = findViewById(mainActivity_encontraId("operador" + indice_comoTexto));
        spinner.setSelection(indiceOperador);  // TODO: teste; complementar
    }

    public void mainActivity_inicializaCheckBox(String indice_comoTexto, boolean mantemPrimeiraCheckbox) {
        // inicializa a checkBox
        CheckBox checkBox = findViewById(mainActivity_encontraId("checkBox" + indice_comoTexto));
        checkBox.setChecked(false);
        checkBox.setBackgroundColor(TRANSPARENT);
        if (!(indice_comoTexto.equals("1") && mantemPrimeiraCheckbox)) {  // não tornará a primeira checkbox invisível
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    public void mainActivity_inicializaCamposNumericos(String indice_comoTexto) {
        // inicializa os campos numéricos das contas
        List<String> tipos = Arrays.asList("primeiroTermo", "segundoTermo", "resultado");
        for (String c : tipos) {
            int idCampoNum = mainActivity_encontraId(c + indice_comoTexto);
            TextView textView = findViewById(idCampoNum);
            textView.setVisibility(View.INVISIBLE);
            textView.setText("");
        }
    }

    public void mainActivity_inicializaCronometro() {
        Chronometer cronometro = findViewById(mainActivity_encontraId("cronometro"));
        cronometro.stop();  // supondo que o método tenha sido evocado mediante o uso da função "Reiniciar"
        cronometroAtivo = false;
        referenciaTempo = 0;
        cronometro.setBase(SystemClock.elapsedRealtime());
    }

    public void inicializaInterface(boolean habilitaBotaoValidar, boolean habilitaBotaoAjuda) {

        for (int i = 1; i <= quantidadeMaximaContas; i++) {
            String i_comoTexto = Integer.toString(i);
            mainActivity_inicializaBarrasProgresso();
            mainActivity_ocultaSinalIgual(i_comoTexto);
            mainActivity_inicializaOperador(i_comoTexto);
            mainActivity_inicializaCheckBox(i_comoTexto, false);
            mainActivity_inicializaCamposNumericos(i_comoTexto);
        }

        Button botaoValidar = findViewById(mainActivity_encontraId("button_Validar"));
        botaoValidar.setEnabled(habilitaBotaoValidar);

        Button botaoAjuda = findViewById(mainActivity_encontraId("button_Ajuda"));
        botaoAjuda.setEnabled(habilitaBotaoAjuda);

        mainActivity_setBarraProgresso("progressBar_dificuldade", this.dificuldade + 1);
        mainActivity_setBarraProgresso("progressBar_acuracia", this.acuracia);

        mainActivity_setTextoFeedback("Zzz...", false);
    }

    private void inicializaContas() {
        contas = new ArrayList<>();
        for (int i = 1; i <= quantidadeMaximaContas; i++) {
            com.nietupski.contaseproblemas.Conta conta = new com.nietupski.contaseproblemas.Conta();
            contas.add(conta);
        }
    }

    private void inicializaJogo() {
        setQuantidadeMaximaContas(quantidadeMaximaContas_default);
        devolutiva = treinador.dificuldadeLinear();
        setAcuracia(devolutiva.acuracia);
        setDificuldade(devolutiva.dificuldade);
        setNumTentativas(0);
        setNumErros(0);
        if (numRodada != 1) {
            inicializaInterface(true, false);
        } else {
            inicializaInterface(false, false);
        }

        inicializaContas();
        mainActivity_inicializaCronometro();
    }

    public void reiniciaJogo(View view) {
        numRodada = 0;  // será incrementado por "sorteiaContas"
        inicializaJogo();
        sorteiaContas(view);

        // sinal sonoro
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.reiniciar);
        mp.start();

        mainActivity_setTextoFeedback("Rodada atual: " + Integer.toString(numRodada), false);
    }

    public void habilitaBotaoSortear(boolean habilita) {

        Button botaoSortear = findViewById(mainActivity_encontraId("button_Sortear"));
        Button botaoValidar = findViewById(mainActivity_encontraId("button_Validar"));
        Button botaoAjuda = findViewById(mainActivity_encontraId("button_Ajuda"));

        if (!habilita) {
            botaoSortear.setEnabled(false);
            botaoValidar.setEnabled(true);
            botaoAjuda.setEnabled(true);
            // botaoAjuda.setVisibility(View.VISIBLE);
        } else {
            botaoSortear.setEnabled(true);
            botaoValidar.setEnabled(false);
            botaoAjuda.setEnabled(false);
            botaoAjuda.setVisibility(View.INVISIBLE);
        }
    }

    public void forneceAjuda(View view) {
        mainActivity_setTextoFeedback("Vou te ajudar.", true);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.ajuda);
        mp.start();
        // TODO: finalizar
    }

    public void mainActivity_populaCamposContas(boolean populaResultados) {
        for (int i = 1; i <= quantidadeMaximaContas; i++) {
            String i_comoTexto = Integer.toString(i);
            TextView primeiroTermo = findViewById(mainActivity_encontraId("primeiroTermo" + i_comoTexto));
            TextView segundoTermo = findViewById(mainActivity_encontraId("segundoTermo" + i_comoTexto));
            TextView resultado = findViewById(mainActivity_encontraId("resultado" + i_comoTexto));
            CheckBox checkBox = findViewById(mainActivity_encontraId("checkBox" + i_comoTexto));

            mainActivity_setSpinnerOperador(i_comoTexto, contas.get(i - 1).getIndice_operador());

            primeiroTermo.setText(Integer.toString(contas.get(i - 1).getPrimeiroTermo()));
            segundoTermo.setText(Integer.toString(contas.get(i - 1).getSegundoTermo()));

            checkBox.setChecked(contas.get(i - 1).isHabilitada());
            checkBox.setBackgroundColor(TRANSPARENT);

            if (populaResultados) {
                resultado.setText(Integer.toString(contas.get(i - 1).getResultado()));
            } else {
                resultado.setText("");
            }
        }
    }

    public void mainActivity_exibeConta(int indice) {
        String indice_comoTexto = Integer.toString(indice);
        TextView primeiroTermo = findViewById(mainActivity_encontraId("primeiroTermo" + indice_comoTexto));
        TextView segundoTermo = findViewById(mainActivity_encontraId("segundoTermo" + indice_comoTexto));
        TextView resultado = findViewById(mainActivity_encontraId("resultado" + indice_comoTexto));
        TextView sinalIgual = findViewById(mainActivity_encontraId("sinalIgual" + indice_comoTexto));
        Spinner operador = findViewById(mainActivity_encontraId("operador" + indice_comoTexto));
        CheckBox checkBox = findViewById(mainActivity_encontraId("checkBox" + indice_comoTexto));

        checkBox.setVisibility(View.VISIBLE);
        checkBox.setClickable(false);  // TODO: modificar, se o método for aproveitado no módulo "Problemas"; por sinal, não funciona

        primeiroTermo.setVisibility(View.VISIBLE);
        primeiroTermo.setClickable(false);  // TODO: checar

        operador.setVisibility(View.VISIBLE);
        operador.setClickable(false); // TODO: checar

        segundoTermo.setVisibility(View.VISIBLE);
        segundoTermo.setClickable(false); // TODO: checar;

        resultado.setVisibility(View.VISIBLE);
        sinalIgual.setVisibility(View.VISIBLE);
    }

    public void mainActivity_exibeContas() {
        for (int i = 1; i <= quantidadeMaximaContas; i++) {
            mainActivity_exibeConta(i);
        }
    }

    public void guardaResultadosNoDB () {
        // armazena os resultados no banco de dados
        // TODO: finalizar
    }

    public void sorteiaContas(View view) {

        mainActivity_setBarraProgresso("progressBar_dificuldade", this.dificuldade + 1);
        mainActivity_setBarraProgresso("progressBar_acuracia", this.acuracia);

        for (com.nietupski.contaseproblemas.Conta c : contas) {
            c.sorteiaConta(getDificuldade()); // TODO: checar
        }
        mainActivity_populaCamposContas(false);
        mainActivity_exibeContas();

        Chronometer cronometro = findViewById(mainActivity_encontraId("cronometro"));

        if (!cronometroAtivo) {
            cronometro.setBase(SystemClock.elapsedRealtime() + referenciaTempo);
            cronometro.start();
            cronometroAtivo = true;
        }

        // sinal sonoro
        if (numRodada != 0) {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.sortear);
            mp.start();
        }

        habilitaBotaoSortear(false);
        numRodada++;
        setNumTentativas(0);
        setNumErros(0);
        mainActivity_setTextoFeedback("Rodada atual: " + Integer.toString(numRodada), true);

    }

    public void validarContas(View view) {
        int resultadoValor = 0;
        int quantidadeAcertosGrupoContas = 0;
        numTentativas++;

        // verifica as contas individualmente.
        for (int i = 1; i <= quantidadeMaximaContas; i++) {
            resultadoValor = 0;
            String indice_comoTexto = Integer.toString(i);
            TextView resultado = findViewById(mainActivity_encontraId("resultado" + indice_comoTexto));
            CheckBox checkBox = findViewById(mainActivity_encontraId("checkBox" + indice_comoTexto));
            String resultado_texto = resultado.getText().toString();
            if (!resultado_texto.equals("")) {
                resultadoValor = Integer.parseInt(resultado_texto);
            }
            int resultado_correto = contas.get(i - 1).getResultado();
            if (!(resultadoValor == resultado_correto)) {
                checkBox.setBackgroundColor(YELLOW);
                numErros++;
            } else {
                checkBox.setBackgroundColor(GREEN);
                quantidadeAcertosGrupoContas++;
            }
            resultado.setText(Integer.toString(resultadoValor));  // porque posso teclar 'Validar' com campos em branco, que devem ser preenchidos com zero
        }

        setErrosPorNumeroDeContas((float) numErros / (getQuantidadeMaximaContas() * numTentativas));

        // verifica se todas as contas estão corretas ou não.
        if (quantidadeAcertosGrupoContas == quantidadeMaximaContas) {

            // manipula o cronômetro
            Chronometer cronometro = findViewById(mainActivity_encontraId("cronometro"));
            referenciaTempo = cronometro.getBase() - SystemClock.elapsedRealtime();
            long tempoTranscorrido = ((SystemClock.elapsedRealtime() - cronometro.getBase()) / 1000) - getTempoAteUltimaRodada();  // tempo transcorrido, em segundos
            setTempoAteUltimaRodada(getTempoAteUltimaRodada() + tempoTranscorrido);  // atualiza o tempo absoluto transcorrido de jogo  // TODO: melhorar isso! Referências redundantes de base de tempo
            cronometro.stop();
            cronometroAtivo = false;

            // habilita o botão Sortear
            habilitaBotaoSortear(true);

            // sinal sonoro - acerto
            mainActivity_setTextoFeedback("Muito bem!", true);
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.sucesso);
            mp.start();

            // coleta o feedback do Treinador
            devolutiva = treinador.dificuldadeLinear(getDificuldade(), tempoTranscorrido, getErrosPorNumeroContas(), false);  // TODO: teste! Implementar tempo, ajuda e checar!
            setAcuracia(devolutiva.acuracia);
            setDificuldade(devolutiva.dificuldade);

            // TODO: remover; desenvolvimento apenas
            mainActivity_setTextoFeedback("DEBUG: tempo = " + Long.toString(tempoTranscorrido) +
                            "s, acurácia = " + Integer.toString(getAcuracia()) +
                            "%, modo proposto = " + Integer.toString(getDificuldade()) + "/9" +
                            ", número de tentativas = " + Integer.toString(numTentativas) +
                            ", número de erros = " + Integer.toString(numErros) +
                            ", erros/numContas = " + Float.toString(getErrosPorNumeroContas()),
                    true);

            // "limpa" status
            setErrosPorNumeroDeContas(0);
            setNumTentativas(0);

        } else {

            // sinal sonoro - erro
            mainActivity_setTextoFeedback("Insista um pouco.", true);
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.erro);
            mp.start();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas);
        inicializaJogo();

    }


}
