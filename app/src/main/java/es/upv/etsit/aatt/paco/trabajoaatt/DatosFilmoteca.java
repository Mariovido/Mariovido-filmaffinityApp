package es.upv.etsit.aatt.paco.trabajoaatt;

public class DatosFilmoteca {
    private String titulo;
    private String portada;
    private String url;


    //Alimenta la lista
    public DatosFilmoteca(){

    }

    //Contructor

    public DatosFilmoteca(String titulo, String portada, String url) {
        this.titulo = titulo;
        this.portada = portada;
        this.url = url;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public String getUrl(){ return url;}

    public void setUrl(String url) { this.url = url;}
}
