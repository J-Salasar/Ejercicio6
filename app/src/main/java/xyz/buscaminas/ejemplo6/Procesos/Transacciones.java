package xyz.buscaminas.ejemplo6.Procesos;

public class Transacciones {
    public static final String empleados="empleados2";
    public static final String id="id";
    public static final String nombres="nombres";
    public static final String apellidos="apellido";
    public static final String url="url";
    public static final String DataBase="Lista2";
    public static final String CrearTablaEmpleado="CREATE TABLE "+empleados+" "+
            "("+
            id+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            nombres+" TEXT,"+
            apellidos+" TEXT,"+
            url+" TEXT"+
            ")";
    public static final String DropTableEmpleado="DROP TABLE IF EXISTS "+empleados;
}