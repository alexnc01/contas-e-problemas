package com.nietupski.contaseproblemas;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.YELLOW;


public class Problemas extends Activity {

    Problema problema = new Problema();

    private int numRodada = 0;
    private int numSentencasHabilitadas = 0;

    private Stack<String> entradasUsuario = new Stack<>();  // TODO: terminar isso.

    static private int quantidadeMaximaContas = 4;  // TODO: considerar a utilidade que há em poder mudar o número máximo de contas


    public int mainActivity_encontraId(String identificador) {
        return getResources().getIdentifier(identificador, "id", getPackageName());
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

    public void mainActivity_setTextoProblema(String texto) {
        TextView t = findViewById(mainActivity_encontraId("text_problema"));
        t.setText(texto);
    }

    public void mainActivity_habilitaInterfaceSentenca(int numSentenca, boolean habilita) {
        String numSentenca_comoString = Integer.toString(numSentenca);
        int id = mainActivity_encontraId("operador" + numSentenca_comoString);

        if (numSentenca == 1) {
            mainActivity_habilitaBotaoValidar(habilita);
        }

        Spinner spinner = findViewById(id);
        if (habilita) {
            spinner.setVisibility(View.VISIBLE);
        } else {
            spinner.setVisibility(View.INVISIBLE);
        }

        List<String> tipos = Arrays.asList("primeiroTermo", "segundoTermo", "resultado", "sinalIgual");
        for (String c : tipos) {
            id = mainActivity_encontraId(c + numSentenca_comoString);
            TextView textView = findViewById(id);
            if (!"sinalIgual".equals(c)) {
                textView.setText("");
            }
            if (habilita) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.INVISIBLE);
            }
        }
        CheckBox checkBox = findViewById(mainActivity_encontraId("checkBox" + numSentenca_comoString));
        checkBox.setBackgroundColor(TRANSPARENT);
    }

    private void mainActivity_habilitaBotaoValidar(boolean habilita) {
        Button botaoHabilitar = findViewById(mainActivity_encontraId("button_Validar"));
        botaoHabilitar.setEnabled(habilita);
    }

    public void habilitaSentenca(int numSentenca) {
        this.numSentencasHabilitadas = numSentenca;
        if (numSentenca != 1) {  // validará cada sentença à medida em que novas sentenças forem "pedidas" pelo usuário; a última sentença será validada mediante o botão 'Validar'
            validaSentenca(numSentenca - 1);
            sequenciaEntradasUsuario(numSentenca - 1);

            if (verificaSolucao(false)) {
                terminoProblema();
            }
        }
        if (numSentenca == quantidadeMaximaContas) {
            mainActivity_habilitaInterfaceSentenca(numSentenca, true);
        } else {
            mainActivity_habilitaInterfaceSentenca(numSentenca, true);
            mainActivity_habilitaCheckbox(numSentenca + 1, true);
        }
    }

    public void validaSentenca(int numSentenca) {
        // checa o valor aritmético da expressão testada
        // TODO: não pode ser feito apenas em função do acionamento de Checkboxes; melhorar isso.
        CheckBox checkBox = findViewById(mainActivity_encontraId("checkBox" + Integer.toString(numSentenca)));
        if (validaExpressao(numSentenca)) {
            mainActivity_coloreCheckbox(numSentenca, "azul");
        } else {
            mainActivity_coloreCheckbox(numSentenca, "amarelo");
        }
    }

    public void sequenciaEntradasUsuario(int numSentencas) {
        // cria uma pilha contendo as entradas do usuário, em notação pós-fixada, para comparação à regra.
        this.entradasUsuario = new Stack<>();
        String valor;
        List<String> tipos = Arrays.asList("primeiroTermo", "segundoTermo", "operador", "resultado");
        for (int i = 1; i <= numSentencas; i++) {
            for (String c : tipos) {
                String nomeCampo = c + Integer.toString(i);
                if (!c.equals("operador")) {
                    TextView campo = findViewById(mainActivity_encontraId(nomeCampo));
                    valor = campo.getText().toString();
                    valor = (valor.equals("")) ? "0" : valor;  // campos vazios devem ser tratados como de valor 0
                } else {
                    Spinner operador_spinner = findViewById(mainActivity_encontraId(nomeCampo));
                    valor = operador_spinner.getSelectedItem().toString();
                }
                this.entradasUsuario.push(valor);
            }
        }
    }

    public void desabilitaSentenca(int numSentenca) {
        // TODO: limpar campos!
        this.numSentencasHabilitadas = numSentenca - 1;
        sequenciaEntradasUsuario(numSentenca - 1);
        if (numSentenca == quantidadeMaximaContas) {
            mainActivity_habilitaInterfaceSentenca(numSentenca, false);
        } else {
            mainActivity_habilitaInterfaceSentenca(numSentenca, false);
            mainActivity_habilitaCheckbox(numSentenca + 1, false);
        }
    }

    public boolean verificaSolucao(boolean botaoValidarPressionado) {
        // TODO: checar
        if (botaoValidarPressionado) {
            Log.d("verificaSolucao()", "Botão Verificar pressionado");  // TODO: remover; desenvolvimento apenas
            sequenciaEntradasUsuario(numSentencasHabilitadas - 1);  // se 'verificaSolucao' foi evocada pelo pressionamento do botão Validar, então a sequência de entradas deve ser tomada até a linha corrente
        }
        Avaliacao avaliacao = problema.verificaEntradasUsuario(entradasUsuario);
        String relatorio = avaliacao.geraRelatorio();
        Log.d("AVALIAÇÃO", relatorio);
        mainActivity_setTextoFeedback(relatorio, true);
        return avaliacao.verificaTerminoAvaliacao();
    }

    private void terminoProblema() {
        Log.d("Status", "SUCESSO");
        mainActivity_setTextoFeedback("SUCESSO", true);
        for (int i = 1; i <= quantidadeMaximaContas; i++) {
            mainActivity_coloreCheckbox(i, "verde");
        }
        // sinal sonoro
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sucesso);
        mp.start();
    }

    public void mainActivity_habilitaCheckbox(int numSentenca, boolean habilitar) {
        int id = mainActivity_encontraId("checkBox" + Integer.toString(numSentenca));
        CheckBox checkBox = findViewById(id);
        desabilitaSentenca(numSentenca);
        checkBox.setChecked(false);
        checkBox.setBackgroundColor(TRANSPARENT);
        if (habilitar) {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setEnabled(true);
        } else {
            checkBox.setVisibility(View.INVISIBLE);
            checkBox.setEnabled(false);
        }
    }

    public void mainActivity_coloreCheckbox(int numSentenca, String cor) {
        int id = mainActivity_encontraId("checkBox" + Integer.toString(numSentenca));
        CheckBox checkBox = findViewById(id);
        cor = cor.toLowerCase();
        if (checkBox.isChecked()) {
            switch (cor) {
                case "amarelo":
                    checkBox.setBackgroundColor(YELLOW);
                    break;
                case "verde":
                    checkBox.setBackgroundColor(GREEN);
                    break;
                case "azul":
                    checkBox.setBackgroundColor(BLUE);
                    break;
                case "vermelho":
                    checkBox.setBackgroundColor(RED);
                    break;
                case "cinza":
                    checkBox.setBackgroundColor(GRAY);
                    break;
                case "transparente":
                    checkBox.setBackgroundColor(TRANSPARENT);
                    break;
                default:
                    checkBox.setBackgroundColor(TRANSPARENT);
                    break;
            }
        }
    }

    // método evocado ao clicarmos no botão "Validar"
    public void mainActivity_validarContas(View view) {
        if (verificaSolucao(true)) {
            terminoProblema();
        }
    }

    public void mainActivity_aoClicarCheckbox(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkBox1:
                if (checked) {
                    habilitaSentenca(1);
                } else { desabilitaSentenca(1); }
                break;
            case R.id.checkBox2:
                if (checked) {
                    habilitaSentenca(2);
                } else { desabilitaSentenca(2); }
                break;
            case R.id.checkBox3:
                if (checked) {
                    habilitaSentenca(3);
                } else { desabilitaSentenca(3); }
                break;
            case R.id.checkBox4:
                if (checked) {
                    habilitaSentenca(4);
                } else { desabilitaSentenca(4); }
                break;
        }
    }

    private void sorteiaProblema(boolean usarProblemaExemplo) {
        this.problema = new Problema();
        problema.escolherProblema(usarProblemaExemplo);
    }

    private void inicializaJogo() {
        numRodada = 0;
        mainActivity_inicializaSpinnersOperadores();


        // TODO: concluir!
    }

    public void mainActivity_inicializaSpinnersOperadores() {
        // inicializa Spinners que conterão os operadores
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.simbolosQuatroOperacoes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (int i = 1; i <= quantidadeMaximaContas; i++) {
            Spinner spinner = findViewById(mainActivity_encontraId("operador" + Integer.toString(i)));
            spinner.setAdapter(adapter);
            spinner.setVisibility(View.INVISIBLE);
            mainActivity_setSpinnerOperador(Integer.toString(i), 0);  // inicializa com o sinal de '+';
        }
    }

    public void mainActivity_setSpinnerOperador(String indice_comoTexto, int indiceOperador) {
        Spinner spinner = findViewById(mainActivity_encontraId("operador" + indice_comoTexto));
        spinner.setSelection(indiceOperador);
    }

    public void reiniciaJogo(View view) {
        inicializaJogo();

        sorteiaProblema(false);
        mainActivity_setTextoProblema(problema.getEnunciado());

        mainActivity_habilitaCheckbox(1, true);


        // sinal sonoro
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.reiniciar);
        mp.start();

        mainActivity_setTextoFeedback("Rodada atual: " + Integer.toString(numRodada), false);

    }

    public boolean validaExpressao(int numSentenca) {
        // valida uma expressão aritmética já preenchida pelo usuário, com base no ordinal da sentença desejada, com base em seu valor.
        Conta sentenca = leValoresCamposTexto(numSentenca);
        return sentenca.validaContaOperadorTextual();
    }

    public Conta leValoresCamposTexto(int numSentenca) {
        // TODO: e se uma TextView estiver vazia?
        int termo = 0;
        Conta sentenca = new Conta();
        String numSentenca_comoTexto = Integer.toString(numSentenca);
        List<String> tipos = Arrays.asList("primeiroTermo", "segundoTermo", "resultado");
        for (String c : tipos) {
            int id = mainActivity_encontraId(c + numSentenca_comoTexto);
            TextView textView = findViewById(id);
            String conteudo = textView.getText().toString();
            if (!conteudo.equals("")) {  // checa se a TextView está vazia
                termo = Integer.parseInt(conteudo);
            } else {
                termo = 0;
            }
            switch (c) {
                case "primeiroTermo":
                    sentenca.setPrimeiroTermo(termo);
                    break;
                case "segundoTermo":
                    sentenca.setSegundoTermo(termo);
                    break;
                case "resultado":
                    sentenca.setResultado(termo);
                    break;
            }
            id = mainActivity_encontraId("operador" + numSentenca_comoTexto);
            Spinner spinner = findViewById(id);
            sentenca.setOperador(spinner.getSelectedItem().toString().charAt(0));
        }
        return sentenca;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problemas);
        inicializaJogo();

    }

}
