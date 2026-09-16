CREATE TABLE Usuario(
	usuario_id INT IDENTITY(1,1) PRIMARY KEY,
	username VARCHAR(20) NOT NULL UNIQUE,
	password_hash VARCHAR(255) NOT NULL,
	nombre_completo VARCHAR(100) NOT NULL,
	email VARCHAR (100) NOT NULL UNIQUE,
	activo BIT NOT NULL DEFAULT 1
);

CREATE TABLE Rol(
	rol_id INT IDENTITY (1,1) PRIMARY KEY,
	nombre_rol VARCHAR(30) NOT NULL UNIQUE
		CHECK(nombre_rol IN('ROLE_ADMIN', 'ROLE_OPERADOR', 'ROLE_CONDUCTOR'))
);

CREATE TABLE UsuarioROL(
	usuario_id INT NOT NULL,
	rol_id INT NOT NULL,
		PRIMARY KEY (usuario_id, rol_id),
	CONSTRAINT FK_UsuarioRol_Usuario FOREIGN KEY (usuario_id)
		REFERENCES Usuario(usuario_id)
		ON DELETE CASCADE,

	CONSTRAINT FK_UsuariosRoles_Roles FOREIGN KEY (rol_id) 
        REFERENCES Rol(rol_id)
        ON DELETE CASCADE
);

CREATE TABLE BitacoraEnvio(
	bitacora_id INT IDENTITY(1,1) PRIMARY KEY,
	envio_id INT,
	estado_anterior VARCHAR(20) not null,
	estado_nuevo VARCHAR (20) not null, 
	fecha_cambio DATETIME not null, 
	usuario_id INT,
	observaciones VARCHAR(250) null,

	CONSTRAINT FK_BitacoraEnvio_EnvioId FOREIGN KEY (envio_id)
		REFERENCES Envio(envio_id),
	
	CONSTRAINT FK_BitacoraEnvio_Usuario FOREIGN KEY (usuario_id)
		REFERENCES Usuario(usuario_id),
			
);
