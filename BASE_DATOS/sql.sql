CREATE TABLE dispositivo (
	id INT AUTO_INCREMENT PRIMARY KEY,
	direccion VARCHAR(40) NOT NULL,
	personaContacto VARCHAR(50) NOT NULL,
	emailContacto VARCHAR(30),
	telefonoContacto VARCHAR(15),
	latitud DECIMAL(8,6),
	longitud DECIMAL(9,6)
);

ALTER TABLE dispositivo AUTO_INCREMENT = 100;

CREATE TABLE medida (
	id INT AUTO_INCREMENT PRIMARY KEY,
	tipo CHAR(1) NOT NULL,
	fecha TIMESTAMP NOT NULL,
	dispositivo INT NOT NULL,
	FOREIGN KEY (dispositivo) REFERENCES dispositivo(id)
	ON UPDATE CASCADE
	ON DELETE CASCADE,

	CONSTRAINT tipo_cnstr CHECK(tipo in('S', 'U', 'D'))
);