package xyz.buscaminas.ejemplo6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import xyz.buscaminas.ejemplo6.Procesos.SQLiteConexion;
import xyz.buscaminas.ejemplo6.Procesos.Transacciones;

public class ActivityCrear extends AppCompatActivity {
    Button enviar,atras,captura;
    EditText txtnombre,txtapellido;
    Intent pagina;
    int turno=1;
    ImageView imagen;
    String currentPhotoPath;
    static final int REQUESTCODECAMARA=100;
    static final int REQUESTTAKEFOTO=101;
    public boolean validar(String dato,int numero){
        String opcion4="[A-Z,Á,É,Í,Ó,Ú,Ñ][a-z,á,é,í,ó,ú,ñ]{2,50}";
        String opcion5="[A-Z,Á,É,Í,Ó,Ú,Ñ][a-z,á,é,í,ó,ú,ñ]{2,50}[ ][A-Z,Á,É,Í,Ó,Ú,Ñ][a-z,á,é,í,ó,ú,ñ]{2,50}";
        switch(numero){
            case 1:{
                return dato.matches(opcion4+"|"+opcion5);
            }
            case 2:{
                return dato.matches(opcion4+"|"+opcion5);
            }
            default:{
                return false;
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear);
        txtnombre=(EditText) findViewById(R.id.txtnombre);
        txtapellido=(EditText) findViewById(R.id.txtapellido);
        enviar=(Button) findViewById(R.id.enviar);
        atras=(Button) findViewById(R.id.atras);
        imagen=(ImageView) findViewById(R.id.foto);
        captura=(Button) findViewById(R.id.tomar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar(txtnombre.getText().toString().trim(),turno)){
                    turno=2;
                    if(validar(txtapellido.getText().toString().trim(),turno)){
                        turno=1;
                        AgregarEmpleado();
                    }
                    else{
                        turno=1;
                    }
                }
                else{
                    turno=1;
                }
            }
        });
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volver();
            }
        });
        captura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });
    }
    private void galleryAddPic(){
        Intent mediaScanIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f=new File(currentPhotoPath);
        Uri contenUri=Uri.fromFile(f);
        mediaScanIntent.setData(contenUri);
        this.sendBroadcast(mediaScanIntent);
    }
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            File photoFile=null;
            try {
                photoFile=createImageFile();
            }
            catch (IOException ex){
            }
            if(photoFile!=null){
                Uri photoURI= FileProvider.getUriForFile(this,"xyz.buscaminas.ejemplo6.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePictureIntent,REQUESTTAKEFOTO);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    //Agrega la foto al cuadro
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESTTAKEFOTO && resultCode==RESULT_OK){
            File foto=new File(currentPhotoPath);
            imagen.setImageURI(Uri.fromFile(foto));
            txtnombre.setEnabled(true);
            txtapellido.setEnabled(true);
            enviar.setEnabled(true);
            galleryAddPic();
        }
    }

    private void permisos(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUESTCODECAMARA);
        }
        else{
            dispatchTakePictureIntent();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUESTCODECAMARA){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }
            else{
                Toast.makeText(getApplicationContext(),"Permiso Denegado",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void volver(){
        pagina=new Intent(getApplicationContext(),ActivityConsulta.class);
        startActivity(pagina);
    }
    private void AgregarEmpleado(){
        SQLiteConexion conexion=new SQLiteConexion(this, Transacciones.DataBase,null,1);
        SQLiteDatabase db=conexion.getWritableDatabase();
        ContentValues valores=new ContentValues();
        valores.put(Transacciones.nombres,txtnombre.getText().toString());
        valores.put(Transacciones.apellidos,txtapellido.getText().toString());
        valores.put(Transacciones.url,currentPhotoPath);
        Long resultado=db.insert(Transacciones.empleados,Transacciones.id,valores);
        Toast.makeText(getApplicationContext(),"Registro guardado",Toast.LENGTH_LONG).show();
        db.close();
        ClearScreen();
    }

    private void ClearScreen(){
        txtnombre.setText("");
        txtapellido.setText("");
        currentPhotoPath="";
        File foto=new File(currentPhotoPath);
        imagen.setImageURI(Uri.fromFile(foto));
        imagen.setImageURI(Uri.fromFile(foto));
        txtnombre.setEnabled(false);
        txtapellido.setEnabled(false);
        enviar.setEnabled(false);
    }
}