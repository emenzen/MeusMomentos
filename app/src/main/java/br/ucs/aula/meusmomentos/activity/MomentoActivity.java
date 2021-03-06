package br.ucs.aula.meusmomentos.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.ucs.aula.meusmomentos.R;
import br.ucs.aula.meusmomentos.banco.BDSQLiteHelper;
import br.ucs.aula.meusmomentos.model.Momento;

public class MomentoActivity extends AppCompatActivity {

    private BDSQLiteHelper bd;
    private final int CAMERA = 1;
    private File arquivoMomento = null;
    private ImageView imagem;
    private double latitude;
    private double longitude;
    private Geocoder geocoder;
    private List<Address> addresses;
    private String fullAddress;
    private final int PERMISSAO_REQUEST = 2;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momento);
        bd = new BDSQLiteHelper(this);
        final EditText descricao = (EditText) findViewById(R.id.edDescricao);
//        ActivityCompat.requestPermissions( this,
//                new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSAO_REQUEST);

        configureGPS();
        //referencia o componente de imagem
        this.imagem = (ImageView) findViewById(R.id.pv_image);
        //chama a camera
        tirarMomento(null);
        Button novo = (Button) findViewById(R.id.btnAdd);
        novo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Brazil/East"));
                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH) + 1; // O m??s vai de 0 a 11.
                int semana = calendar.get(Calendar.WEEK_OF_MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int hora = calendar.get(Calendar.HOUR_OF_DAY);
                int minuto = calendar.get(Calendar.MINUTE);
                int segundo = calendar.get(Calendar.SECOND);
                Momento momento = new Momento();
                momento.setDescricao(descricao.getText().toString());
                momento.setData(String.valueOf(dia) + '/' + String.valueOf(mes) + '/' + String.valueOf(ano) +
                        ' ' + String.valueOf(hora) + ':' + String.valueOf(minuto) + ':' + String.valueOf(segundo));
                momento.setLocalizacao(fullAddress);
                momento.setCaminho(arquivoMomento.getAbsolutePath());
                bd.addMomento(momento);
                Intent intent = new Intent(MomentoActivity.this, MainActivity.class);
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
        } else {
            Intent intent = new Intent(MomentoActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                MomentoActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MomentoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSAO_REQUEST);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                latitude = locationGPS.getLatitude();
                longitude = locationGPS.getLongitude();
            }
        }
    }

    public void configureGPS(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void getLocation(){
        geocoder = new Geocoder(this, Locale.getDefault());
        this.getCurrentLocation();
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses.size() > 0){
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                fullAddress = address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
