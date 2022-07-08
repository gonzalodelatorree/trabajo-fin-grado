<?php
  require_once './vendor/autoload.php';
  require_once './controlador/controlador.php';

  $loader = new \Twig\Loader\FilesystemLoader('templates');
  $twig = new \Twig\Environment($loader);

  session_start();

  $logged = false;
  if(isset($_SESSION['idUsuario'])){
    $logged = $_SESSION['idUsuario'];
  }

  $fechaInicio = 'nulo';
  $fechaFin = 'nulo';
  $dispositivoMostrado = 'nulo';

  if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		$fechaInicio = $_POST['fechaInicio'];
    $fechaFin = $_POST['fechaFin'];

    $dispositivoID = $_POST['dispositivoID'];

    if(isset($dispositivoID)){
      $cortes = Controlador::getCortesDispositivo($dispositivoID, $fechaInicio, $fechaFin);
      $dispositivoMostrado = $dispositivoID;

      Controlador::crearCSV($dispositivoID);
      Controlador::crearCSV();
    }else{
      $cortes = Controlador::getTodosCortesLuz($fechaInicio, $fechaFin);
      Controlador::crearCSV();
    }

  }else{
    date_default_timezone_set('UTC');

    $fechaHoy = date('Y-m-d', strtotime($fechaHoy. ' + 1 days'));
    $fecha7diasAntes = date("Y-m-d", strtotime($fecha_actual."- 7 days"));

    $cortes = Controlador::getTodosCortesLuz($fecha7diasAntes, $fechaHoy);

    Controlador::crearCSV();
  }

  $dispositivos = Controlador::getDispositivos();
  
  echo $twig->render('datos.html', ['logged' => $logged, 'cortes' => $cortes, 'fechaInicio' => $fechaInicio, 'fechaFin' => $fechaFin, 'dispositivos' => $dispositivos, 'dispositivoMostrado' => $dispositivoMostrado]);
?>