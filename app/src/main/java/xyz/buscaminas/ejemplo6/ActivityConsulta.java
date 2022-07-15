package xyz.buscaminas.ejemplo6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class ActivityConsulta extends AppCompatActivity {
    SQLiteConexion conexion;
    EditText id,nombre,apellido;
    Button consultar,eliminar,actializar,veras,inicio,reflejo;
    Intent pagina;
    int turno=1;
    String foto,currentPhotoPath;
    ImageView imagen;
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
        setContentView(R.layout.activity_consulta);
        init();
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscar();
            }
        });
        actializar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validar(nombre.getText().toString().trim(),turno)){
                    turno=2;
                    if(validar(apellido.getText().toString().trim(),turno)){
                        turno=1;
                        actializa();
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
        veras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ver();
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                elimina();
            }
        });
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inicial();
            }
        });
        reflejo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });
    }
    private void inicial(){
        pagina=new Intent(getApplicationContext(),ActivityCrear.class);
        startActivity(pagina);
    }
    private void elimina(){
        SQLiteDatabase db= conexion.getWritableDatabase();
        if(db!=null){
            db.execSQL("DELETE FROM "+ Transacciones.empleados+
                    " WHERE "+Transacciones.id+"="+Integer.parseInt(id.getText().toString()));
            db.close();
            Toast.makeText(getApplicationContext(),"Se elimino el registro.",Toast.LENGTH_LONG).show();
            nombre.setEnabled(false);
            apellido.setEnabled(false);
            eliminar.setEnabled(false);
            actializar.setEnabled(false);
            reflejo.setEnabled(false);
            id.setText("");
            nombre.setText("");
            apellido.setText("");
            foto="";
            File foto1=new File(foto);
            imagen.setImageURI(Uri.fromFile(foto1));
        }
        else{
            Toast.makeText(getApplicationContext(),"Error al eliminar",Toast.LENGTH_LONG).show();
        }
    }
    private void ver(){
        pagina=new Intent(getApplicationContext(),ActivityList.class);
        startActivity(pagina);
    }
    private void actializa(){
        SQLiteDatabase db= conexion.getWritableDatabase();
        if(db!=null){
            db.execSQL("UPDATE "+Transacciones.empleados+" SET "+
                    Transacciones.nombres+"='"+nombre.getText().toString()+"', "+
                    Transacciones.apellidos+"='"+apellido.getText().toString()+"', "+
                    Transacciones.url+"='"+currentPhotoPath+"' "+
                    "WHERE "+Transacciones.id+"="+Integer.parseInt(id.getText().toString()));
            db.close();
            Toast.makeText(getApplicationContext(),"Se actualizo los datos.",Toast.LENGTH_LONG).show();
            nombre.setEnabled(false);
            apellido.setEnabled(false);
            eliminar.setEnabled(false);
            actializar.setEnabled(false);
            reflejo.setEnabled(false);
        }
        else{
            Toast.makeText(getApplicationContext(),"Error en actualizar",Toast.LENGTH_LONG).show();
        }
    }
    private void buscar() {
        try{
            SQLiteDatabase db= conexion.getWritableDatabase();
            String[] parametro={id.getText().toString()};
            String[] folders={  Transacciones.nombres,
                    Transacciones.apellidos,
                    Transacciones.url
            };
            String condicion=Transacciones.id+"=?";
            Cursor data=db.query(Transacciones.empleados,folders,condicion,parametro,null,null,null);
            data.moveToFirst();
            if(data.getCount()>0){
                nombre.setText(data.getString(0));
                apellido.setText(data.getString(1));
                foto=data.getString(2);
                currentPhotoPath=foto;
                File foto1=new File(foto);
                imagen.setImageURI(Uri.fromFile(foto1));
                Toast.makeText(getApplicationContext(),"Se encontro este resultado.",Toast.LENGTH_LONG).show();
                nombre.setEnabled(true);
                apellido.setEnabled(true);
                eliminar.setEnabled(true);
                actializar.setEnabled(true);
                consultar.setEnabled(true);
                reflejo.setEnabled(true);
            }
            else{
                Toast.makeText(getApplicationContext(),"No hay nada en ese 'ID'.",Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void init(){
        conexion=new SQLiteConexion(this, Transacciones.DataBase,null,1);
        consultar=(Button) findViewById(R.id.buscarB);
        eliminar=(Button) findViewById(R.id.elimina);
        actializar=(Button) findViewById(R.id.actualiza);
        veras=(Button) findViewById(R.id.ver);
        inicio=(Button) findViewById(R.id.inicio);
        id=(EditText) findViewById(R.id.buscarT);
        nombre=(EditText) findViewById(R.id.txtnombre21);
        apellido=(EditText) findViewById(R.id.txtapellido21);
        imagen=(ImageView) findViewById(R.id.cap21);
        reflejo=(Button) findViewById(R.id.refejo);
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
    //Agregala foto al cuadro
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESTTAKEFOTO && resultCode==RESULT_OK){
            File foto=new File(currentPhotoPath);
            imagen.setImageURI(Uri.fromFile(foto));
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
}