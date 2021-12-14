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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterFilmoteca extends RecyclerView.Adapter<AdapterFilmoteca.ViewHolderFilmoteca> {
    ArrayList <DatosFilmoteca> listDatos;
    private Context contexto;
    private long m_DownTime;

    public AdapterFilmoteca(ArrayList<DatosFilmoteca> listDatos, Context contexto) {
        this.listDatos = listDatos;
        this.contexto = contexto;
    }

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

    @Override
    public ViewHolderFilmoteca onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filmoteca, parent ,false);

        return new ViewHolderFilmoteca(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderFilmoteca holder, int position) {
        DatosFilmoteca item = listDatos.get(position);
        ViewHolderFilmoteca viewHolderFilmoteca = holder;

        pintarCartel(item.getPortada().replace("mtiny","large"),viewHolderFilmoteca.foto);
        viewHolderFilmoteca.title.setText(item.getTitulo());

        holder.parentLayout.setOnClickListener((view)->{
            Log.d("Filmoteca","onClick");
            Intent intent = new Intent(contexto,DescriptionActivity.class); //(Origen, destino)
            Bundle bundle = new Bundle();
            bundle.putString("url",item.getUrl());
            bundle.putString("imagen",item.getPortada());
            intent.putExtras(bundle);
            contexto.startActivity(intent);
        });

        holder.foto.setOnTouchListener((view, event)->{
            Log.d("RV","onTouch");
            long MAX_DURATION = 100;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                m_DownTime = event.getEventTime();
            }
            if (event.getAction() == MotionEvent.ACTION_UP){
                if(event.getEventTime()-m_DownTime <=MAX_DURATION) {
                    Intent intent = new Intent(contexto,DescriptionActivity.class); //(Origen, destino)
                    Bundle bundle = new Bundle();
                    bundle.putString("url",item.getUrl());
                    bundle.putString("imagen",item.getPortada());
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

    public class ViewHolderFilmoteca extends RecyclerView.ViewHolder {
        ConstraintLayout parentLayout;
        TextView title;
        WebView foto;

        public ViewHolderFilmoteca(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.idTitulo_filmoteca);
            foto = itemView.findViewById(R.id.idPortada_filmoteca);
            parentLayout = itemView.findViewById(R.id.container_filmoteca);
        }
    }
}
