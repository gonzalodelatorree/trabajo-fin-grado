<?php

require_once('./modelo/CorteLuz.php');
require_once('./modelo/Dispositivo.php');
require_once('./modelo/ListaDispositivos.php');
require_once('./modelo/ListaMedidas.php');

session_start();

class Controlador{

    static public function crearCSV($idDispositivo = null){

        $medidas = getMedidas($idDispositivo);

        if($idDispositivo === null){
            $f = fopen('./datosMedidas/medidas.csv', 'w+b');
        }else{
            $f = fopen('./datosMedidas/medidas'. $idDispositivo . '.csv', 'w+b');
        }

        if ($f === false) {
            die('Error opening the file ' . $filename);
        }else{
            $array = ['id', 'tipo', 'fecha', 'IDdispositivo'];
            fputcsv($f, $array);

            for ($i=0; $i<count($medidas); $i++){

                $array = [$medidas[$i]->id, $medidas[$i]->tipo, $medidas[$i]->fecha, $medidas[$i]->dispositivo];
                fputcsv($f, $array);
            }

            fclose($f);
        }
    }

    static public function getMedidas($idDispositivo = null){
        return getMedidas($idDispositivo);
    }

    static public function getDispositivos(){
        return getTodosDispositivos();
    }

    static public function getCortesDispositivo($id, $fIni = null, $fFin = null){
        $cortesDispositivo = (new Dispositivo($id))->getCortesLuz();
        return $cortesDispositivo;

    }

    static public function getTodosCortesLuz($fIni = null, $fFin = null){

        $todosCortesLuz = [];

        $dispositivos = getTodosDispositivos();

        for($i=0; $i<count($dispositivos); $i++){
            $cortesDispositivo = $dispositivos[$i]->getCortesLuz($fIni, $fFin);
            
            for($j=0; $j<count($cortesDispositivo); $j++){
                $todosCortesLuz[] = $cortesDispositivo[$j];
            }
        }
        return $todosCortesLuz;
    }

    /*
    Funcion que devuelve cortes de luz teniendo en cuenta
    posibles incidencias en la medida de datos por parte del 
    Arduino
    */
    static public function getCortesLuz(){
        $datos = Modelo::getCortesLuz();

        //Creamos un array que contenga cortes de Luz
		$cortes = [];

        $i = 0;

        /* Voy a ir mirando las medidas en grupos de dos,
           Un corte de luz puede tener 3 casos, 
           - El primer caso, con un fin y un inicio establecidos.
           - El segundo caso, indefinido y fin establecido
           - Terccer caso, inicio establecido y final indefinido.
        */

        while($i < (count($datos) -1)){

            $fechaInicio = null;
            $fechaFin = null;
            $insertarCorte = false;

            $primerIndice = $datos[$i]['tipo'];
            $segundoIndice = $datos[$i+1]['tipo'];

            if($primerIndice === 'S'){
                $i++;

            }elseif($primerIndice === 'D'){
                $insertarCorte = true;

                if($segundoIndice === 'U'){
                    $inicioCorte = $i;
                    $finCorte = $i+1;
                    $i += 2;

                }elseif($segundoIndice === 'S'){
                    $inicioCorte = $i;
                    $finCorte = null;
                    $i += 2;

                }elseif($segundoIndice === 'D'){
                    $inicioCorte = $i;
                    $finCorte = null;
                    $i++;

                }

            }elseif ($primerIndice === 'U') {
                $insertarCorte = true;

                if($segundoIndice === 'U'){
                    $inicioCorte = null;
                    $finCorte = $i;
                    $i++;

                }elseif($segundoIndice === 'S'){
                    $inicioCorte = null;
                    $finCorte = $i;
                    $i += 2;
                }elseif($segundoIndice === 'D'){
                    $inicioCorte = null;
                    $finCorte = $i;
                    $i++;
                }
            }

             
            if($insertarCorte){
                echo('<br>');

                if($inicioCorte != null){
                    $fechaInicio = $datos[$inicioCorte]['fecha'];
                }else{
                    $fechaInicio = 'indefinido';
                }
                if($finCorte != null){
                    $fechaFin = $datos[$finCorte]['fecha'];
                }else{
                    $fechaFin = 'indefinido';
                }
                //if($i < count($datos)){
                    $cortes[] = new CorteLuz($datos[$i]['id'],
                                            $datos[$i]['direccion'],
                                            $fechaInicio,
                                            $fechaFin,
                                            $datos[$i]['latitud'],
                                            $datos[$i]['longitud'],
                                            $datos[$i]['dispositivo']);
                //}
            }
        }
        return $cortes;
    }

	

}

?>