package es.upv.etsit.aatt.paco.trabajoaatt;

public class DatosCarteleras {
    private String titulo;
    private String portada;
    private String estreno;

    public DatosCarteleras(){

    }

    public DatosCarteleras(String titulo, String portada, String estreno) {
        this.titulo = titulo;
        this.portada = portada;
        this.estreno = estreno;
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

    public String getEstreno() {
        return estreno;
    }

    public void setEstreno(String estreno) {
        this.estreno = estreno;
    }
}
