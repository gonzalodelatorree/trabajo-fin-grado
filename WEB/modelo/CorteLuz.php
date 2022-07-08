<?php

class CorteLuz{
	private $idDispositivo;
	private $direccion;
	private $inicio;
	private $fin;
	private $latitud;
	private $longitud;

	public function __construct($idDispositivo, $direccion, $inicio, $fin, $latitud, $longitud){
        $this->idDispositivo = $idDispositivo;
        $this->direccion = $direccion;
        $this->inicio = $inicio;
		$this->fin = $fin;
        $this->latitud = $latitud;
        $this->longitud = $longitud;
    }

    public function getIdDispositivo(){
        return $this->idDispositivo;
    }

    public function getDireccion(){
        return $this->direccion;
    }

    public function getInicioCorte(){
        return $this->inicio;
    }

    public function getFinCorte(){
        return $this->fin;
    }

    public function getLatitud(){
        return $this->latitud;
    }

    public function getLongitud(){
        return $this->longitud;
    }

}

?>