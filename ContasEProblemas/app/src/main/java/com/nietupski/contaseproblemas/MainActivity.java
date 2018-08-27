package com.nietupski.contaseproblemas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private Intent svcMusica;

    public void iniciaContas(android.view.View view) {
        Intent intentContas = new Intent(MainActivity.this, Contas.class);
        startActivity(intentContas);
    }

    public void iniciaProblemas(android.view.View view) {
        Intent intentProblemas = new Intent(MainActivity.this, Problemas.class);
        startActivity(intentProblemas);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // indica que o teclado não deve ficar aberto, enquanto o foco não for um campo de texto
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // cria o banco de dados da aplicação
        Database db = new Database();

        // inicializa o serviço de música, em background
        svcMusica = new Intent(MainActivity.this, com.nietupski.contaseproblemas.Musica.class);
        startService(svcMusica);
    }

    @Override
    protected void onDestroy() {
        stopService(svcMusica);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        stopService(svcMusica);
        super.onPause();
    }

    @Override
    protected void onResume() {
        startService(svcMusica);
        super.onResume();
    }

}
