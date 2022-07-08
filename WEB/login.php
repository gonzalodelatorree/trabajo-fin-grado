<?php
  require_once './vendor/autoload.php';

  $loader = new \Twig\Loader\FilesystemLoader('templates');
  $twig = new \Twig\Environment($loader);

  session_start();

  if ($_SERVER['REQUEST_METHOD'] === 'POST') {
		$nick = $_POST['id_usuario'];

    if (md5($_POST['password']) === md5("1234") && $nick === "admin") {
  		$_SESSION['idUsuario'] = $nick;

  		header("Location: index.php");

    }
  }
  
  echo $twig->render('login.html', ['logged' => $_SESSION['idUsuario']]);
?>