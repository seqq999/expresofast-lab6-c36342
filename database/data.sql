USE ExpresoFast_c36342_II2026;
GO

IF NOT EXISTS (SELECT 1 FROM dbo.EmpresaLogistica WHERE cedula_juridica = '3-101-123456')
    INSERT INTO dbo.EmpresaLogistica (nombre, cedula_juridica, telefono, fecha_registro)
    VALUES ('ExpresoFast S.A.', '3-101-123456', '2222-3333', '2026-01-15');

DECLARE @empresa INT = (SELECT empresa_id FROM dbo.EmpresaLogistica WHERE cedula_juridica = '3-101-123456');

IF NOT EXISTS (SELECT 1 FROM dbo.Vehiculo WHERE placa = 'CL-100001')
    INSERT INTO dbo.Vehiculo (placa, capacidad_kg, estado, empresa_id)
    VALUES ('CL-100001', 1500.00, 'DISPONIBLE', @empresa);

IF NOT EXISTS (SELECT 1 FROM dbo.Vehiculo WHERE placa = 'CL-100002')
    INSERT INTO dbo.Vehiculo (placa, capacidad_kg, estado, empresa_id)
    VALUES ('CL-100002', 3000.00, 'EN_RUTA', @empresa);

IF NOT EXISTS (SELECT 1 FROM dbo.Conductor WHERE licencia = 'LIC-0001')
    INSERT INTO dbo.Conductor (nombre, apellidos, licencia, telefono)
    VALUES ('Roberto', 'Mora Salas', 'LIC-0001', '8888-1111');

IF NOT EXISTS (SELECT 1 FROM dbo.Conductor WHERE licencia = 'LIC-0002')
    INSERT INTO dbo.Conductor (nombre, apellidos, licencia, telefono)
    VALUES ('Elena', 'Quirós Vargas', 'LIC-0002', '8888-2222');

DECLARE @veh1  INT = (SELECT vehiculo_id  FROM dbo.Vehiculo  WHERE placa = 'CL-100001');
DECLARE @veh2  INT = (SELECT vehiculo_id  FROM dbo.Vehiculo  WHERE placa = 'CL-100002');
DECLARE @cond1 INT = (SELECT conductor_id FROM dbo.Conductor WHERE licencia = 'LIC-0001');
DECLARE @cond2 INT = (SELECT conductor_id FROM dbo.Conductor WHERE licencia = 'LIC-0002');

DECLARE @semilla TABLE (
    codigo_rastreo    VARCHAR(30),
    destinatario      VARCHAR(100),
    direccion_destino VARCHAR(200),
    peso_kg           DECIMAL(10,2),
    costo             DECIMAL(10,2),
    estado_envio      VARCHAR(20),
    vehiculo_id       INT,
    conductor_id      INT,
    fecha_creacion    DATETIME
);

INSERT INTO @semilla VALUES
('EF-2026-0001', 'María Fernández Rojas',   'San José, Escazú, 200 m norte del Multiplaza',    12.50, 4500.00, 'PENDIENTE',   @veh1, @cond1, '2026-09-01 08:15'),
('EF-2026-0002', 'Carlos Jiménez Mora',     'Cartago, Paraíso, frente a la iglesia',            8.00, 3200.00, 'EN_TRANSITO', @veh2, @cond2, '2026-09-01 09:40'),
('EF-2026-0003', 'Ana Lucía Vargas',        'Heredia, Barva, 100 m sur del parque',            20.30, 5100.00, 'ENTREGADO',   @veh1, @cond1, '2026-09-02 10:05'),
('EF-2026-0004', 'Luis Alberto Solano',     'Alajuela, Grecia, barrio San Roque',               5.75, 2800.00, 'CANCELADO',   @veh2, @cond2, '2026-09-02 11:30'),
('EF-2026-0005', 'Daniela Castro Araya',    'Limón, Siquirres, contiguo a la escuela',         35.00, 6700.00, 'PENDIENTE',   @veh1, @cond2, '2026-09-03 07:50'),
('EF-2026-0006', 'Jorge Esteban Quesada',   'Puntarenas, Esparza, 50 m oeste de la plaza',     42.10, 7400.00, 'EN_TRANSITO', @veh2, @cond1, '2026-09-03 13:20'),
('EF-2026-0007', 'Sofía Ramírez Chaves',    'San José, Desamparados, Urb. Los Pinos casa 12',   9.90, 3900.00, 'ENTREGADO',   @veh1, @cond1, '2026-09-04 08:00'),
('EF-2026-0008', 'Andrés Salazar Brenes',   'Cartago, Turrialba, 300 m este del hospital',     15.60, 4600.00, 'PENDIENTE',   @veh2, @cond2, '2026-09-04 15:45'),
('EF-2026-0009', 'Valeria Núñez Campos',    'Guanacaste, Liberia, Barrio Capulín',             55.00, 8200.00, 'EN_TRANSITO', @veh1, @cond2, '2026-09-05 09:10'),
('EF-2026-0010', 'Ricardo Alvarado Pérez',  'Heredia, Santo Domingo, calle principal',          7.25, 3500.00, 'ENTREGADO',   @veh2, @cond1, '2026-09-05 12:25'),
('EF-2026-0011', 'Paola Mendoza Ulate',     'Alajuela, San Ramón, Res. Las Palmas',            11.00, 4100.00, 'CANCELADO',   @veh1, @cond1, '2026-09-06 10:35'),
('EF-2026-0012', 'Kevin Rodríguez Blanco',  'San José, Curridabat, 75 m sur del BAC',          18.40, 5300.00, 'PENDIENTE',   @veh2, @cond2, '2026-09-07 08:55'),
('EF-2026-0013', 'Natalia Soto Herrera',    'Cartago, Paraíso, Barrio Los Ángeles',             6.80, 2950.00, 'EN_TRANSITO', @veh1, @cond1, '2026-09-08 14:00'),
('EF-2026-0014', 'Mauricio Cordero Vega',   'Limón, Guápiles, 100 m norte del banco',          38.20, 6900.00, 'ENTREGADO',   @veh2, @cond2, '2026-09-09 11:15'),
('EF-2026-0015', 'Karla Montero Zúñiga',    'Puntarenas, Jacó, frente a la playa',             60.00, 9100.00, 'PENDIENTE',   @veh1, @cond2, '2026-09-10 09:30'),
('EF-2026-0016', 'Esteban Villalobos Cruz', 'Heredia, Flores, contiguo al colegio',             10.10, 3700.00, 'CANCELADO',   @veh2, @cond1, '2026-09-12 16:20'),
('EF-2026-0017', 'Gabriela Arce Sandí',     'San José, Tibás, Urb. La Florida casa 5',         14.70, 4800.00, 'EN_TRANSITO', @veh1, @cond1, '2026-09-15 08:40'),
('EF-2026-0018', 'Fabián Ugalde Picado',    'Alajuela, Atenas, 200 m oeste del parque',        22.90, 5600.00, 'ENTREGADO',   @veh2, @cond2, '2026-09-18 13:05');

INSERT INTO dbo.Envio (codigo_rastreo, destinatario, direccion_destino, peso_kg, costo,
                       estado_envio, vehiculo_id, conductor_id, fecha_creacion, fecha_modificacion)
SELECT s.codigo_rastreo, s.destinatario, s.direccion_destino, s.peso_kg, s.costo,
       s.estado_envio, s.vehiculo_id, s.conductor_id, s.fecha_creacion, s.fecha_creacion
FROM @semilla s
WHERE NOT EXISTS (SELECT 1 FROM dbo.Envio e WHERE e.codigo_rastreo = s.codigo_rastreo);
GO

-- Verificación
SELECT estado_envio, COUNT(*) AS total FROM dbo.Envio GROUP BY estado_envio;

