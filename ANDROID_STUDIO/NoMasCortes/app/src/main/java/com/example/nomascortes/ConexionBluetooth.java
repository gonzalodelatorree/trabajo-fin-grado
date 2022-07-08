package com.example.nomascortes;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;


public class ConexionBluetooth {

    private static ConexionBluetooth instancia = null;

    private BluetoothAdapter btAdaptador;
    private BluetoothSocket  btSocket;


    private HebraConectada transmision;

    private Handler handler; //Handler que permite organizar las acciones según los tipos de mensaje

    private interface ConstantesMensaje {
        int MENSAJE_OBTENER_MEDIDAS = 0;
        int MENSAJE_OBTENER_DISPOSITIVO = 1;
        int MENSAJE_DISPOSITIVO_ACTUALIZADO = 2;

    }

    private ConexionBluetooth(ActivityResultLauncher<Intent> resultadoActivity){
        //Buscar adaptador bluetooth del dispositivo
        btAdaptador = BluetoothAdapter.getDefaultAdapter();

        if (btAdaptador != null && !btAdaptador.isEnabled()) {
            Intent permitirConexion = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            resultadoActivity.launch(permitirConexion);
        }

        Set<BluetoothDevice> dispositivosEmparejados = btAdaptador.getBondedDevices();
        BluetoothDevice arduinoHC_05 = null;

        boolean arduinoEncontrado = false;
        for (BluetoothDevice dispositivo : dispositivosEmparejados) {
            if (dispositivo.getName().equals("HC-05") && !arduinoEncontrado) {
                arduinoEncontrado = true;
                arduinoHC_05 = dispositivo;
            }
        }

        if(arduinoHC_05 != null) {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

            try {
                btSocket = arduinoHC_05.createRfcommSocketToServiceRecord(uuid);
                btSocket.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        transmision = new HebraConectada(btSocket);
        transmision.start();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == ConstantesMensaje.MENSAJE_OBTENER_MEDIDAS){
                    String contenido = ((String) msg.obj).substring(2, ((String) msg.obj).length()-1);
                    Log.d("TAG_DEBUG_H", contenido);

                    //MANDAR POR API
                    ConexionAPI.getInstance(MainActivity.getInstance()).postMedidasRequest(contenido, resultadoActivity);


                }else if (msg.what == ConstantesMensaje.MENSAJE_OBTENER_DISPOSITIVO){
                    String contenido = ((String) msg.obj).substring(2, ((String) msg.obj).length()-1);
                    Log.d("TAG_DEBUG_H", contenido);

                    PaginaAdmin.getInstance().setDatosDispositivo(contenido);
                }else if(msg.what == ConstantesMensaje.MENSAJE_DISPOSITIVO_ACTUALIZADO){
                    String contenido = ((String) msg.obj).substring(2, ((String) msg.obj).length()-1);
                    Log.d("TAG_DEBUG_H", contenido);

                    ConexionAPI.getInstance(PaginaAdmin.getInstance()).actualizarDispositivoRequest();
                }
            }
        };
    }

    public void actualizarDispositivo(){

        if (btSocket != null && btSocket.isConnected() && transmision.isAlive()){

            String datos = PaginaAdmin.getInstance().getDatosDispositivoCamposTxt();
            datos += "#";
            Log.d("TAG_DEBUG_g", datos);

            transmision.write(("U").getBytes(StandardCharsets.UTF_8));
            transmision.write(datos.getBytes(StandardCharsets.UTF_8));
        }

    }

    public void ajustarHora(){

        if (btSocket != null && btSocket.isConnected() && transmision.isAlive()){

            transmision.write(("T").getBytes(StandardCharsets.UTF_8));

            //Devuelve la fecha en UTC
            Instant fechaActual = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                fechaActual = Instant.now();
            }

            //Log.d("TAG_DEBUG_", fechaActual.toString());
            transmision.write(fechaActual.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    public void desconectar(){
        try{
            if (btSocket != null) {
                btSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConexionBluetooth getInstancia(ActivityResultLauncher<Intent> resultadoActivity){

        if(instancia == null){
            instancia = new ConexionBluetooth(resultadoActivity);
        }
        return instancia;
    }

    public void pedirDatosDispositivo(){
        if (btSocket != null && btSocket.isConnected() && transmision.isAlive()){
            transmision.write(("D").getBytes(StandardCharsets.UTF_8));
        }
    }

    public void transmistirMedidas() {

        if (btSocket != null && btSocket.isConnected() && transmision.isAlive())
            transmision.write(("S").getBytes(StandardCharsets.UTF_8));
    }

    public void transmistirMedidasCorrecto() {

        if (btSocket != null && btSocket.isConnected() && transmision.isAlive())
            transmision.write(("C").getBytes(StandardCharsets.UTF_8));
    }

    public void transmistirMedidasIncorrecto() {

        if (btSocket != null && btSocket.isConnected() && transmision.isAlive())
            transmision.write(("E").getBytes(StandardCharsets.UTF_8));
    }



    public class HebraConectada extends Thread{
        private final BluetoothSocket btSocket;
        private final InputStream entradaDatos;
        private final OutputStream salidaDatos;
        private byte[] buffer;

        public HebraConectada(BluetoothSocket socket) {
            btSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("TAG_DEBUG_", "Error al crear el input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("TAG_DEBUG_", "Error al crear output stream", e);
            }

            entradaDatos = tmpIn;
            salidaDatos = tmpOut;
        }

        public void run() {
            buffer = new byte[1024];
            int numBytes;
            String contenido = "";

            while (true) {
                try {
                    numBytes = entradaDatos.read(buffer);
                    contenido += new String(buffer, 0, numBytes);

                    Message msg = null;
                    if(contenido.charAt(0) == 'S' && contenido.charAt(contenido.length()-1) == '#') {
                        msg = handler.obtainMessage(
                            ConstantesMensaje.MENSAJE_OBTENER_MEDIDAS, numBytes, -1,
                            contenido);
                        Log.d("TAG_DEBUG_", contenido);
                        contenido = "";
                    }else if(contenido.charAt(0) == 'D' && contenido.charAt(contenido.length()-1) == '#'){
                        msg = handler.obtainMessage(
                            ConstantesMensaje.MENSAJE_OBTENER_DISPOSITIVO, numBytes, -1,
                            contenido);
                        Log.d("TAG_DEBUG_", contenido);
                        contenido = "";
                    }
                    else if(contenido.charAt(0) == 'T' && contenido.charAt(contenido.length()-1) == '#'){

                        Log.d("TAG_DEBUG_", contenido);
                        contenido = "";
                    }
                    else if(contenido.charAt(0) == 'U' && contenido.charAt(contenido.length()-1) == '#'){
                        msg = handler.obtainMessage(
                                ConstantesMensaje.MENSAJE_DISPOSITIVO_ACTUALIZADO, numBytes, -1,
                                contenido);

                        Log.d("TAG_DEBUG_", contenido);
                        contenido = "";
                    }

                    if(msg != null)
                        msg.sendToTarget();

                } catch (IOException e) {
                    Log.d("TAG_DEBUG_", "El input stream se desconectó", e);
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                salidaDatos.write(bytes);
            } catch (IOException e) {
                Log.e("TAG_DEBUG_", "Error trasnfiriendo datos", e);

            }
        }

        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException e) {
                Log.e("TAG_DEBUG_", "Could not close the connect socket", e);
            }
        }
    }
}
