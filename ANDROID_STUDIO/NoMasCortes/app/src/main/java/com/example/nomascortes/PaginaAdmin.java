package com.example.nomascortes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PaginaAdmin extends AppCompatActivity{

    Button actualizar;
    Button ajustarHora;
    Button inicializarDispositivo;
    ImageButton recargarDatos;

    TextView id;
    EditText direccion;
    EditText personaContacto;
    EditText telefono;
    EditText email;

    static PaginaAdmin instancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_admin);

        instancia = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        id = findViewById(R.id.idDispositivo);
        direccion = findViewById(R.id.direccion);
        personaContacto = findViewById(R.id.editPersonaContacto);
        telefono = findViewById(R.id.editTextoTelefono);
        email = findViewById(R.id.editTextoEmail);

        ConexionBluetooth.getInstancia(resultadoActivity).pedirDatosDispositivo();

        //ConexionAPI.getInstance().postMedidasRequest("D prigue\nU pringue\n");

        actualizar = findViewById(R.id.actualizarDispositivo);
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConexionBluetooth.getInstancia(resultadoActivity).actualizarDispositivo();
            }
        });

        ajustarHora = findViewById(R.id.botonAjustarHora);
        ajustarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConexionBluetooth.getInstancia(resultadoActivity).ajustarHora();
            }
        });

        recargarDatos = findViewById(R.id.botonRecargarDatosDispositivo);
        recargarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConexionBluetooth.getInstancia(resultadoActivity).pedirDatosDispositivo();
            }
        });

        inicializarDispositivo = findViewById(R.id.botonInicializarDispositivo);
        inicializarDispositivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDatosDispositivoCamposTxt();
                ConexionAPI.getInstance(PaginaAdmin.this).inicializarDispositivoRequest();
            }
        });

    }

    public static PaginaAdmin getInstance() {
        return instancia;
    }

    public void setDatosDispositivo(String contenido){

        List<TextView> listaCampos = new ArrayList<>();

        listaCampos.add(id);
        listaCampos.add(direccion);
        listaCampos.add(personaContacto);
        listaCampos.add(telefono);
        listaCampos.add(email);

        if(contenido.charAt(0) == 'N'){
            id.setText("No inicializado");
            direccion.setText("");
            personaContacto.setText("");
            telefono.setText("");
            email.setText("");

        }else if (contenido.charAt(0) == 'I'){

            contenido = contenido.substring(2);
            String valor = "";
            int i=0;

            int atributo = 0;
            for(TextView elemento:listaCampos) {
                while (contenido.length() > i && !String.valueOf(contenido.charAt(i)).equals("\n")) {
                    valor += String.valueOf(contenido.charAt(i));
                    elemento.setText(valor);
                    i++;
                }
                switch (atributo){
                    case 0:
                        if(valor == "desconocido")
                            Dispositivo.getInstancia().setId(Integer.valueOf(valor));
                        break;
                    case 1: Dispositivo.getInstancia().setDireccion(valor); break;
                    case 2: Dispositivo.getInstancia().setPersonaContacto(valor); break;
                    case 3: Dispositivo.getInstancia().setTelefonoContacto(valor); break;
                    case 4: Dispositivo.getInstancia().setEmailContacto(valor); break;
                }

                atributo++;
                i++;
                valor = "";
            }
        }
    }

    public String getDatosDispositivoCamposTxt(){
        String info = "I\n";

        info += id.getText() + "\n";
        info += direccion.getText() + "\n";
        info += personaContacto.getText() + "\n";
        info += telefono.getText() + "\n";
        info += email.getText() + "\n";

        Dispositivo.getInstancia().setId(Integer.valueOf(String.valueOf(id.getText())));
        Dispositivo.getInstancia().setDireccion(String.valueOf(direccion.getText()));
        Dispositivo.getInstancia().setPersonaContacto(String.valueOf(personaContacto.getText()));
        Dispositivo.getInstancia().setTelefonoContacto(String.valueOf(telefono.getText()));
        Dispositivo.getInstancia().setEmailContacto(String.valueOf(email.getText()));

        return info;
    }

    ActivityResultLauncher<Intent> resultadoActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(PaginaAdmin.this
                            , "Bluetooth correctamente activado"
                            , Toast.LENGTH_SHORT).show();
                }
            }
    );

}