#include <assert.h>
#include <SoftwareSerial.h>

//Bibliotecas para manejar tarjeta SD
#include <SPI.h>
#include <SD.h>

//Bibliotecas para manejar reloj de tiempo real
#include "RTClib.h"

RTC_DS3231 reloj;

//Archivos para gestionar datos del Arduino (medidas y configuración)
File registro;
File infoDispositivo;

//Definimos el objeto que va a manejar la conexión Bluetooth
SoftwareSerial BT(5, 6);

//Pin que detecta si hay corriente eléctrica o no
short int pinDetectarCorriente = 2;

//Variable que contiene la información sobre si hay  corriente
bool estadoCorriente; // 1 -> hay corriente, 0 -> no hay corriente

//Variable que contiene la información sobre si había corriente
//en el instante anterior
bool estadoAnteriorCorriente;

//Variables de control de bluetooth
const char ENVIAR_REGISTRO = 'S';        //SEND
const char TRABAJO_FINALIZADO = 'C';     //COMPLETED
const char ERROR_AL_PROCESAR = 'E';      //ERROR
const char ACTUALIZAR = 'U';             //UPDATE
const char AJUSTAR_HORA = 'T';           //TIME
const char DATOS_DISPOSITIVO = 'D';      //DATA

char accionBT; //Variable que define que acción realiza el módulo BT

//----------------------------- SETUP ------------------------------------------

void setup()
{
  //Pines para la conexión del módulo microSD
  /* MOSI -> pin 11
     MISO -> pin 12
     SCK ->  pin 13
     CS ->   pin 10
  */
  short int pinCS = 10;

  //Inicialización módulo BT
  Serial.begin(9600);
  BT.begin(9600);

  //Inicialización módulo microSD
  Serial.print("Inicializando tarjeta microSD... ");
  if (!SD.begin(pinCS)) {
    Serial.print("error!\n");
    while (1);
  }
  Serial.print("completado!\n");

  //Inicialización módulo RTC
  if (!reloj.begin()) {
    Serial.println("Módulo RTC no encontrado!");
    while (1);
  }

  pinMode(pinDetectarCorriente, INPUT);

  estadoCorriente = digitalRead(pinDetectarCorriente);
  estadoAnteriorCorriente = estadoCorriente;

  reloj.adjust(DateTime(F(__DATE__), F(__TIME__)));

  //Mensaje que indica que el dispositivo se ha conectado.
  escribirArchivo('S');

  //Serial.print(leerArchivo("conf.txt"));
  
}

//-------------------------- LOOP ------------------------------------------

void loop()
{
  estadoCorriente = digitalRead(pinDetectarCorriente);

  Serial.print("\nEstado corriente: ");
  Serial.print(estadoCorriente);

  //DETECCIÓN DE CORRIENTE --------------------------------
  if (!estadoCorriente && estadoAnteriorCorriente) {
    estadoAnteriorCorriente = estadoCorriente;
    escribirArchivo('D');
    Serial.println("\nEscribir down");
  }
  else if (estadoCorriente && !estadoAnteriorCorriente) {
    estadoAnteriorCorriente = estadoCorriente;
    escribirArchivo('U');
    Serial.println("\nEscribir up");
  }

  accionBT = BT.read();
  
  //ENVIAR MEDIDAS AL DISPOSITIVO MÓVIL ---------------------------
  if (accionBT == ENVIAR_REGISTRO) {
    Serial.print('S');
    enviarRegistro();
    
    accionBT  = BT.read();

    //Esperamos a que la app movil haga su procesamiento
    while(accionBT != TRABAJO_FINALIZADO && accionBT != ERROR_AL_PROCESAR){
      accionBT =  BT.read();
    }
    Serial.println(accionBT);
    /*Una vez que el archivo ha sido enviado y procesado por la app
      movil, eliminamos su contenido para almacenar únicamente los
      nuevos cortes de energía*/
    if(accionBT == TRABAJO_FINALIZADO){
      borrarRegistro();
    }

  //ACTUALIZAR DISPOSITIVO --------------------
  }else if(accionBT == ACTUALIZAR){
    Serial.print('U');
    actualizarDispositivo();

  //ENVIAR DATOS DEL DISPOSITIVO --------------
  }else if(accionBT == DATOS_DISPOSITIVO){
    Serial.print('D');
    //BT.print(leerInfoDispositivo());
    enviarDatosDispositivo();
  }

  //AJUSTAR HORA ------------------------------
  else if(accionBT == AJUSTAR_HORA){
    Serial.print('T');
    ajustarHora();
  }
  
  delay(1000);
}

//--------------------------- FUNCIONES ----------------------------------------------------

//Funcion que recibe los campos para inicializar el dispositivo ARDUINO
bool actualizarDispositivo(){

  BT.print(ACTUALIZAR);
  BT.print('\n');

  infoDispositivo = SD.open("conf.txt", FILE_WRITE | O_TRUNC);
  if(infoDispositivo){
    char c = BT.read();
    Serial.print(c);
    
    while(BT.available()){
      Serial.print(BT.available());
      infoDispositivo.print(c);
      Serial.print(c);
      c = BT.read();
    }
    Serial.print(c);

    infoDispositivo.close();  
  }else{
    Serial.println("Error abriendo archivo");
  }
  BT.print('#');
}

//Funcion que ajusta la hora del RTC (recibe la nueva hora mediante BT)
void ajustarHora(){

  BT.print(AJUSTAR_HORA);
  BT.print('\n');

  char c;
  String fecha = "";
  while(BT.available()){
    c = BT.read();
    fecha += String(c);
  }
  Serial.println("T " + fecha);
  BT.print(fecha + " #");
  reloj.adjust(DateTime(F(__DATE__), F(__TIME__)));
}

//Función que elimina toda la información del registro de medidas
void borrarRegistro(){
  registro = SD.open("regs.txt", FILE_WRITE | O_TRUNC);
  registro.close();
}

/*Función que obtiene los valores de configuracion del dispositivo
  desde un archivo en la memoria SD.
  El primer caracter de configuración del archivo puede ser
  - I (INICIALIZADO)
  - N (NO INICIALIZADO)
*/

String leerInfoDispositivo(){

  String info = "";
  
  infoDispositivo = SD.open("conf.txt", FILE_READ);
  
  if(infoDispositivo){
    char primerCaracter = infoDispositivo.read();

    if(primerCaracter == 'I'){
      info += primerCaracter;
      
      while(infoDispositivo.available()){
        info += (char)infoDispositivo.read();
      }
    }else if(primerCaracter == 'N'){
      info = "N";
    }

    infoDispositivo.close();
  }else{
    Serial.println("Error abriendo archivo");
    info = "N";
  }
  return info;
}

//Función que devuelve por BT los datos de configuracion del dispositivo
void enviarDatosDispositivo(){

  BT.print(DATOS_DISPOSITIVO);
  BT.print('\n');

  BT.print(leerInfoDispositivo());
  //Serial.print(leerInfoDispositivo());
  
  BT.print('\n');
  BT.print('#');
  
}

//Función que envía mediante Bluetooth los registros escritos en el archivo de medidas
void enviarRegistro(){

  String info = leerInfoDispositivo();
  char inicializado = info[0];
  String id = info.substring(2, 6);
  

  BT.print(ENVIAR_REGISTRO);
  BT.print('\n');

  if(inicializado == 'I'){

    BT.print(id);
    registro = SD.open("regs.txt", FILE_READ);
    while(registro.available()){
      BT.print((char)registro.read());
    }
    registro.close();
  }else{
    BT.print('N');
    BT.print('\n');
  }
  BT.print('#');
}

/*Función que escribe el archivo que contiene el registro de medidas
  Se le pasa 'D' para escribir una caída de tensión, 'U' para una subida
  y 'S' para escribir un encendido del dispositivo */
void escribirArchivo(const char c) {
  assert(c == 'D' || c == 'U' || c == 'S');

  registro = SD.open("regs.txt", FILE_WRITE);
  if (registro){
    String linea = "";
    linea = String(c) + String(" ") + formatearFecha(reloj.now()) + "\n";
    
    registro.print(linea);
    registro.close();
  }
  else
    Serial.println("Error abriendo el archivo!");
}


/*Función que devuelve un string con el siguiente formato de fecha
  DD-MM-YYYY hh:mi  */
String formatearFecha(DateTime date) {

  String fecha = "";

  String dia = String(date.day()); 
  String mes = String(date.month());
  String hora = String(date.hour());
  String minuto = String(date.minute());
  String anio = String(date.year());

  if(mes.length() < 2) mes = "0" + mes;
  if(dia.length() < 2) dia = "0" + dia;
  if(hora.length() < 2) hora = "0" + hora;
  if(minuto.length() < 2) minuto = "0" + minuto;
  
  fecha = dia + "-" + mes + "-" + anio + " " + hora + ":" + minuto;   
  return fecha;
}
