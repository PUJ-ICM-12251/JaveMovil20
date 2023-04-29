package com.example.javemovil20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.javemovil20.databinding.ActivityDetalleEdificioBinding;

public class DetalleEdificioActivity extends AppCompatActivity {

    private ActivityDetalleEdificioBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalleEdificioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ImageView Foto=binding.Foto;
        TextView Nombre=binding.Nombre;
        TextView Num=binding.Numero;
        TextView Info=binding.Informacion;
        String urlFoto = getIntent().getStringExtra("Foto");

        Nombre.setText(getIntent().getStringExtra("Nombre"));
        Num.setText(getIntent().getStringExtra("NumEd"));
        Info.setText(getIntent().getStringExtra("Informacion"));
        Glide.with(this).load(urlFoto).into(Foto);

        Button boton=binding.button;

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), ListaReservasActivity.class);
                intent.putExtra("boton", boton.getText().toString());
                startActivity(intent);
            }
        });
    }
}