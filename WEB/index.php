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
  
echo $twig->render('index.html', ['logged' => $logged]);
?>