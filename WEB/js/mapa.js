//GENERACIÓN DEL MAPA
var map = L.map('map').setView([37.203601, -3.601248],15);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap'
}).addTo(map);

//OBTENCIÓN DE CORTES DE LUZ 
var cortes = document.querySelectorAll('div.puntoMapa');


/* HACEMOS QUE CUANTOS MÁS CPORTES DE LUZ TIENE UN DISPOSITIVO,
   MAYOR RADIO TENDRÁ EL CÍRCULO QUE LO REPRESENTA*/
const VALOR_RADIO_INICIAL = 30;
const VALOR_SUMA_RADIO = 10;
var radio = VALOR_RADIO_INICIAL - VALOR_SUMA_RADIO;

var dispositivoActual, dispositivoAnterior;

var circulos = [];

if(cortes.length>0) dispositivoAnterior = cortes[0].dataset.dispositivo;

var i=0;
while(i<cortes.length){
    dispositivoActual = cortes[i].dataset.dispositivo;
    console.log(radio);
    
    if(dispositivoActual == dispositivoAnterior && !(i==cortes.length-1)){
        radio += VALOR_SUMA_RADIO;
    }else{

        if(i==cortes.length-1 && dispositivoActual == dispositivoAnterior) radio += VALOR_SUMA_RADIO;

        circulos.push({"dispositivo": dispositivoAnterior,
                       "latitud": cortes[i-1].dataset.latitud,
                       "longitud": cortes[i-1].dataset.longitud, 
                       "radio": radio});

        radio = VALOR_RADIO_INICIAL;
    }
    dispositivoAnterior = dispositivoActual;
    i++;
}

//DIBUJAMOS LOS CIRCULOS EN EL MAPA
circulos.forEach(circulo => {
    L.circle([circulo.latitud, circulo.longitud], {radius: circulo.radio}).addTo(map);
});
