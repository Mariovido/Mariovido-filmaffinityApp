package es.upv.etsit.aatt.paco.trabajoaatt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

import es.upv.etsit.aatt.paco.trabajoaatt.ui.busqueda.BusquedaFragment;

public class DescriptionActivity extends AppCompatActivity {
    final String TAG = "DescriptionActivity";
    String url = "";
    private String image_url = "";
    WebView foto_web;
    TextView title, date, genre, synopsys,val;
    CheckBox checkBox;
    String[] datosfinal = new String[7];

    // NOMBRE DEL ARCHIVO
    private static final String FILE_NAME = "datos.json";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        title = findViewById(R.id.titulo_description);
        date = findViewById(R.id.fecha_description);
        genre = findViewById(R.id.genero_description);
        val = findViewById(R.id.valoracion_description);
        synopsys = findViewById(R.id.descripcion_description);
        checkBox = findViewById(R.id.checkBox);

        checkBox.setOnClickListener((view)-> {
                if(((CompoundButton) view).isChecked()) {
                    if (datosfinal[0] != null) {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        String json = "";
                        try {
                            String file = loadFile();
                            if (file != "") {
                                JsonReader lector = Json.createReader(new StringReader(file));
                                JsonArray raiz = lector.readArray();
                                int longitud = raiz.size();

                                // Inicializamos el JSON.
                                String url = raiz.getJsonObject(0).getString("URL");
                                jsonObject.put("URL", url);
                                String titulo = raiz.getJsonObject(0).getString("Titulo");
                                jsonObject.put("Titulo", titulo);
                                String foto = raiz.getJsonObject(0).getString("Foto");
                                jsonObject.put("Foto", foto);
                                jsonArray.put(0, jsonObject);
                                json = jsonArray.getString(0);

                                // Añadimos el resto de JSON.
                                for (int i = 1; i < longitud; i++) {
                                    url = raiz.getJsonObject(i).getString("URL");
                                    jsonObject.put("URL", url);
                                    titulo = raiz.getJsonObject(i).getString("Titulo");
                                    jsonObject.put("Titulo", titulo);
                                    foto = raiz.getJsonObject(i).getString("Foto");
                                    jsonObject.put("Foto", foto);
                                    jsonArray.put(i, jsonObject);

                                    json = json + "," + jsonArray.getString(i);
                                }

                                // Añadimos el último JSON.
                                jsonObject.put("URL", datosfinal[0]);
                                jsonObject.put("Titulo", datosfinal[1]);
                                jsonObject.put("Foto", datosfinal[6]);
                                jsonArray.put(longitud, jsonObject);

                                json = "[" + json + "," + jsonArray.getString(longitud) + "]";
                            } else {
                                // Incializamos el JSON.
                                jsonObject.put("URL", datosfinal[0]);
                                jsonObject.put("Titulo", datosfinal[1]);
                                jsonObject.put("Foto", datosfinal[6]);
                                jsonArray.put(0, jsonObject);

                                json = "[" + jsonArray.getString(0) + "]";
                            }
                            saveFile(json);
                            Toast.makeText(this, "Saved" ,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Checkbox", "Checked");
                    }
                } else {
                    if (datosfinal[0] != null) {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        String json = "";
                        try {
                            String file = loadFile();
                            JsonReader lector = Json.createReader(new StringReader(file));
                            JsonArray raiz = lector.readArray();
                            int longitud = raiz.size();
                            if (file != "") {
                                // Inicializamos el JSON.
                                String url = raiz.getJsonObject(0).getString("URL");
                                String titulo = "";
                                String foto = "";
                                // Compruebo que no son la misma.
                                if (!url.equals(datosfinal[0])) {
                                    jsonObject.put("URL", url);
                                    titulo = raiz.getJsonObject(0).getString("Titulo");
                                    jsonObject.put("Titulo", titulo);
                                    foto = raiz.getJsonObject(0).getString("Foto");
                                    jsonObject.put("Foto", foto);
                                    jsonArray.put(0, jsonObject);
                                    json = jsonArray.getString(0);
                                }

                                // Añadimos el resto de JSON.
                                for (int i = 1; i < longitud; i++) {
                                    url = raiz.getJsonObject(i).getString("URL");
                                    if (!url.equals(datosfinal[0])) {
                                        jsonObject.put("URL", url);
                                        titulo = raiz.getJsonObject(i).getString("Titulo");
                                        jsonObject.put("Titulo", titulo);
                                        foto = raiz.getJsonObject(i).getString("Foto");
                                        jsonObject.put("Foto", foto);
                                        jsonArray.put(i, jsonObject);
                                        if (json != "") {
                                            json = json + "," + jsonArray.getString(i);
                                        } else {
                                            json = jsonArray.getString(i);
                                        }
                                    }
                                }
                                if (!json.equals("")) {
                                    json = "[" + json + "]";
                                }
                            }
                            saveFile(json);
                            Toast.makeText(this, "Unsaved" ,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Checkbox", "Un-Checked");
                    }
                }
        });

        foto_web = findViewById(R.id.imagen_peli);
        Log.d("Intent", "catching intent");
            Bundle bundle = getIntent().getExtras();
            //sacamos los parametros que hemos pasado con intent
            url = bundle.getString("url");
            image_url = bundle.getString("imagen"); //saco url de la imagen con el bundle
            Log.d("Intent","Antes de replace "+ image_url);
            image_url = image_url.replace("mtiny","large");
            pintarCartel(image_url,foto_web); //pinto la imagen
            Log.d("Intent", "Despues de replace" +image_url);

        // Comprobamos que el archivo ya existe en la base de datos.
        // Abrimos el archivo.
        String fileComprobacion = loadFile();
        if (fileComprobacion != "") {
            JsonReader lectorComprobacion = Json.createReader(new StringReader(fileComprobacion));
            JsonArray raizComprobacion = lectorComprobacion.readArray();
            int longitudComprobacion = raizComprobacion.size();
            for (int j = 0; j < longitudComprobacion; j++) {
                String urlComprobacion = raizComprobacion.getJsonObject(j).getString("URL");
                if (url.equals(urlComprobacion)) {
                    checkBox.setChecked(true);
                }
            }
        }

            // Sacamos todos lo datos que necesitamos.
            try {
                TareaAsyncDescription ta = new TareaAsyncDescription();
                ta.execute(url);
            } catch(Exception e) {
                e.printStackTrace();

        }


    }
    class TareaAsyncDescription extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... url) {
            // Array que guardará los datos.
            String[] datos = new String[7];

            if (url != null) {
                // Aquí sacamos los datos de la url.
                for (int j = 0; j < url.length; j++) {
                    // Compruebo si me da un 200 al hacer la petición
                    if (getStatusConnectionCode(url[j]) == 200) {
                        // Url
                        datos[0] = url[j];

                        // Obtengo el HTML de la web en un objeto Document.
                        Document document = getHtmlDocument(url[j]);

                        // Localizo donde estan los datos.
                        Elements entradas = document.select("div.cpanel").not("div.cpanel.adver-wrapper");

                        // Compruebo cada una de las posibilidades.
                        for (Element elem : entradas) {
                            // Título
                            datos[1] = elem.getElementsByTag("H1").text();
                            // Género
                            datos[2] = elem.getElementsByAttributeValue("itemprop", "genre").text();
                            // Fecha
                            datos[3] = elem.getElementsByAttributeValue("itemprop", "datePublished").text();
                            // Valoración
                            datos[4] = elem.getElementsByAttributeValue("itemprop", "ratingValue").text();
                            // Descripción
                            datos[5] = elem.getElementsByAttributeValue("itemprop", "description").text();
                        }
                        // Para buscar la imagen.
                        Elements entradasImagen = document.select("div#movie-main-image-container > a");
                        for (Element elem : entradasImagen) {
                            // Imagen
                            datos[6] = elem.attr("href");
                            image_url = datos[6];
                        }

                        // Saco por pantalla los datos.
                        Log.d(TAG, datos[0] + "\n" + datos[1] + "\n" + datos[2] + "\n" + datos[3] + "\n" + datos[4] + "\n" + datos[5] + "\n" + datos[6]);
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
        protected void onPostExecute(String[] strings) {
            //super.onPostExecute(strings);
            datosfinal = strings;
            Log.d("Tag",strings[1]);
            title.setText(strings[1]);
            date.setText(strings[3]);
            genre.setText(strings[2]);
            synopsys.setText(strings[5]);
            if (strings[4].equals("")){
                val.setText(" - - ");
            }else {
                val.setText(strings[4]);
            }
            checkBox.setVisibility(View.VISIBLE);

        }
    } // TareaAsync


    private void pintarCartel(final String url, final WebView wv) {

        wv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                wv.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                float densidad = getResources().getDisplayMetrics().density;
                int height = (int)(wv.getHeight()/densidad);
                int width = (int)(wv.getWidth()/densidad);

                String html = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<style>\n" +
                        "body {\n" +
                        "    width: "  + width  +"px;\n" +
                        "    height: " + height + "px;\n" +
                        "    background-image: url(\"" + url  +"\");\n" +
                        "    background-repeat: no-repeat;\n" +
                        "    background-size: contain;\n" +
                        "    background-position: center;\n" +
                        "}\n" +
                        "</style>\n" +
                        "<body> \n" +
                        "</body>\n" +
                        "</html>";

                wv.loadData(html, "text/html", null);

            }
        });

    }

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

    // Guardamos el archivo.
    public void saveFile(String datos) {
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            // URL
            fos.write(datos.getBytes());
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Leemos el archivo
    public String loadFile() {
        FileInputStream fis = null;
        String file = "";
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            file = sb.toString();
            if (fis != null) {
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
