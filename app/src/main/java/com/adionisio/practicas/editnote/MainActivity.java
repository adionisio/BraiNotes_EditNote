package com.adionisio.practicas.editnote;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{

    private EditText titulo;
    private EditText cuerpo;
    private EditText fecha;
    private EditText hora;
    private CheckBox recordatorioCheck;
    private Spinner recordatorioSpinner;
    private EditText color;
    private Nota nota;
    private int idNota;
    private Gson gson;
    private Retrofit retrofit;
    private RestClient restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        titulo = (EditText)findViewById(R.id.editTextTitulo);
        cuerpo = (EditText)findViewById(R.id.editTextCuerpo);
        fecha = (EditText)findViewById(R.id.editTextDate);
        hora = (EditText)findViewById(R.id.editTextTime);
        recordatorioCheck = (CheckBox)findViewById(R.id.checkBox);
        recordatorioSpinner = (Spinner)findViewById(R.id.spinner);
        String[] recordatorios = {"Una hora antes","Un dia antes","Una semana antes"};
        recordatorioSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, recordatorios));
        color = (EditText)findViewById(R.id.editTextColor);
        // Este idNota llegara del Intent de la actividad anterior de calendario
        idNota = 1;

        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://vyvserver.etsisi.upm.es:13000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        restClient = retrofit.create(RestClient.class);

        loadJSON();

    }


    private void loadJSON(){

        Call<Nota> call = restClient.getDataId(idNota);
        call.enqueue(new Callback<Nota>() {
            @Override
            public void onResponse(Call<Nota> call, Response<Nota> response) {
                switch (response.code()) {
                    case 200:
                        nota = response.body();
                        fillFields();
                        break;
                    case 400:
                        Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later.BAD REQUEST", Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later.Unauthorized", Toast.LENGTH_SHORT).show();
                        break;
                    case 404:
                        Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later. NOT FOUND", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Nota> call, Throwable t) {
                Log.e("EditNote_ADB", t.toString());
                Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void modifyNota(View view){

        nota.setTitulo(titulo.getText().toString());
        nota.setCuerpo(cuerpo.getText().toString());
        if(recordatorioCheck.isChecked()){
            nota.setRecordatorio(recordatorioSpinner.getSelectedItemPosition());
        }else{
            nota.setRecordatorio(-1);
        }
        nota.setFechaHora(fecha.getText().toString()+"T"+hora.getText().toString()+"Z");
        nota.setColor(color.getText().toString());
        Call<Nota> call = restClient.putNota(idNota,nota);
        call.enqueue(new Callback<Nota>() {
            @Override
            public void onResponse(Call<Nota> call, Response<Nota> response) {
                switch (response.code()) {
                    case 200:
                        Toast.makeText(getApplicationContext(), "Nota modificada", Toast.LENGTH_SHORT).show();
                        nota = response.body();
                        fillFields();
                        break;
                    case 400:
                        Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later.BAD REQUEST", Toast.LENGTH_SHORT).show();
                        break;
                    case 401:
                        Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later.Unauthorized", Toast.LENGTH_SHORT).show();
                        break;
                    case 404:
                        Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later. NOT FOUND", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Nota> call, Throwable t) {
                Log.e("EditNote_ADB", t.toString());
                Toast.makeText(getApplicationContext(), "Ups, something is wrong. try it later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillFields(){
        titulo.setText(nota.getTitulo());
        cuerpo.setText(nota.getCuerpo());
        String[] fechas = nota.getFechaHora().split("T");
        fecha.setText(fechas[0]);
        hora.setText(fechas[1].split("Z")[0]);
        color.setText(nota.getColor());
        if(nota.getRecordatorio()!=-1){
            recordatorioCheck.setChecked(true);
            recordatorioSpinner.setSelection(nota.getRecordatorio());
        }else{
            recordatorioCheck.setChecked(false);
        }
    }
}
