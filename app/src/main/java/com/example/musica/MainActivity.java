package com.example.musica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.musica.modelo.Cancion;
import com.example.musica.modelo.Main;
import com.example.musica.modelo.MusicService;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
//https://itunes.apple.com/search?media=music&entity=song&&term=Shakira
    static int numBotones = 50;
    private MusicService service= null;
    private SearchView termino= null;
    private Cancion[] canciones= {};
    private Button btnGet= null;
    private LinearLayout LlBotones= null;
    private MediaPlayer mediaPlayer=null;
    private Boolean reproduciendo=false;
    private int ultimo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        service = new MusicService();

        initViews();
        initEvents();


    }
    public void initViews(){
        LlBotones =(LinearLayout) findViewById(R.id.llBotones);
        termino= findViewById(R.id.termino);
        btnGet= findViewById(R.id.btnGet);

    }
    public void initEvents(){

        btnGet.setOnClickListener(view-> btnObtenerCanciones());
    }
    private void playAudio(String urlAudio, int actual){

        try{
            if( mediaPlayer==null){
                mediaPlayer= new MediaPlayer() ;
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer=null;
                if(ultimo!=actual){
                    mediaPlayer= new MediaPlayer() ;
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(urlAudio);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }

            }else{
                mediaPlayer.setDataSource(urlAudio);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
            ultimo=actual;

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void detenerAudio(){
        if(mediaPlayer!=null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }

        }

    }
    public void btnObtenerCanciones() {
        service.searchSongsByTerm( termino.getQuery().toString(), 50,(isNetworkError, statusCode, text)->{
            switch (statusCode){
                case -1:

                    Log.d("musica", "Error -1"+ isNetworkError);
                    Log.d("musica", "Error -1"+ termino.getQuery().toString());
                    break;
                case 404:
                    Log.d("musica", "Termino no encontrado");
                    break;
                case 200:
                    Log.d("musica", "Correcto"+ termino.getQuery().toString());
                    //Log.d("musica", "Numero de canciones"+root.getMain().getNumero());
                    GsonBuilder gsonBuilder= new GsonBuilder();
                    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                    Gson gson= gsonBuilder.create();
                    Main convertir=null;
                    Gson gson2=new Gson();

                    convertir=gson2.fromJson( text, Main.class);

                    Log.d("respuesta numero obtenido ", convertir.getNumero()+"");
                    Log.d("respuesta cantidad leida real ", convertir.getResults().length+"");
                    if(convertir.getResults().length==0){
                        runOnUiThread(()->{
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            LinearLayout.LayoutParams cosas = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            LlBotones.removeAllViewsInLayout();
                            LlBotones.requestLayout();
                            LlBotones.invalidate();
                                LinearLayout principal= new LinearLayout(this);
                                principal.setOrientation(LinearLayout.HORIZONTAL);
                                principal.setLayoutParams(cosas);
                                LinearLayout secundario= new LinearLayout(this);
                                secundario.setOrientation(LinearLayout.VERTICAL);
                                secundario.setLayoutParams(cosas);
                                CardView card = new CardView(this);
                                card.setLayoutParams(lp);
                                TextView nombreCancion= new TextView(this);
                                nombreCancion.setText("NO HAY RESULTADOS");
                                nombreCancion.setLayoutParams(cosas);
                                nombreCancion.setTextSize(20);
                                nombreCancion.setTextColor(Color.BLACK);
                                nombreCancion.setTextColor(Color.GRAY);
                                secundario.addView(nombreCancion);
                                principal.addView(secundario);

                                card.addView(principal);


                                LlBotones.addView(card);


                        });
                    }else{
                        Log.d("respuesta cantidad leida real ", convertir.getResults()[0].getCollectionName()+"");
                        Main finalConvertir = convertir;

                        runOnUiThread(()->{
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            LinearLayout.LayoutParams cosas = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            LlBotones.removeAllViewsInLayout();
                            LlBotones.requestLayout();
                            LlBotones.invalidate();
                            for(int i=0; i<finalConvertir.getResults().length;i++){

                                LinearLayout principal= new LinearLayout(this);
                                principal.setOrientation(LinearLayout.HORIZONTAL);
                                principal.setLayoutParams(cosas);
                                LinearLayout secundario= new LinearLayout(this);
                                secundario.setOrientation(LinearLayout.VERTICAL);
                                secundario.setLayoutParams(cosas);

                                CardView card = new CardView(this);
                                card.setLayoutParams(lp);
                                ImageView imagen=new ImageView(this);
                                Picasso.get().load(finalConvertir.getResults()[i].getArtworkUrl100()).into(imagen);
                                imagen.setLayoutParams(cosas);
                                TextView nombreCancion= new TextView(this);
                                nombreCancion.setText(finalConvertir.getResults()[i].getTrackCensoredName());
                                nombreCancion.setLayoutParams(cosas);
                                nombreCancion.setTextSize(20);
                                nombreCancion.setTextColor(Color.BLACK);
                                TextView nombreAlbum= new TextView(this);
                                nombreAlbum.setText(finalConvertir.getResults()[i].getCollectionName());
                                nombreAlbum.setLayoutParams(cosas);
                                nombreAlbum.setTextSize(15);
                                nombreCancion.setTextColor(Color.GRAY);
                                principal.addView(imagen);
                                secundario.addView(nombreCancion);
                                secundario.addView(nombreAlbum);
                                principal.addView(secundario);
                                //boton.setImage(finalConvertir.getResults()[i].getCollectionName());
                                card.addView(principal);
                                int finalI = i;
                                card.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("reproductor ", finalConvertir.getResults()[finalI].getPreviewUrl()+"");
                                        playAudio(finalConvertir.getResults()[finalI].getPreviewUrl()+"", finalI);
                                        //agregar boton parar
                                        //detenerAudio()
                                        //         ImageView imagen = new ImageView(this);
                                        //   Picasso.get().load(finalConvertir.getResults()[i].getArtworkUrl100()).into(imagen);
                                        //     imagen.setLayoutParams(cosas);
                                        //       this.addView(imagen);
                                    }
                                });
                                LlBotones.addView(card);
                            }

                        });
                    }

                    break;
            }

        });

    }



}