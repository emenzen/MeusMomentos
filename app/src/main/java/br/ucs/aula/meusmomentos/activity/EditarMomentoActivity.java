package br.ucs.aula.meusmomentos.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import br.ucs.aula.meusmomentos.R;
import br.ucs.aula.meusmomentos.banco.BDSQLiteHelper;
import br.ucs.aula.meusmomentos.model.Momento;

public class EditarMomentoActivity extends AppCompatActivity {

    private BDSQLiteHelper bd;
    private ImageView imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_momento);
        Intent intent = getIntent();
        final int codigo = intent.getIntExtra("CODIGO", 0);
        bd = new BDSQLiteHelper(this);
        Momento momento = bd.getMomento(codigo);
        final EditText descricao = (EditText) findViewById(R.id.etDescricao);
        final EditText data = (EditText) findViewById(R.id.etData);
        descricao.setText(momento.getDescricao());
        data.setText(momento.getData());

        //referencia o componente de imagem
        this.imagem = (ImageView) findViewById(R.id.epv_image);

        Bitmap bitmap = BitmapFactory.decodeFile(momento.getCaminho());
        imagem.setImageBitmap(bitmap);

        final Button alterar = (Button) findViewById(R.id.btnAlterar);
        alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Momento momento = new Momento();
                momento.setCodigo(codigo);
                momento.setDescricao(descricao.getText().toString());
                momento.setData(data.getText().toString());
                bd.updateMomento(momento);
                Intent intent = new Intent(EditarMomentoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Button remover = (Button) findViewById(R.id.btnRemover);
        remover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(EditarMomentoActivity.this)
                        .setTitle(R.string.confirmar_exclusao)
                        .setMessage(R.string.quer_mesmo_apagar)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Momento momento = new Momento();
                                momento.setCodigo(codigo);
                                bd.deleteMomento(momento);
                                Intent intent = new Intent(EditarMomentoActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }
}
