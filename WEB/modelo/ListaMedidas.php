<?php

function getMedidas($idDispositivo = null){

    if($idDispositivo === null){
        $response = file_get_contents('http://localhost:4000/api/medidas');
    }else{
        $response = file_get_contents('http://localhost:4000/api/medidas?dispositivo='. $idDispositivo);
    }
    $medidas = json_decode($response);

    return $medidas;
}


?>