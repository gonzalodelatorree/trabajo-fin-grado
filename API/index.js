const express = require('express');
const mysql = require('mysql2');
const parser = require('body-parser');
const miConexion = require('express-myconnection');
const rutas = require('./rutas');

const PUERTO = 4000;

const app = express();

const conexionDatos = {
    host: 'localhost',
    user: 'nomascortes',
    password: 'nomascortes',
    database: 'nomascortes'
};
app.use(miConexion(mysql, conexionDatos, 'single'));

app.use(parser.json());
app.use('/API', rutas);
app.use(express.json());

app.listen(PUERTO, ()=> {
    console.log('Servidor Ejecutando en puerto ' + PUERTO);
})