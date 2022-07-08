<?php

function getTodosDispositivos(){
    $response = file_get_contents('http://localhost:4000/api/dispositivosID');
    $datos = json_decode($response, true);

    $listaDispositivos = [];

    for($i=0; $i<count($datos); $i++){
        $listaDispositivos[] = new Dispositivo($datos[$i]['id']);
    }

    return $listaDispositivos;
}

?>