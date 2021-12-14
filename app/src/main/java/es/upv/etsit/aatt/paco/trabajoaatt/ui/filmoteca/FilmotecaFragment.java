package es.upv.etsit.aatt.paco.trabajoaatt.ui.filmoteca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

import es.upv.etsit.aatt.paco.trabajoaatt.AdapterFilmoteca;
import es.upv.etsit.aatt.paco.trabajoaatt.DatosFilmoteca;
import es.upv.etsit.aatt.paco.trabajoaatt.R;

public class FilmotecaFragment extends Fragment {
    ArrayList<DatosFilmoteca> listDatos;
    RecyclerView recyclerDatos;

    // NOMBRE DEL ARCHIVO
    private static final String FILE_NAME = "datos.json";

    private FilmotecaViewModel filmotecaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        filmotecaViewModel =
                ViewModelProviders.of(this).get(FilmotecaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_filmoteca, container, false);

        //Muestra los datos del recycler
        listDatos = new ArrayList<>();
        recyclerDatos = (RecyclerView) root.findViewById(R.id.recyclerId);
        recyclerDatos.setLayoutManager(new GridLayoutManager(getContext(),2));

        // Limpiamos la lista anterior.
        listDatos.clear();
        // Abrimos el archivo.
        String file = loadFile();
        if (file != "") {
            JsonReader lector = Json.createReader(new StringReader(file));
            JsonArray raiz = lector.readArray();
            int longitud = raiz.size();
            for (int j = 0; j < longitud; j++) {
                String titulo = raiz.getJsonObject(j).getString("Titulo");
                String foto = raiz.getJsonObject(j).getString("Foto");
                String url = raiz.getJsonObject(j).getString("URL");
                llenarlista(titulo, foto, url);
            }
        }

        AdapterFilmoteca adapterFilmoteca = new AdapterFilmoteca(listDatos, getContext());
        recyclerDatos.setAdapter(adapterFilmoteca);
        return root;
    }
    private void llenarlista(String titulo, String foto, String url){
        listDatos.add(new DatosFilmoteca(titulo,foto, url));
    }

    // Leemos el archivo
    public String loadFile() {
        FileInputStream fis = null;
        String file = "";
        try {
            fis = getContext().openFileInput(FILE_NAME);
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
