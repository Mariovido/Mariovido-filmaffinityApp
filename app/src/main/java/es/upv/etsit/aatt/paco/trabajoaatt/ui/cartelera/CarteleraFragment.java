package es.upv.etsit.aatt.paco.trabajoaatt.ui.cartelera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import es.upv.etsit.aatt.paco.trabajoaatt.AdapterCartelera;
import es.upv.etsit.aatt.paco.trabajoaatt.DatosCarteleras;
import es.upv.etsit.aatt.paco.trabajoaatt.R;

public class CarteleraFragment extends Fragment {
    ArrayList<DatosCarteleras> listEspaña, listNetflix, listProximo;
    RecyclerView recyclerEspaña, recyclerNetflix, recyclerProximo;
    private CarteleraViewModel carteleraViewModel;
    ProgressBar pgsBar;
    TextView car1, car2, car3;

    // TAG.
    protected static final String TAG = "CarteleraFragment";
    // Número de peliculas a sacar (múltiplo de 3).
    int peliculas = 12;
    // Número de cosas que sacar.
    int informacion = 3;
    // Variable que guardará los datos ha utilizar.
    public String[][] datosfinal = new String[peliculas][informacion];
    // Variable para sacar las urls.
    int z = 0;
    // Urls de las tres carteleras.
    String[] urlsCarteleras = new String[3];


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cartelera, container, false);

        pgsBar = root.findViewById(R.id.pBar2);
        pgsBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        pgsBar.setVisibility(View.VISIBLE);
        car1 = root.findViewById(R.id.cart1);
        car2 = root.findViewById(R.id.cart2);
        car3 = root.findViewById(R.id.cart3);

        // Declaración de las 3 carteleras.
        urlsCarteleras[0] = "https://www.filmaffinity.com/es/cat_new_th_es.html";
        urlsCarteleras[1] = "https://www.filmaffinity.com/es/cat_new_netflix.html";
        urlsCarteleras[2] = "https://www.filmaffinity.com/es/cat_upc_th_es.html";

        //Recycler de la cartelera en España
        listEspaña = new ArrayList<>();
        recyclerEspaña = (RecyclerView) root.findViewById(R.id.recyclerEspaña);
        recyclerEspaña.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        //Recycler destacados Netflix
        listNetflix = new ArrayList<>();
        recyclerNetflix = (RecyclerView) root.findViewById(R.id.recyclerNetflix);
        recyclerNetflix.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        //Recycler próximos estrenos España
        listProximo = new ArrayList<>();
        recyclerProximo = (RecyclerView) root.findViewById(R.id.recyclerProximos);
        recyclerProximo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        for (int j = 0; j < peliculas; j++) {
            try {
                TareaAsyncCartelera ta = new TareaAsyncCartelera();
                ta.execute(urlsCarteleras);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return root;

    }

    class TareaAsyncCartelera extends AsyncTask<String[], Integer, String[][]> {

        @Override
        protected String[][] doInBackground(String[]... url) {
            // Array que guardará los datos.
            String[][] datos = new String[peliculas][informacion];

            // Primera petición
            if (url != null) {
                // Aquí sacamos los datos de la url.
                for (int j = 0; j < url.length; j++) {
                    for (int i = 0; i < url[j].length; i++) {
                        // Compruebo si me da un 200 al hacer la petición
                        if (getStatusConnectionCode(url[j][i]) == 200) {

                            // Para la cartelera España.
                            if (url[j][i] == urlsCarteleras[0]) {
                                publishProgress(10);
                                if(z < (peliculas/3)) {
                                    // Obtengo el HTML de la web en un objeto Document.
                                    Document document = getHtmlDocument(urlsCarteleras[0]);

                                    // Me coloco donde voy a sacar los datos.
                                    Elements entradasUrl = document.select("div.movie-poster > a");

                                    z = 0;
                                    int y = 0;
                                    // Recorro todas las posibilidades
                                    for (Element elem : entradasUrl) {
                                        if (y == (peliculas/3)) {
                                            break;
                                        }
                                        y++;
                                        // URL.
                                        datos[z][0] = elem.attr("href");
                                        // Titulo.
                                        datos[z][1] = elem.attr("title");

                                        // Saco por pantalla los datos.
                                        Log.d(TAG, datos[z][0] + "\n" + datos[z][1]);

                                        // Añado 1 a z.
                                        z += 1;
                                    }

                                    Elements entradasFoto = document.select("div.movie-poster > a").select("img");;

                                    z = 0;
                                    y = 0;
                                    // Recorro todas las posibilidades
                                    for (Element elem : entradasFoto) {
                                        if (y == (peliculas/3)) {
                                            break;
                                        }
                                        y++;
                                        // Foto.
                                        datos[z][2] = elem.attr("src");

                                        // Saco por pantalla los datos.
                                        Log.d(TAG, "Foto " + datos[z][2]);

                                        // Añado 1 a z.
                                        z += 1;
                                    }
                                }
                            }
                            // Para la cartelera Netflix.
                            if (url[j][i] == urlsCarteleras[1]) {
                                publishProgress(30);
                                if(z < (2*(peliculas/3))) {
                                    // Obtengo el HTML de la web en un objeto Document.
                                    Document document = getHtmlDocument(urlsCarteleras[1]);

                                    // Me coloco donde voy a sacar los datos.
                                    Elements entradasUrl = document.select("div.movie-poster > a");

                                    z = (peliculas/3);
                                    int y = 0;
                                    // Recorro todas las posibilidades
                                    for (Element elem : entradasUrl) {
                                        if (y == (peliculas/3)) {
                                            break;
                                        }
                                        y++;
                                        // URL.
                                        datos[z][0] = elem.attr("href");
                                        // Titulo.
                                        datos[z][1] = elem.attr("title");

                                        // Saco por pantalla los datos.
                                        Log.d(TAG, datos[z][0] + "\n" + datos[z][1]);

                                        // Añado 1 a z.
                                        z += 1;
                                    }
                                    publishProgress(50);

                                    Elements entradasFoto = document.select("div.movie-poster > a").select("img");;

                                    z = (peliculas/3);
                                    y = 0;
                                    // Recorro todas las posibilidades
                                    for (Element elem : entradasFoto) {
                                        if (y == (peliculas/3)) {
                                            break;
                                        }
                                        y++;
                                        // Foto.
                                        datos[z][2] = elem.attr("src");

                                        // Saco por pantalla los datos.
                                        Log.d(TAG, "Foto " + datos[z][2]);

                                        // Añado 1 a z.
                                        z += 1;
                                    }
                                }
                            }
                            // Para la cartelera Proximos.
                            if (url[j][i] == urlsCarteleras[2]) {
                                publishProgress(80);
                                if(z < peliculas) {
                                    // Obtengo el HTML de la web en un objeto Document.
                                    Document document = getHtmlDocument(urlsCarteleras[2]);

                                    // Me coloco donde voy a sacar los datos.
                                    Elements entradasUrl = document.select("div.movie-poster > a");

                                    z = (2*(peliculas/3));
                                    int y = 0;
                                    // Recorro todas las posibilidades
                                    for (Element elem : entradasUrl) {
                                        publishProgress(90);
                                        if (y == (peliculas/3)) {
                                            break;
                                        }
                                        y++;
                                        // URL.
                                        datos[z][0] = elem.attr("href");
                                        // Titulo.
                                        datos[z][1] = elem.attr("title");

                                        // Saco por pantalla los datos.
                                        Log.d(TAG, datos[z][0] + "\n" + datos[z][1]);

                                        // Añado 1 a z.
                                        z += 1;
                                    }

                                    Elements entradasFoto = document.select("div.movie-poster > a").select("img");;

                                    z = (2*(peliculas/3));
                                    y = 0;
                                    // Recorro todas las posibilidades
                                    for (Element elem : entradasFoto) {
                                        publishProgress(100);
                                        if (y == (peliculas/3)) {
                                            break;
                                        }
                                        y++;
                                        // Foto.
                                        datos[z][2] = elem.attr("src");

                                        // Saco por pantalla los datos.
                                        Log.d(TAG, "Foto " + datos[z][2]);

                                        // Añado 1 a z.
                                        z += 1;

                                    }

                                }
                            }
                        } else {
                            Log.d(TAG, "El Status Code no es OK es: " + getStatusConnectionCode(url[j][i]));
                        }
                    }
                }
                return datos;
            } else {
                return null;
            }
        } // doInBackground


        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onProgressUpdate(Integer... progreso) {
            pgsBar.setProgress(progreso[0],true);
        }



        @Override
        protected void onPostExecute (String datos[][]) {
            pgsBar.setVisibility(View.GONE);
            car1.setVisibility(View.VISIBLE);
            car2.setVisibility(View.VISIBLE);
            car3.setVisibility(View.VISIBLE);
            for (int indice = 0; indice < peliculas; indice++) {
                if (datos[indice][0] != null) {
                    datosfinal[indice] = datos[indice];
                    // Llenamos la cartelera España.
                    if (indice < (peliculas/3)) {
                        llenarEspaña(datosfinal, indice);

                        AdapterCartelera adapterCarteleraEspaña = new AdapterCartelera(listEspaña, getContext(),datosfinal, 0, peliculas);
                        recyclerEspaña.setAdapter(adapterCarteleraEspaña);
                    }
                    // LLenamos la cartelera Netflix
                    if (indice >= (peliculas/3) && indice < (2*(peliculas/3))) {
                        llenarNetflix(datosfinal, indice);
                        WebView a;

                        AdapterCartelera adapterNetflix = new AdapterCartelera(listNetflix, getContext(),datosfinal, 1, peliculas);
                        recyclerNetflix.setAdapter(adapterNetflix);
                    }
                    // Llenamos la cartelera Proximos.
                    if (indice >= (2*(peliculas/3))) {
                        llenarProximos(datosfinal, indice);

                        AdapterCartelera adapterProximos = new AdapterCartelera(listProximo, getContext(),datosfinal, 2, peliculas);
                        recyclerProximo.setAdapter(adapterProximos);
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

        Connection.Response response = null;

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

    private void llenarEspaña(String info[][], int i){
        listEspaña.add(new DatosCarteleras(info[i][1], info[i][2].replace("mtiny","large"), info[i][2].replace("mtiny","large")));
    } // llenarEspaña

    private void llenarNetflix(String info[][], int i) {
        listNetflix.add(new DatosCarteleras(info[i][1], info[i][2].replace("mtiny","large"), info[i][2]));
    } // llenarNetflix

    private void llenarProximos(String info[][], int i) {
        listProximo.add(new DatosCarteleras(info[i][1], info[i][2].replace("mtiny","large"), info[i][2]));
    } // llenarProximos
}

