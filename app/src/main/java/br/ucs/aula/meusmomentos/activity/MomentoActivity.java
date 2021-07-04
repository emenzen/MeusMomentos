package br.ucs.aula.meusmomentos.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.ucs.aula.meusmomentos.R;
import br.ucs.aula.meusmomentos.banco.BDSQLiteHelper;
import br.ucs.aula.meusmomentos.model.Momento;

public class MomentoActivity extends AppCompatActivity {

    private BDSQLiteHelper bd;
    private final int CAMERA = 1;
    private File arquivoMomento = null;
    private ImageView imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momento);
        bd = new BDSQLiteHelper(this);
        final EditText descricao = (EditText) findViewById(R.id.edDescricao);
        final EditText data = (EditText) findViewById(R.id.edData);
        //final EditText local = (EditText) findViewById(R.id.edlocal);

        //referencia o componente de imagem
        this.imagem = (ImageView) findViewById(R.id.pv_image);
        //chma a camera
        tirarMomento(null);

        Button novo = (Button) findViewById(R.id.btnAdd);
        novo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Momento momento = new Momento();
                momento.setDescricao(descricao.getText().toString());
                momento.setData(data.getText().toString());
                //momento.setLocal(local.getText().toString());
                momento.setCaminho(arquivoMomento.getAbsolutePath());
                bd.addMomento(momento);
                Intent intent = new Intent(MomentoActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void tirarMomento(View view) {
        Intent takePictureIntent = new
                Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                arquivoMomento = criaArquivo();
            } catch (IOException ex) {
                mostraAlerta(getString(R.string.erro), getString(R.string.erro_salvando_momento));
            }
            if (arquivoMomento != null) {
                Uri photoURI = FileProvider.getUriForFile(getBaseContext(),
                        getBaseContext().getApplicationContext().getPackageName() +
                                ".provider", arquivoMomento);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA);
            }
        }
    }

    private File criaArquivo() throws IOException {
        String timeStamp = new
                SimpleDateFormat("yyyyMMdd_Hhmmss").format(
                new Date());
        File pasta = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imagem = new File(pasta.getPath() + File.separator
                + "JPG_" + timeStamp + ".jpg");
        return imagem;
    }

    private void mostraAlerta(String titulo, String mensagem) {
        AlertDialog alertDialog = new AlertDialog.Builder(MomentoActivity.this).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void mostraMomento(String caminho) {
        Bitmap bitmap =
                BitmapFactory.decodeFile(caminho);
        imagem.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA) {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(arquivoMomento))
            );
            mostraMomento(arquivoMomento.getAbsolutePath());
        }
        else{
            Intent intent = new Intent(MomentoActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

}
