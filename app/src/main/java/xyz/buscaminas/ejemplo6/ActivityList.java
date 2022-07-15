package xyz.buscaminas.ejemplo6;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import xyz.buscaminas.ejemplo6.Procesos.Empleado;
import xyz.buscaminas.ejemplo6.Procesos.SQLiteConexion;
import xyz.buscaminas.ejemplo6.Procesos.Transacciones;

public class ActivityList extends AppCompatActivity {
    SQLiteConexion conexion;
    ListView lista;
    ArrayList<Empleado> listaempleado;
    ArrayList<String> arregloempleado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        lista=(ListView) findViewById(R.id.lista);
        conexion=new SQLiteConexion(this, Transacciones.DataBase,null,1);
        ObtenerLista();
        ArrayAdapter adp=new ArrayAdapter(this, android.R.layout.simple_list_item_1,arregloempleado);
        lista.setAdapter(adp);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info="ID: "+listaempleado.get(i).getId()+"\n"+listaempleado.get(i).getNombres();
                Snackbar.make(view,info,Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void ObtenerLista() {
        SQLiteDatabase db=conexion.getReadableDatabase();
        Empleado empleado=null;
        listaempleado=new ArrayList<Empleado>();
        Cursor cursor=db.rawQuery("SELECT * FROM "+Transacciones.empleados,null);
        while (cursor.moveToNext()){
            empleado=new Empleado();
            empleado.setId(cursor.getInt(0));
            empleado.setNombres(cursor.getString(1));
            empleado.setApellidos(cursor.getString(2));
            empleado.setUrl(cursor.getString(3));
            listaempleado.add(empleado);
        }
        cursor.close();
        fllList();
    }

    private void fllList() {
        arregloempleado=new ArrayList<String>();
        for(int i=0;i<listaempleado.size();i++){
            arregloempleado.add(listaempleado.get(i).getId()+" | "+
                    listaempleado.get(i).getNombres()+" "+
                    listaempleado.get(i).getApellidos());
        }
    }
}