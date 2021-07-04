package br.ucs.aula.meusmomentos.banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import br.ucs.aula.meusmomentos.model.Momento;

public class BDSQLiteHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MeusMomentosDB";
    private static final String TABELA_MOMENTOS = "meusmomentos";
    private static final String CODIGO = "codigo";
    private static final String DESCRICAO = "descricao";
    private static final String DATA = "data";
    private static final String LOCALIZACAO = "localizacao";
    private static final String CAMINHO = "caminho";
    private static final String[] COLUNAS = {CODIGO, DESCRICAO, DATA, LOCALIZACAO, CAMINHO};

    public BDSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE meusmomentos ("+
                "codigo INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "descricao TEXT,"+
                "data TEXT,"+
                "localizacao TEXT,"+
                "caminho TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS meusmomentos");
        this.onCreate(db);
    }

    public void addMomento(Momento momento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DESCRICAO, momento.getDescricao());
        values.put(DATA, momento.getData());
        values.put(LOCALIZACAO, momento.getLocalizacao());
        values.put(CAMINHO, momento.getCaminho());
        db.insert(TABELA_MOMENTOS, null, values);
        db.close();
    }

    public Momento getMomento(int codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABELA_MOMENTOS, // a. tabela
                COLUNAS, // b. colunas
                " codigo = ?", // c. colunas para comparar
                new String[] { String.valueOf(codigo) }, // d. parâmetros
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
            Momento momento = cursorToMomento(cursor);
            return momento;
        }
    }

    private Momento cursorToMomento(Cursor cursor) {
        Momento momento = new br.ucs.aula.meusmomentos.model.Momento();
        momento.setCodigo(Integer.parseInt(cursor.getString(0)));
        momento.setDescricao(cursor.getString(1));
        momento.setData(cursor.getString(2));
        momento.setLocalizacao(cursor.getString(3));
        momento.setCaminho(cursor.getString(4));
        return momento;
    }

    public ArrayList<Momento> getAllMomentos() {
        ArrayList<Momento> listaMomentos = new ArrayList<Momento>();
        String query = "SELECT * FROM " + TABELA_MOMENTOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Momento momento = cursorToMomento(cursor);
                listaMomentos.add(momento);
            } while (cursor.moveToNext());
        }
        return listaMomentos;
    }

    public int updateMomento(Momento momento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DESCRICAO, momento.getDescricao());
        values.put(DATA, momento.getData());
        values.put(LOCALIZACAO, momento.getLocalizacao());

        int i = db.update(TABELA_MOMENTOS, //tabela
                values, // valores
                CODIGO+" = ?", // colunas para comparar
                new String[] { String.valueOf(momento.getCodigo()) }); //parâmetros
        db.close();
        return i; // número de linhas modificadas
    }

    public int deleteMomento(Momento momento) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(TABELA_MOMENTOS, //tabela
                CODIGO+" = ?", // colunas para comparar
                new String[] { String.valueOf(momento.getCodigo()) });
        db.close();
        return i; // número de linhas excluídas
    }
}
