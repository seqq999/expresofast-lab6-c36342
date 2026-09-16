USE ExpresoFast_c36342_II2026;
GO

-- inserción de datos iniciales
INSERT INTO EmpresaLogistica (nombre, cedula_juridica, telefono, fecha_registro) VALUES
('Logística Del Sol S.A.', '3-101-456789', '2551-1111', GETDATE()),
('Transportes Rápidos CR S.R.L.', '3-102-654321', '2222-3333', GETDATE()),
('Carga Express del Atlántico S.A.', '3-101-789012', '2750-4444', GETDATE()),
('Fletes y Envíos Centrales S.A.', '3-101-987654', '2430-5555', GETDATE()),
('Servicios Logísticos del Pacífico S.R.L.', '3-102-123456', '2660-6666', GETDATE());

INSERT INTO Conductor (nombre, apellidos, licencia, telefono) VALUES
('Carlos', 'Mora Jiménez', 'LIC-001234', '8888-1111'),
('María', 'Rojas Solano', 'LIC-005678', '8777-2222'),
('Esteban', 'Chaves Quesada', 'LIC-009012', '8666-3333'),
('Valeria', 'Vargas Castro', 'LIC-003456', '8555-4444'),
('José', 'Monge Alvarado', 'LIC-007890', '8444-5555');

INSERT INTO Vehiculo (placa, capacidad_kg, estado, empresa_id) VALUES
('CL-123456', 1500.00, 'DISPONIBLE', 1),
('CL-654321', 3500.50, 'EN_RUTA', 1),
('CL-789012', 2000.00, 'DISPONIBLE', 2),
('CL-345678', 5000.00, 'MANTENIMIENTO', 3),
('CL-901234', 1200.75, 'EN_RUTA', 4);


BEGIN TRANSACTION;

DECLARE @UsuarioId INT;
DECLARE @RolId INT;

IF NOT EXISTS (
    SELECT 1 FROM dbo.Rol
    WHERE nombre_rol = 'ROLE_ADMIN'
)
BEGIN
    INSERT INTO dbo.Rol (nombre_rol)
    VALUES ('ROLE_ADMIN');
END;

SELECT @RolId = rol_id
FROM dbo.Rol
WHERE nombre_rol = 'ROLE_ADMIN';

IF NOT EXISTS (
    SELECT 1 FROM dbo.Usuario
    WHERE username = 'admin'
)
BEGIN
    INSERT INTO dbo.Usuario
        (username, password_hash, nombre_completo, email, activo)
    VALUES
        (
            'admin',
            '$2a$10$EJ7jfjQQoa.Fv8lMdQQbfe4YSfcoBmKvqO9/IoJKzV.Dk8NPamTja',
            'Administrador Principal',
            'admin@expresofast.cr',
            1
        );
END
ELSE
BEGIN
    UPDATE dbo.Usuario
    SET password_hash =
        '$2a$10$EJ7jfjQQoa.Fv8lMdQQbfe4YSfcoBmKvqO9/IoJKzV.Dk8NPamTja',
        activo = 1
    WHERE username = 'admin';
END;

SELECT @UsuarioId = usuario_id
FROM dbo.Usuario
WHERE username = 'admin';

IF NOT EXISTS (
    SELECT 1
    FROM dbo.UsuarioROL
    WHERE usuario_id = @UsuarioId
      AND rol_id = @RolId
)
BEGIN
    INSERT INTO dbo.UsuarioROL (usuario_id, rol_id)
    VALUES (@UsuarioId, @RolId);
END;

COMMIT TRANSACTION;

SELECT
    u.username,
    u.activo,
    r.nombre_rol
FROM dbo.Usuario u
INNER JOIN dbo.UsuarioROL ur ON ur.usuario_id = u.usuario_id
INNER JOIN dbo.Rol r ON r.rol_id = ur.rol_id
WHERE u.username = 'admin';



BEGIN TRANSACTION;

DECLARE @OperadorId INT;
DECLARE @ConductorId INT;
DECLARE @RolOperadorId INT;
DECLARE @RolConductorId INT;

-- Asegurar roles
IF NOT EXISTS (
    SELECT 1 FROM dbo.Rol
    WHERE nombre_rol = 'ROLE_OPERADOR'
)
    INSERT INTO dbo.Rol (nombre_rol) VALUES ('ROLE_OPERADOR');

IF NOT EXISTS (
    SELECT 1 FROM dbo.Rol
    WHERE nombre_rol = 'ROLE_CONDUCTOR'
)
    INSERT INTO dbo.Rol (nombre_rol) VALUES ('ROLE_CONDUCTOR');

SELECT @RolOperadorId = rol_id
FROM dbo.Rol
WHERE nombre_rol = 'ROLE_OPERADOR';

SELECT @RolConductorId = rol_id
FROM dbo.Rol
WHERE nombre_rol = 'ROLE_CONDUCTOR';

-- Crear o actualizar operador
IF NOT EXISTS (
    SELECT 1 FROM dbo.Usuario
    WHERE username = 'operador'
)
BEGIN
    INSERT INTO dbo.Usuario
        (username, password_hash, nombre_completo, email, activo)
    VALUES
        (
            'operador',
            '$2a$10$6lcNfgOCZBZ2nieC6joOOeH0qRMbuHXyztYLeBrPbRkDm6TbeDFJy',
            'Operador Principal',
            'operador@expresofast.cr',
            1
        );
END
ELSE
BEGIN
    UPDATE dbo.Usuario
    SET password_hash =
        '$2a$10$6lcNfgOCZBZ2nieC6joOOeH0qRMbuHXyztYLeBrPbRkDm6TbeDFJy',
        activo = 1
    WHERE username = 'operador';
END;

-- Crear o actualizar conductor
IF NOT EXISTS (
    SELECT 1 FROM dbo.Usuario
    WHERE username = 'conductor'
)
BEGIN
    INSERT INTO dbo.Usuario
        (username, password_hash, nombre_completo, email, activo)
    VALUES
        (
            'conductor',
            '$2a$10$hgow.6i.FTBXh8uf4SZWCezgAmpcZFOuHZdY17.0R5lHyoyzIDYM2',
            'Conductor Principal',
            'conductor@expresofast.cr',
            1
        );
END
ELSE
BEGIN
    UPDATE dbo.Usuario
    SET password_hash =
        '$2a$10$hgow.6i.FTBXh8uf4SZWCezgAmpcZFOuHZdY17.0R5lHyoyzIDYM2',
        activo = 1
    WHERE username = 'conductor';
END;

SELECT @OperadorId = usuario_id
FROM dbo.Usuario
WHERE username = 'operador';

SELECT @ConductorId = usuario_id
FROM dbo.Usuario
WHERE username = 'conductor';

-- Asignar roles
IF NOT EXISTS (
    SELECT 1 FROM dbo.UsuarioROL
    WHERE usuario_id = @OperadorId
      AND rol_id = @RolOperadorId
)
    INSERT INTO dbo.UsuarioROL (usuario_id, rol_id)
    VALUES (@OperadorId, @RolOperadorId);

IF NOT EXISTS (
    SELECT 1 FROM dbo.UsuarioROL
    WHERE usuario_id = @ConductorId
      AND rol_id = @RolConductorId
)
    INSERT INTO dbo.UsuarioROL (usuario_id, rol_id)
    VALUES (@ConductorId, @RolConductorId);

COMMIT TRANSACTION;

-- Verificación
SELECT
    u.username,
    u.activo,
    r.nombre_rol
FROM dbo.Usuario u
INNER JOIN dbo.UsuarioROL ur ON ur.usuario_id = u.usuario_id
INNER JOIN dbo.Rol r ON r.rol_id = ur.rol_id
WHERE u.username IN ('operador', 'conductor');

select * from UsuarioROL;

