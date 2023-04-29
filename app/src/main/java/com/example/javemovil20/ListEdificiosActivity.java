package com.example.javemovil20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.javemovil20.databinding.ActivityListEdificiosBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ListEdificiosActivity extends AppCompatActivity {

    private ActivityListEdificiosBinding binding;
    private static final String EDIFICIOS = "Edificios.json";
    JSONArray arreglo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityListEdificiosBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ListView ListaEdificios = binding.ListaEdificios;

        List<String> EdificiosArray=new ArrayList<String>();
        List<String> Names=new ArrayList<String>();
        List<String> NumEd=new ArrayList<String>();
        List<String> Informacion=new ArrayList<String>();
        List<String> Foto=new ArrayList<String>();
        List<String> Lat=new ArrayList<String>();
        List<String> Lon=new ArrayList<String>();

        try {
            JSONObject jsonFile = loadCountriesByJson();
            arreglo = jsonFile.getJSONArray("Edificios");
            for (int i = 0; i < arreglo.length(); i++) {
                EdificiosArray.add(arreglo.getJSONObject(i).getString("Name"));
                Names.add(arreglo.getJSONObject(i).getString("Name"));
                NumEd.add(arreglo.getJSONObject(i).getString("NumEd"));
                Informacion.add(arreglo.getJSONObject(i).getString("Informacion"));
                Foto.add(arreglo.getJSONObject(i).getString("Foto"));
                Lat.add(arreglo.getJSONObject(i).getString("Lat"));
                Lon.add(arreglo.getJSONObject(i).getString("Lon"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapterListView = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, EdificiosArray);
        ListaEdificios.setAdapter(adapterListView);

        ListaEdificios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getBaseContext() , DetalleEdificioActivity.class);
                intent.putExtra("Nombre", Names.get(position));
                intent.putExtra("NumEd",NumEd.get(position));
                intent.putExtra("Informacion",Informacion.get(position));
                intent.putExtra("Foto",Foto.get(position));
                intent.putExtra("Lat",Lat.get(position));
                intent.putExtra("Lon",Lon.get(position));


                startActivity(intent);

            }
        });


    }
    public String loadJSONFromAsset(String assetName) {
        String json = null;
        try {
            InputStream is = this.getAssets().open("Edificios.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public JSONObject loadCountriesByJson() throws JSONException {
        return new JSONObject(loadJSONFromAsset(EDIFICIOS));
    }
}