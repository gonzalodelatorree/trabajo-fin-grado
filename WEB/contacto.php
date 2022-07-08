<?php
  require_once './vendor/autoload.php';

  $loader = new \Twig\Loader\FilesystemLoader('templates');
  $twig = new \Twig\Environment($loader);

  session_start();

  echo $twig->render('contacto.html', ['logged' => $_SESSION['idUsuario']]);
?>