const e = require('express');
const { response } = require('express');
const express = require('express');
const rutas = express.Router();


//-------------------------------RUTAS DISPOSITIVO -------------------------------

rutas.get('/dispositivos/:id', (req, res) =>{

    req.getConnection((error, conexion) => {
        if(error) return res.send(error);
 
        conexion.query("SELECT * FROM dispositivo WHERE id=?", [req.params.id], (error, resultado) =>{
            if(error) return res.send(error);
            res.json(resultado);
        });
    })
})

rutas.get('/dispositivosID/', (req, res) =>{
    
    req.getConnection((error, conexion) => {
        if(error) return res.send(error);
 
        conexion.query("SELECT id FROM dispositivo", (error, resultado) =>{
            if(error) return res.json(error);
            res.json(resultado);
        });
    })
})

rutas.get('/dispositivos', (req, res) =>{
    
    req.getConnection((error, conexion) => {
        if(error) return res.send(error);
 
        conexion.query("SELECT * FROM dispositivo", (error, resultado) =>{
            if(error) return res.json(error);

            res.json(resultado);
        });
    })
})

rutas.post('/dispositivos', (req, res) =>{
    req.getConnection((error, conexion) => {
        if(error) return res.send(error);

        const json = req.body;
        console.log(json);
 
        conexion.query("INSERT INTO dispositivo(direccion, personaContacto, emailContacto, telefonoContacto, longitud, latitud) VALUES(?,?,?,?,?,?)", 
            [json.direccion, json.personaContacto, json.emailContacto, json.telefonoContacto, json.latitud, json.longitud], 
            (error, resultado) =>{

            if(resultado) res.json(resultado);
            else res.json(error);
        });
        
    })
})

rutas.put('/dispositivos/:id', (req, res) =>{
   req.getConnection((error, conexion) => {
       if(error) return res.send(error);
 
       const json = req.body;
 
        conexion.query("UPDATE dispositivo SET direccion=?, personaContacto=?, emailContacto=?, telefonoContacto=?, latitud=?, longitud=? WHERE id=?",
            [json.direccion, json.personaContacto, json.emailContacto, json.telefonoContacto, json.latitud, json.longitud, req.params.id], 
            (error, resultado) =>{

                console.log(resultado);
                console.log(error);

            if(resultado) res.json(resultado);
            else res.json(error);
        });
   })
})

//-------------------------------RUTAS MEDIDAS -------------------------------

rutas.get('/medidas', (req, res) =>{
    req.getConnection((error, conexion) => {
        if(error) return res.send(error);

        var sql = "SELECT * FROM medida";
        var input = []

        if(req.query.dispositivo){
            sql = "SELECT * FROM medida WHERE dispositivo=?";
            input = [req.query.dispositivo];
        }
 
        conexion.query(sql, input, (error, resultado) =>{
            if(error) return res.send(error);
            res.json(resultado);
        });
    })
})

rutas.get('/medidas/:id', (req, res) =>{
    req.getConnection((error, conexion) => {
        if(error) return res.send(error);
 
        conexion.query("SELECT * FROM medida WHERE id=?", [req.params.id], (error, resultado) =>{
            if(error) return res.send(error);
            res.json(resultado);
        });
    })
})

rutas.post('/medidas', (req, res) =>{
    req.getConnection((error, conexion) => {
        if(error) return res.send(error);

        const json = req.body;
        const tamJSON = json.length;
        var filasFallo = 0;

        for(let i=1; i<tamJSON; i++){
            conexion.query("INSERT INTO medida(tipo, fecha, dispositivo) VALUES(?, NOW(),?)", 
                [json[i].tipo, json[0].dispositivo], 
                (error, resultadoFila) =>{

                var filasFallo=0;
                if(error) {
                    filasFallo = filasFallo +1;
                }
                console.log(resultadoFila);
            });
        } 
        if(filasFallo == 0) res.json([{respuesta: "Filas insertadas correctamente!"}]);
        else res.json([{respuesta: "Filas que no se han podido insertar: ${filasFallo}"}]);
        
    })
})

/*--------------------------- RUTAS MAS ELABORADAS -------------------*/
rutas.get('/medidas/dispositivo/:id', (req, res) =>{
    req.getConnection((error, conexion) => {
        if(error) return res.send(error);

        var sql;
        var input;

        if(req.query.fIni && req.query.fFin){
            sql = "SELECT dispositivo.id AS dispositivo, direccion, latitud, longitud, tipo, fecha  FROM medida JOIN dispositivo WHERE (dispositivo.id = ? && dispositivo.id = medida.dispositivo && fecha BETWEEN ? AND ?);";
            input = [req.params.id, req.query.fIni, req.query.fFin]
        }else{
            sql = "SELECT dispositivo.id AS dispositivo, direccion, latitud, longitud, tipo, fecha  FROM medida JOIN dispositivo " + 
            "WHERE (dispositivo.id = ? && dispositivo.id = medida.dispositivo)"
            input = [req.params.id];
        }
 
        conexion.query(sql, input, (error, resultado) =>{
            if(error) return res.send(error);
            res.json(resultado);
        });
    })
})

module.exports = rutas;