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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{

    Button botonTransferir;
    Button botonAdmin;
    EditText campoContrasenia;


    private static MainActivity instancia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instancia = this;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //ConexionBluetooth.getInstancia(resultadoActivity).pedirDatosDispositivo();

        //Defino como tiene que comportarse el botón para la tranferencia de datos.
        botonTransferir = findViewById(R.id.botonTransferir);
        botonTransferir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    ConexionBluetooth.getInstancia(resultadoActivity).transmistirMedidas();
            }
        });

        //Boton para abrir la pantalla de configuración del administrador
        botonAdmin = findViewById(R.id.botonAdmin);
        campoContrasenia = findViewById(R.id.contraseniaCampo);
        botonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String form = campoContrasenia.getText().toString();
                if(form.equals("1234")) {
                    campoContrasenia.getText().clear();
                    Intent intent = new Intent(MainActivity.this, PaginaAdmin.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this
                            , "Contraseña incorrecta!"
                            , Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public static MainActivity getInstance() {
        return instancia;
    }

    ActivityResultLauncher<Intent> resultadoActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            resultado -> {
                if (resultado.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(MainActivity.this
                            , "Bluetooth correctamente activado"
                            , Toast.LENGTH_SHORT).show();
                }
            }
    );
}