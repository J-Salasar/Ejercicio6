package xyz.buscaminas.ejemplo6.Procesos;

public class Empleado {
    private Integer id;
    private String nombres,apellidos,url;
    public Empleado(){

    }
    public Empleado(Integer id, String nombres, String apellidos, String url) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.url=url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}