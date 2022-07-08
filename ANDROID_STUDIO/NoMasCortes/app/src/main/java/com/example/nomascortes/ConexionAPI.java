package com.example.nomascortes;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConexionAPI {
    private static ConexionAPI instancia;
    private RequestQueue cola;
    private static Context ctx;
    private final String URL_BASE = "http://192.168.1.33:4000/api/";

    private ConexionAPI(Context context) {
        ctx = context;
        cola = getRequestQueue();
    }

    public static synchronized ConexionAPI getInstance(Context context) {
        if (instancia == null) {
            instancia = new ConexionAPI(context);
        }
        return instancia;
    }

    public void actualizarDispositivoRequest(){

        RequestQueue cola = ConexionAPI.getInstance(ctx.getApplicationContext()).getRequestQueue();
        String url = URL_BASE + "dispositivos/" + Dispositivo.getInstancia().getId();

        JSONObject datosDispActualizado = new JSONObject();
        try {
            //Conseguir localización del dispositivo.
            ActivityCompat.requestPermissions(PaginaAdmin.getInstance() ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            LocationManager lm = (LocationManager)ctx.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitud = location.getLongitude();
            double latitud = location.getLatitude();

            datosDispActualizado.put("direccion", Dispositivo.getInstancia().getDireccion());
            datosDispActualizado.put("personaContacto", Dispositivo.getInstancia().getPersonaContacto());
            datosDispActualizado.put("telefonoContacto", Dispositivo.getInstancia().getTelefonoContacto());
            datosDispActualizado.put("emailContacto", Dispositivo.getInstancia().getEmailContacto());
            datosDispActualizado.put("latitud", latitud);
            datosDispActualizado.put("longitud", longitud);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.PUT, url, datosDispActualizado,
                response -> {
                    Log.d("TAG_DEBUG_", response.toString());
                    Toast.makeText(PaginaAdmin.getInstance(),"Dispositivo Actualizado", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.d("TAG_DEBUG_", error.toString());
                    Toast.makeText(MainActivity.getInstance()
                            , "Error al actualizar el dispositivo", Toast.LENGTH_SHORT).show();
                });

        ConexionAPI.getInstance(PaginaAdmin.getInstance()).addToRequestQueue(peticion);
    }

    public void getDispositivoRequest(){
        RequestQueue cola = ConexionAPI.getInstance(ctx.getApplicationContext()).getRequestQueue();
        String url = URL_BASE + "dispositivos/" + Dispositivo.getInstancia().getId();

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    int tam = response.length();
                    Log.d("TAG_DEBUG_", String.valueOf(tam));

                    for (int i=0; i<tam; i++){
                        try {
                            JSONObject dispositivo = new JSONObject(response.get(i).toString());
                            String contenido = "I\n";
                            contenido += dispositivo.getString("id") + "\n";
                            contenido += dispositivo.getString("direccion") + "\n";
                            contenido += dispositivo.getString("personaContacto") + "\n";
                            contenido += dispositivo.getString("telefonoContacto") + "\n";
                            contenido += dispositivo.getString("emailContacto") + "\n";
                            Log.d("TAG_DEBUG_", contenido);
                            PaginaAdmin.getInstance().setDatosDispositivo(contenido);
                        } catch (JSONException e) {
                            Log.d("TAG_DEBUG_", "CATCH");
                        }
                    }
                },
                error -> {
                    Log.d("TAG_DEBUG_", error.getMessage());
                });

        ConexionAPI.getInstance(PaginaAdmin.getInstance()).addToRequestQueue(request);
    }

    public RequestQueue getRequestQueue() {
        if (cola == null) {
            cola = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return cola;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void inicializarDispositivoRequest(){

        RequestQueue cola = ConexionAPI.getInstance(ctx.getApplicationContext()).getRequestQueue();
        String url = URL_BASE + "dispositivos/";

        JSONObject datosNuevoDisp = new JSONObject();
        try {
            //Conseguir localización del dispositivo.
            ActivityCompat.requestPermissions(PaginaAdmin.getInstance() ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            LocationManager lm = (LocationManager)ctx.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitud = location.getLongitude();
            double latitud = location.getLatitude();


            datosNuevoDisp.put("direccion", Dispositivo.getInstancia().getDireccion());
            datosNuevoDisp.put("personaContacto", Dispositivo.getInstancia().getPersonaContacto());
            datosNuevoDisp.put("telefonoContacto", Dispositivo.getInstancia().getTelefonoContacto());
            datosNuevoDisp.put("emailContacto", Dispositivo.getInstancia().getEmailContacto());
            datosNuevoDisp.put("longitud", longitud);
            datosNuevoDisp.put("latitud", latitud);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.POST, url, datosNuevoDisp,
                response -> {
                    Log.d("TAG_DEBUG_", response.toString());
                    try {
                        Dispositivo.getInstancia().setId(Integer.valueOf(response.getString("insertId")));
                        String resultado = "I\n" + response.getString("insertId") + "\n";
                        resultado += Dispositivo.getInstancia().getDireccion() + "\n";
                        resultado += Dispositivo.getInstancia().getPersonaContacto() + "\n";
                        resultado += Dispositivo.getInstancia().getTelefonoContacto() + "\n";
                        resultado += Dispositivo.getInstancia().getEmailContacto() + "\n";
                        PaginaAdmin.getInstance().setDatosDispositivo(resultado);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(PaginaAdmin.getInstance(),"Dispositivo inicializado", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.d("TAG_DEBUG_", error.toString());
                    Toast.makeText(MainActivity.getInstance()
                            , "Error al inicializar el dispositivo", Toast.LENGTH_SHORT).show();
                });

        ConexionAPI.getInstance(PaginaAdmin.getInstance()).addToRequestQueue(peticion);
    }

    public void postMedidasRequest(String medidas, ActivityResultLauncher<Intent> resultadoActivity){

        //RequestQueue cola = ConexionAPI.getInstance(this.getApplicationContext()).getRequestQueue();
        String url = URL_BASE + "medidas/";

        String dispositivo = "";

        //Definimos el dispositivo que ha enviado medidas.
        int i=0;
        while(medidas.charAt(i) != '\n'){
            dispositivo += medidas.charAt(i);
            i++;
        }

        Log.d("TAG_DEBUG", "Generando petición API");

        //Si el dispositivo no está inicializado no mandamos la petición a la API.
        if(dispositivo.length() == 3) {

            Dispositivo.getInstancia().setId(Integer.valueOf(dispositivo));

            JSONArray arrayMedidas = new JSONArray();
            JSONObject idJson = new JSONObject();
            try {
                //Primero insertamos el id del dispositivo que ha registrado las medidas
                idJson.put("dispositivo", Dispositivo.getInstancia().getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            arrayMedidas.put(idJson);

            i=0;
            String medida = "";
            while(medidas.length() > i) {

                medida = "";
                while(medidas.charAt(i) != '\n' && medidas.length() > i){
                    medida += medidas.charAt(i);
                    i++;
                }
                i++;
                try {
                    JSONObject medidaJSON = new JSONObject();
                    medidaJSON.put("tipo", String.valueOf(medida.charAt(0)));
                    medidaJSON.put("fecha", medida.substring(2));

                    arrayMedidas.put(medidaJSON);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JsonArrayRequest peticion = new JsonArrayRequest(Request.Method.POST, url, arrayMedidas,
                    response -> {
                        Log.d("TAG_DEBUG_Response", response.toString());
                        Toast.makeText(MainActivity.getInstance()
                                , "Datos enviados correctamente", Toast.LENGTH_SHORT).show();
                        ConexionBluetooth.getInstancia(resultadoActivity).transmistirMedidasCorrecto();

                    },
                    error -> {
                        Log.d("TAG_DEBUG_", error.toString());
                        Toast.makeText(MainActivity.getInstance()
                                , "Error al enviar los datos", Toast.LENGTH_SHORT).show();
                        ConexionBluetooth.getInstancia(resultadoActivity).transmistirMedidasIncorrecto();
                    });

            ConexionAPI.getInstance(MainActivity.getInstance()).addToRequestQueue(peticion);
        }
    }
}