package es.upv.etsit.aatt.paco.trabajoaatt;
/*
Datos que queremos mostrar en el RecyclerView
*/

public class DatosPeliculas {
    private String titulo;
    private String genero;
    private String year;
    private String portada;


    //Alimentar la lista
    public DatosPeliculas() {

    }

    //Constructor
    public DatosPeliculas(String titulo,String genero, String year ,String portada) {
        this.titulo = titulo;
        this.portada = portada;
        this.genero = genero;
        this.year = year;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }
}

