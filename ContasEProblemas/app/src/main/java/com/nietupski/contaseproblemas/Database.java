package com.nietupski.contaseproblemas;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe usada na manipulação do banco de dados local
 */

final class Database {

    public Database() {
    }

    static class dbContasResultados implements BaseColumns {
        static final String NOME_TABELA = "contasresultados";
        static final String RODADA = "rodada";
        static final String NUM_TENTATIVAS_RODADA = "num_tentativas_rodada";
        static final String DESAFIO_MODO = "desafio_modo";
        static final String TEMPO = "tempo";
    }

    static class dbProblemasEnunciados implements BaseColumns {
        static final String NOME_TABELA = "problemasenunciados";
        static final String ENUNCIADO = "enunciado";
        static final String VALORESINICIAIS = "valoresiniciais";
        static final String VALORESINICIAISMODO = "modovaloresiniciais";
        static final String RESOLUCAO = "esquemaresolucao";
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "ContasProblemas.db";

        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String REAL_TYPE = " REAL";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CRIA_CONTAS_RESULTADOS =
                "CREATE TABLE " + dbContasResultados.NOME_TABELA + " ( " +
                        dbContasResultados._ID + " INTEGER PRIMARY KEY," +
                        dbContasResultados.RODADA + INTEGER_TYPE + COMMA_SEP +
                        dbContasResultados.NUM_TENTATIVAS_RODADA + INTEGER_TYPE + COMMA_SEP +
                        dbContasResultados.DESAFIO_MODO + INTEGER_TYPE + COMMA_SEP +
                        dbContasResultados.TEMPO + INTEGER_TYPE + " )";

        private static final String SQL_CRIA_PROBLEMAS_ENUNCIADOS =
                "CREATE TABLE " + dbProblemasEnunciados.NOME_TABELA + " ( " +
                        dbProblemasEnunciados._ID + " INTEGER PRIMARY KEY," +
                        dbProblemasEnunciados.ENUNCIADO + TEXT_TYPE + COMMA_SEP +
                        dbProblemasEnunciados.VALORESINICIAIS + TEXT_TYPE + COMMA_SEP +
                        dbProblemasEnunciados.VALORESINICIAISMODO + INTEGER_TYPE + COMMA_SEP +
                        dbProblemasEnunciados.RESOLUCAO + TEXT_TYPE + " )";

        private static final String SQL_DELETA_REGISTROS_CONTASRESULTADOS =
                "DROP TABLE IF EXISTS " + dbContasResultados.NOME_TABELA;

        private static final String SQL_DELETA_REGISTROS_PROBLEMAS_ENUNCIADOS =
                "DROP TABLE IF EXISTS " + dbProblemasEnunciados.NOME_TABELA;

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CRIA_CONTAS_RESULTADOS);
            db.execSQL(SQL_CRIA_PROBLEMAS_ENUNCIADOS);
            populaProblemasEnunciados(db);
        }

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        void populaProblemasEnunciados(SQLiteDatabase db) {

            List<ProblemaEnunciado> problemas = new ArrayList<>();

            // TODO: deselegante; rever, sobretudo porque pode-se iniciar a partir da carga de um XML.
            problemas.add(new ProblemaEnunciado("Quantos bombons de R$#A posso comprar com R$#B? Qual o valor do troco?", "BA/;-B", "BA/J;JA*K;BK-L;=L", 7));
            problemas.add(new ProblemaEnunciado("Paulo e Tina foram à feira, Paulo com R$#A e Tina, com R$#B. Quantas dúzias de bananas comprariam juntos, sabendo-se que uma dúzia de bananas custa R$#C?", "AB+;+C", "AB+J;JC/K;=K", 7));
            problemas.add(new ProblemaEnunciado("Com R$#A, posso comprar #B doces e ainda ficar com R$#C de troco. Qual o valor de cada doce?", "AB/;+C", "AC-J;JB/K;=K", 5));
            problemas.add(new ProblemaEnunciado("Para a Festa de Natal da escola, Maria trouxe #A brindes e Bárbara, #B brindes. Quantos brindes trouxeram, ao todo? Depois de dividir os brindes entre #D crianças, quantos brindes sobraram?", "AB+;+D", "AB+C;CD/E;ED*F;CF-G;=G", 7));
            problemas.add(new ProblemaEnunciado("Sobre a mesa há #A garrafas de suco. Se pusermos #B garrafas na geladeira e #C garrafas no freezer, quantas garrafas permanecerão sobre a mesa?", "BC+;-A", "AB-J;JC-K;=K", 4));
            problemas.add(new ProblemaEnunciado("Mamãe comprou #A batatas. Usou #B para fritar e #C para a sopa. Quantas batatas sobraram?", "BC+;-A", "AB-J;JC-K;=K", 4));
            problemas.add(new ProblemaEnunciado("Joaquim tinha #A canetas. Perdeu #B e deu #C a seu amigo. Com quantas ficou?", "BC+;-A", "AB-J;JC-K;=K", 4));
            problemas.add(new ProblemaEnunciado("Na casa de Ana há #A cachorros. Na casa de Pedro há o dobro dessa quantidade. Quantos cachorros Ana e Pedro têm, juntos?", "AB*;+A", "AB*J;JA+K;=K", 4));
            problemas.add(new ProblemaEnunciado("Quero guardar pares de sapatos em caixas onde cabem #A pares em cada uma. Quantos pares conseguirei guardar em #B caixas dessas?", "AB*;+A", "AB*J;=J", 4));
            problemas.add(new ProblemaEnunciado("Os irmãos João e José querem doar #A brinquedos para crianças pobres. João conseguiu juntar #B brinquedos e José, #C brinquedos. Quantos brinquedos ainda faltam?", "BC+;-A", "BC+J;AJ-K;=K", 4));

            long chave;

            for (ProblemaEnunciado p : problemas) {
                ContentValues valores = new ContentValues();
                valores.put(dbProblemasEnunciados.ENUNCIADO, p.getEnunciado());
                valores.put(dbProblemasEnunciados.VALORESINICIAIS, p.getStringValoresIniciais());
                valores.put(dbProblemasEnunciados.RESOLUCAO, p.getStringSolucao());
                valores.put(dbProblemasEnunciados.VALORESINICIAISMODO, p.getModoValores());
                chave = db.insert(dbProblemasEnunciados.NOME_TABELA, null, valores);
            }
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETA_REGISTROS_CONTASRESULTADOS);
            db.execSQL(SQL_DELETA_REGISTROS_PROBLEMAS_ENUNCIADOS);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        public void fecharDatabase() {
            // TODO: implementar
        }

    }

}

