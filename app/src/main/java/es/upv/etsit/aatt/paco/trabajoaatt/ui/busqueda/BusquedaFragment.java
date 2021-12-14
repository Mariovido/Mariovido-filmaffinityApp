package es.upv.etsit.aatt.paco.trabajoaatt.ui.busqueda;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import es.upv.etsit.aatt.paco.trabajoaatt.AdapterRV;
import es.upv.etsit.aatt.paco.trabajoaatt.DatosPeliculas;
import es.upv.etsit.aatt.paco.trabajoaatt.R;

public class BusquedaFragment extends Fragment {
    ArrayList<DatosPeliculas> listDatos;
    RecyclerView recyclerDatos;
    SearchView buscador;
    ProgressBar spinner;
    ImageView flecha;
    TextView mensajito,mensaje_notfound;

    // TAG.
    protected static final String TAG = "BusquedaFragment";
    // String de la url inicial.
    String url = "https://www.filmaffinity.com/es/search.php?stext=";
    // Variable que diferenciará entre distintas peliculas.
    int z = 0;
    // Número de peliculas a sacar.
    int peliculas = 5;
    // Número de cosas que sacar.
    int informacion = 5;
    // Variable que guardará los datos ha utilizar.
    public String[][] datosfinal = new String[peliculas][informacion];
    // String para saber que pelicula se está buscando.
    public String busqueda = "";
    // Variable para que solo se escriba una vez el error de busqueda.
    int fallo = 0;

    private BusquedaViewModel busquedaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Inflater para poder acceder a los objetos creados en fragment_search
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        spinner = root.findViewById(R.id.spinner);
        flecha = root.findViewById(R.id.foto_flecha);
        mensajito = root.findViewById(R.id.mensaje_busqueda);
        mensaje_notfound = root.findViewById(R.id.notfound);

        //Buscador

        buscador = (SearchView) root.findViewById(R.id.buscador);
        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                String new_url="";
                mensaje_notfound.setVisibility(View.INVISIBLE);
                busqueda = query;
                query = query.replace(" ","+");
                Log.d("Query","TextSubmit");
                new_url = url+query;
                Log.d("Query",new_url);

                //lanzamos peticion de busqueda
                try {
                    listDatos.clear();
                    fallo = 0;
                    z = 0;
                    for (int i = 0; i < peliculas; i++) {
                        TareaAsyncBusqueda ta = new TareaAsyncBusqueda();
                        ta.execute(new_url);
                        flecha.setVisibility(View.INVISIBLE);
                        mensajito.setVisibility(View.INVISIBLE);
                        spinner.setVisibility(View.VISIBLE);
                    }
                } catch(Exception e) { // Posibles excepciones: MalformedURLException, IOException y ProtocolException
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //poner aqui si queremos que pase algo al cambiar el texto
                Log.v("Query","TextChange");
                return false;
            }
        });

        //Muestra los datos en el recycler
        listDatos = new ArrayList<>();
        recyclerDatos = (RecyclerView) root.findViewById(R.id.recyclerId);
        recyclerDatos.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    } // onCreateView

    class TareaAsyncBusqueda extends AsyncTask <String, Integer, String[][]> {

        @Override
        protected String[][] doInBackground(String... url) {
            // Array que guardará los datos.
            String[][] datos = new String[peliculas][informacion];

            if (url != null) {
                // Aquí sacamos los datos de la url.
                for (int j = 0; j < url.length; j++) {
                    // Compruebo si me da un 200 al hacer la petición
                    if (getStatusConnectionCode(url[j]) == 200) {
                        // Comprobamos que existen resultados.

                        // Obtengo el HTML de la web en un objeto Document.
                        Document documentComprobacion = getHtmlDocument(url[j]);

                        // Me coloco donde voy a sacar los datos.
                        Elements entradasComprobacion = documentComprobacion.select("div#mt-content-cell");

                        String comprobacion = "";
                        for (Element elem : entradasComprobacion) {
                            // Dato comprobación.
                            comprobacion = elem.getElementsByTag("H1").text();
                        }
                        String busquedaComprobacion = "Búsqueda de \"" + busqueda + "\"";

                        if (comprobacion.equals(busquedaComprobacion)) {
                            if (z < peliculas) {

                                // Obtengo el HTML de la web en un objeto Document.
                                Document document = getHtmlDocument(url[j]);

                                // Me coloco donde voy a sacar los datos.
                                Elements entradasUrl = document.select("div.mc-poster > a");

                                // Variable para coger todos los datos.
                                int y = 0;
                                z = 0;
                                // Recorro todas las posibilidades
                                for (Element elem : entradasUrl) {
                                    if (y == peliculas) {
                                        break;
                                    }
                                    y++;
                                    // URL.
                                    datos[z][0] = elem.attr("href");
                                    // Titulo.
                                    datos[z][1] = elem.attr("title");

                                    // Saco por pantalla los datos.
                                    Log.d(TAG, datos[z][0] + "\n" + datos[z][1]);

                                    // Añado uno a z.
                                    z += 1;
                                }

                                // Para buscar Nacionalidad.
                                Elements entradasNacionalidad = document.select("div.mc-title > img");

                                y = 0;
                                z = 0;
                                for (Element elem : entradasNacionalidad) {
                                    if (y == peliculas) {
                                        break;
                                    }
                                    y++;
                                    // Nacionalidad
                                    datos[z][2] = elem.attr("title");

                                    // Saco por pantalla los datos.
                                    Log.d(TAG, datos[z][2]);

                                    // Añado uno a z.
                                    z += 1;
                                }

                                // Para buscar valoración.
                                Elements entradasValoracion = document.select("div.mc-info-container");

                                y = 0;
                                z = 0;
                                for (Element elem : entradasValoracion) {
                                    if (y == peliculas) {
                                        break;
                                    }
                                    y++;
                                    // Valoración
                                    datos[z][3] = elem.getElementsByClass("avgrat-box").text();

                                    // Saco por pantalla los datos.
                                    Log.d(TAG, datos[z][3]);

                                    // Añado uno a z.
                                    z += 1;
                                }

                                // Para buscar la imagen.
                                Elements entradasImagen = document.select("div.mc-poster > a").select("img");

                                y = 0;
                                z = 0;
                                for (Element elem : entradasImagen) {
                                    if (y == peliculas) {
                                        break;
                                    }
                                    y++;
                                    // Imagen
                                    datos[z][4] = elem.attr("src");

                                    // Saco por pantalla los datos.
                                    Log.d(TAG, datos[z][4]);

                                    // Añado uno a z.
                                    z += 1;
                                }

                            /*// Hago z para que no vuelva a buscar datos.
                            z = peliculas;*/
                            }
                        } else {
                            datos[0][1] = "No hay resultados exactos para las palabras introducidas.";
                            //mensaje_notfound.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "El Status Code no es OK es: " + getStatusConnectionCode(url[j]));
                    }
                }
                return datos;
            } else {
                return null;
            }
        } // doInBackground


        @Override
        protected void onPostExecute(String datos[][]) {
            spinner.setVisibility(View.INVISIBLE);
            if (datos[0][1] != null) {
                if (datos[0][1].equals("No hay resultados exactos para las palabras introducidas.") && (fallo == 0)) {
                    //TextView de error en la búsqueda
                    mensaje_notfound.setVisibility(View.VISIBLE);
                    fallo = 1;
                } else {
                    for (int indice = 0; indice < peliculas; indice++) {
                        if (datos[indice][0] != null) {
                            mensaje_notfound.setVisibility(View.INVISIBLE);
                            datosfinal[indice] = datos[indice];

                            llenarLista(datosfinal, indice);

                            AdapterRV adapter = new AdapterRV(listDatos, getContext(), datosfinal);
                            recyclerDatos.setAdapter(adapter);
                        }
                    }
                }
            }
        } // onPostExecute
    } // TareaAsync

    /**
     * Con este método compruebo el Status code de la respuesta que recibo al hacer la petición
     * EJM:
     * 200 OK			300 Multiple Choices
     * 301 Moved Permanently	305 Use Proxy
     * 400 Bad Request		403 Forbidden
     * 404 Not Found		500 Internal Server Error
     * 502 Bad Gateway		503 Service Unavailable
     *
     * @param url
     * @return Status Code
     */
    public int getStatusConnectionCode(String url) {

        Response response = null;

        try {
            response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        } catch (IOException ex) {
            Log.d(TAG, "Excepción al obtener el Status Code: " + ex.getMessage());
        }
        return response.statusCode();
    } // getStatusConnectionCode

    /**
     * Con este método devuelvo un objeto de la clase Document con el contenido del
     * HTML de la web que me permitirá parsearlo con los métodos de la libreria JSoup
     *
     * @param url
     * @return Documento con el HTML
     */
    public Document getHtmlDocument(String url) {

        Document doc = null;

        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).get();
        } catch (IOException ex) {
            Log.d(TAG, "Excepción al obtener el HTML de la página" + ex.getMessage());
        }

        return doc;

    } // getHtmlDocument
//Ejemplo de llenado de lista, hay que buscar alternativa
   private void llenarLista(String info[][], int i) {
        listDatos.add(new DatosPeliculas(info[i][1],info[i][2],info[i][3],info[i][4]));

    } // llenarLista
}
