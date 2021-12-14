package es.upv.etsit.aatt.paco.trabajoaatt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

/*Esta clase se encarga de "alimentar" el xml item_list
*
*  */
public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewHolderDatos> {
    ArrayList<DatosPeliculas> listDatos;
    private Context contexto;
    private long m_DownTime;
    String datosfinal[][];

    // NOMBRE DEL ARCHIVO
    private static final String FILE_NAME = "datos.json";

    private void pintarCartel(final String url, final WebView wv) {

        wv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                wv.getViewTreeObserver().removeOnGlobalLayoutListener(this);


                float densidad = contexto.getResources().getDisplayMetrics().density;
                int height = (int) (wv.getHeight() / densidad);
                int width = (int) (wv.getWidth() / densidad);

                String html = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<style>\n" +
                        "body {\n" +
                        "    width: " + width + "px;\n" +
                        "    height: " + height + "px;\n" +
                        "    background-image: url(\"" + url + "\");\n" +
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

    public AdapterRV(ArrayList<DatosPeliculas> listDatos, Context context, String[][] datos) {
        this.listDatos = listDatos;
        contexto = context;
        datosfinal = datos;
    }

    @NonNull
    @Override
    public AdapterRV.ViewHolderDatos onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent,false);

        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRV.ViewHolderDatos holder, int position) {
        DatosPeliculas item=listDatos.get(position);
        ViewHolderDatos viewHolderDatos = holder;

        viewHolderDatos.titulo.setText(item.getTitulo());
        viewHolderDatos.pais.setText(item.getGenero());
        viewHolderDatos.valoracion.setText(item.getYear());

        pintarCartel(item.getPortada(),viewHolderDatos.prueba);
        holder.parentLayout.setOnClickListener((view)->{
            Log.d("RV","onClick");
            Intent intent = new Intent(contexto,DescriptionActivity.class); //(Origen, destino)
            Bundle bundle = new Bundle();
            bundle.putString("url", datosfinal[position][0]);
            bundle.putString("imagen",datosfinal[position][4]);
            intent.putExtras(bundle);
            contexto.startActivity(intent);
        });

        holder.prueba.setOnTouchListener((view, event)->{
            Log.d("RV","onTouch");
            long MAX_DURATION = 100;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                m_DownTime = event.getEventTime();
            }
            if (event.getAction() == MotionEvent.ACTION_UP){
                if(event.getEventTime()-m_DownTime <=MAX_DURATION) {
                    Intent intent = new Intent(contexto,DescriptionActivity.class); //(Origen, destino)
                    Bundle bundle = new Bundle();
                    bundle.putString("url", datosfinal[position][0]);
                    bundle.putString("imagen",datosfinal[position][4]);
                    intent.putExtras(bundle);
                    contexto.startActivity(intent);
                }
            }
            return false;
        });

    }

    @Override
    public int getItemCount() {

        return listDatos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView titulo, pais, valoracion;
        WebView prueba;
        LinearLayout parentLayout;
        CheckBox box;


        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            prueba = itemView.findViewById(R.id.idImagen);
            titulo = (TextView) itemView.findViewById(R.id.idTitulo);
            pais = (TextView) itemView.findViewById(R.id.idPais);
            valoracion = (TextView) itemView.findViewById(R.id.idValoracion);
            box = itemView.findViewById(R.id.idCheck);
            parentLayout = itemView.findViewById(R.id.container);

            // HAY QUE CONSEGUIR QUE ENVIE EL INDICE LA PELICULA EN LA QUE ESTA.
            // Temporal, borrar cuando se logre.
            int indice = 0;
            box.setOnClickListener((view)-> {
                if(((CompoundButton) view).isChecked()) {
                    if (datosfinal[indice][0] != null) {
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
                                jsonObject.put("URL", datosfinal[indice][0]);
                                jsonObject.put("Titulo", datosfinal[indice][1]);
                                jsonObject.put("Foto", datosfinal[indice][4]);
                                jsonArray.put(longitud, jsonObject);

                                json = "[" + json + "," + jsonArray.getString(longitud) + "]";
                            } else {
                                // Incializamos el JSON.
                                jsonObject.put("URL", datosfinal[indice][0]);
                                jsonObject.put("Titulo", datosfinal[indice][1]);
                                jsonObject.put("Foto", datosfinal[indice][4]);
                                jsonArray.put(0, jsonObject);

                                json = "[" + jsonArray.getString(0) + "]";
                            }
                            saveFile(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Checkbox", "Checked");
                    }
                } else {
                    if (datosfinal[indice][0] != null) {
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
                                if (!url.equals(datosfinal[indice][0])) {
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
                                    if (!url.equals(datosfinal[indice][0])) {
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Checkbox", "Un-Checked");
                    }
                }
            });

            // PASA LO MISMO QUE ARRIBA.
            // Comprobamos que el archivo ya existe en la base de datos.
            // Abrimos el archivo.
            String fileComprobacion = loadFile();
            if (fileComprobacion != "") {
                JsonReader lectorComprobacion = Json.createReader(new StringReader(fileComprobacion));
                JsonArray raizComprobacion = lectorComprobacion.readArray();
                int longitudComprobacion = raizComprobacion.size();
                for (int j = 0; j < longitudComprobacion; j++) {
                    String urlComprobacion = raizComprobacion.getJsonObject(j).getString("URL");
                    if (datosfinal[indice][0].equals(urlComprobacion)) {
                        box.setChecked(true);
                    }
                }
            }
        }
    }

    // Guardamos el archivo.
    public void saveFile(String datos) {
        FileOutputStream fos = null;

        try {
            fos = contexto.openFileOutput(FILE_NAME, contexto.MODE_PRIVATE);
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
            fis = contexto.openFileInput(FILE_NAME);
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
