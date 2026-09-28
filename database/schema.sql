USE ExpresoFast_c36342_II2026;
GO

IF COL_LENGTH('dbo.Envio', 'destinatario') IS NULL
BEGIN
    ALTER TABLE dbo.Envio
        ADD destinatario VARCHAR(100) NOT NULL
            CONSTRAINT DF_Envio_destinatario DEFAULT 'Sin especificar';
END
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes
               WHERE name = 'IX_Envio_estado_fecha' AND object_id = OBJECT_ID('dbo.Envio'))
BEGIN
    CREATE INDEX IX_Envio_estado_fecha
        ON dbo.Envio (estado_envio, fecha_creacion DESC);
END
GO

CREATE OR ALTER PROCEDURE dbo.SP_OBTENER_ENVIOS_POR_ESTADO
    @pEstado VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT envio_id, codigo_rastreo, destinatario, direccion_destino,
           peso_kg, costo, estado_envio, vehiculo_id, conductor_id,
           fecha_creacion, fecha_modificacion
    FROM dbo.Envio
    WHERE estado_envio = @pEstado
    ORDER BY fecha_creacion DESC;
END
GO

CREATE OR ALTER PROCEDURE dbo.SP_RESUMEN_METRICAS_ENVIOS
AS
BEGIN
    SET NOCOUNT ON;

    SELECT estado_envio  AS estado,
           COUNT(*)      AS total_envios,
           SUM(costo)    AS total_flete
    FROM dbo.Envio
    GROUP BY estado_envio
    ORDER BY estado_envio;
END
GO

-- Pruebas rápidas
EXEC dbo.SP_OBTENER_ENVIOS_POR_ESTADO @pEstado = 'PENDIENTE';
EXEC dbo.SP_RESUMEN_METRICAS_ENVIOS;