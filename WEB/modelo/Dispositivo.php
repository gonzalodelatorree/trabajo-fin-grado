<?php

require_once('CorteLuz.php');

class Dispositivo{
	private $idDispositivo;
	private $direccion;
	private $personaContacto;
    private $telefonoContacto;
	private $emailContacto;
	private $latitud;
	private $longitud;
    
    private $cortesLuz = [];

	public function __construct($idDispositivo){
        $this->idDispositivo = $idDispositivo;

        $response = file_get_contents('http://localhost:4000/api/dispositivos/'. $this->idDispositivo);
        $dispositivo = json_decode($response, true);
        $this->direccion = $dispositivo[0]['direccion'];
        $this->personaContacto = $dispositivo[0]['personaContacto'];
		$this->telefonoContacto = $dispositivo[0]['telefonoContacto'];
        $this->emailContacto = $dispositivo[0]['emailContacto'];
        $this->latitud = $dispositivo[0]['latitud'];
        $this->longitud = $dispositivo[0]['longitud'];


    }

    public function getCortesLuz($fIni = null, $fFin = null){
        if($this->idDispositivo != null){

            if($fIni === null || $fFin === null){
                $response = file_get_contents('http://localhost:4000/api/medidas/dispositivo/'. $this->idDispositivo);
            }else{
                $response = file_get_contents('http://localhost:4000/api/medidas/dispositivo/'. $this->idDispositivo .'?fIni='. $fIni . '&fFin='. $fFin);
            }
            $datos = json_decode($response, true);

            $medidaAnterior = null;
            for($i=0; $i<count($datos); $i++){

                $medidaActual = $datos[$i]['tipo'];
                
                if($medidaAnterior === 'D' && $medidaActual === 'U'){

                    $fechaI = $datos[$i-1]['fecha'];
                    $fechaF = $datos[$i]['fecha'];

                    $fechaI = new DateTime($fechaI, new DateTimeZone('UTC'));
                    $fechaI->setTimezone(new DateTimeZone('Europe/Madrid'));

                    $fechaF = new DateTime($fechaF, new DateTimeZone('UTC'));
                    $fechaF->setTimezone(new DateTimeZone('Europe/Madrid'));

                    $fechaIni = date_format($fechaI, 'Y-m-d H:i');
                    $fechaFin = date_format($fechaI, 'Y-m-d H:i');

                    $this->cortesLuz[] = new CorteLuz($this->idDispositivo,
                                                    $this->direccion,
                                                    $fechaIni,
                                                    $fechaFin,
                                                    $this->latitud,
                                                    $this->longitud);
                }
                $medidaAnterior = $medidaActual;
            }
        }
        return $this->cortesLuz;
    }

    public function getIdDispositivo(){
        return $this->idDispositivo;
    }

    public function getDireccion(){
        return $this->direccion;
    }

    public function getPersonaContacto(){
        return $this->personaContacto;
    }

    public function getTelefonoContacto(){
        return $this->telefonoContacto;
    }

    public function getEmailContacto(){
        return $this->emailContacto;
    }

    public function getLatitud(){
        return $this->latitud;
    }

    public function getLongitud(){
        return $this->longitud;
    }
}

?>