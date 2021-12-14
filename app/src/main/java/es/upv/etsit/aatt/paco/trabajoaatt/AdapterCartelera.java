package es.upv.etsit.aatt.paco.trabajoaatt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterCartelera extends RecyclerView.Adapter<AdapterCartelera.ViewHolderCartelera> {
    ArrayList<DatosCarteleras> listDatos;
    private Context contexto;
    private long m_DownTime;
    String datosfinal[][];
    int offset;

    private void pintarCartel(final String url, final WebView wv) { // final ImageView iv

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


    public AdapterCartelera(ArrayList<DatosCarteleras>listDatos, Context context,String[][] datos, int index, int peliculas) {
        this.listDatos = listDatos;
        contexto = context;
        datosfinal = datos;
        offset = index*(peliculas/3);
    }
    @Override
    public ViewHolderCartelera onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cartelera,parent,false );

        return new ViewHolderCartelera(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCartelera holder, int position) {
        DatosCarteleras item = listDatos.get(position);
        ViewHolderCartelera viewHolderCartelera = holder;

        viewHolderCartelera.titulo.setText(item.getTitulo());
        viewHolderCartelera.estreno.setText(item.getEstreno());

        pintarCartel(item.getPortada(),viewHolderCartelera.prueba);

        holder.parentLayout.setOnClickListener((view)->{
            Log.d("RV","onClick");
            Intent intent = new Intent(contexto,DescriptionActivity.class); //(Origen, destino)
            Bundle bundle = new Bundle();
            bundle.putString("url",datosfinal[position+offset][0]);
            bundle.putString("imagen",datosfinal[position+offset][2]);
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
                    Intent intent = new Intent(contexto, DescriptionActivity.class); //(Origen, destino)
                    Bundle bundle = new Bundle();
                    bundle.putString("url", datosfinal[position + offset][0]);
                    bundle.putString("imagen", datosfinal[position + offset][2]);
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

    public class ViewHolderCartelera extends RecyclerView.ViewHolder {
        TextView titulo,estreno;
        WebView prueba;
        ConstraintLayout parentLayout;


        public ViewHolderCartelera(@NonNull View itemView) {
            super(itemView);
            prueba = itemView.findViewById(R.id.idPortada_cartelera);
            titulo = (TextView) itemView.findViewById(R.id.idTitulo_cartelera);
            estreno = (TextView) itemView.findViewById(R.id.idEstreno_cartelera);
            parentLayout = itemView.findViewById(R.id.container_cartelera);
        }
    }
}
