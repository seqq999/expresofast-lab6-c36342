CREATE DATABASE ExpresoFast_c36342_II2026;
GO

USE ExpresoFast_c36342_II2026;
GO

CREATE TABLE EmpresaLogistica(
	empresa_id INT IDENTITY(1,1) PRIMARY KEY,
	nombre VARCHAR(100) NOT NULL UNIQUE,
	cedula_juridica VARCHAR(20) NOT NULL UNIQUE,
	telefono VARCHAR(20) NOT NULL,
	fecha_registro DATETIME NOT NULL
);

CREATE TABLE Vehiculo(
	vehiculo_id INT IDENTITY(1,1) PRIMARY KEY,
	placa VARCHAR(15) NOT NULL UNIQUE,
	capacidad_kg DECIMAL(10,2) NOT NULL,
	estado VARCHAR(20) NOT NULL CHECK (estado IN ('DISPONIBLE', 'EN_RUTA', 'MANTENIMIENTO')),
	empresa_id INT NOT NULL,
	CONSTRAINT FK_Vehiculo_Empresa FOREIGN KEY (empresa_id) REFERENCES EmpresaLogistica(empresa_id)
);

CREATE TABLE Conductor(
	conductor_id INT IDENTITY(1,1) PRIMARY KEY,
	nombre VARCHAR(50) NOT NULL,
	apellidos VARCHAR(50) NOT NULL,
	licencia VARCHAR(20) NOT NULL UNIQUE,
	telefono VARCHAR(20) NOT NULL
);

CREATE TABLE Envio(
	envio_id INT IDENTITY(1,1) PRIMARY KEY,
	codigo_rastreo VARCHAR(30) NOT NULL UNIQUE,
	direccion_destino VARCHAR(200) NOT NULL,
	peso_kg DECIMAL(10,2) NOT NULL,
	costo DECIMAL(10,2) NOT NULL,
	estado_envio VARCHAR(20) NOT NULL CHECK (estado_envio IN ('PENDIENTE', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO')),
	vehiculo_id INT NOT NULL,
	conductor_id INT NOT NULL,
	fecha_creacion DATETIME NULL,
	fecha_modificacion DATETIME NULL,
	CONSTRAINT FK_Vehiculo_Envio FOREIGN KEY (vehiculo_id) REFERENCES Vehiculo(vehiculo_id),
	CONSTRAINT FK_Conductor_Envio FOREIGN KEY (conductor_id) REFERENCES Conductor(conductor_id)
);
