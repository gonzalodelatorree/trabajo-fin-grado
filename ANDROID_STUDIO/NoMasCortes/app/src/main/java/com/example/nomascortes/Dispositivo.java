package com.example.nomascortes;

import android.widget.TextView;

public class Dispositivo {

    private int id;
    private String direccion;
    private String personaContacto;
    private String telefonoContacto;
    private String emailContacto;
    private float latitud;
    private float longitud;

    private Dispositivo(){};

    private static Dispositivo instancia = null;

    public String getDireccion() {
        return direccion;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public int getId() {
        return id;
    }

    public float getLatitud() {
        return latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public String getPersonaContacto() {
        return personaContacto;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public void setPersonaContacto(String personaContacto) {
        this.personaContacto = personaContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public static Dispositivo getInstancia() {
        if (instancia == null)
            instancia = new Dispositivo();

        return instancia;
    }
}

