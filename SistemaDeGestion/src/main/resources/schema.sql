ALTER TABLE pedido ADD COLUMN IF NOT EXISTS met_de_pago VARCHAR(20) NOT NULL DEFAULT 'EFECTIVO';

CREATE TABLE IF NOT EXISTS caja_arqueo (
    id_arqueo BIGSERIAL PRIMARY KEY,
    id_caja BIGINT NOT NULL REFERENCES caja(id_caja),
    monto_inicial NUMERIC(12,2) NOT NULL,
    monto_final NUMERIC(12,2) NOT NULL,
    total_efectivo NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_debito NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_credito NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_transferencia NUMERIC(12,2) NOT NULL DEFAULT 0,
    cantidad_pedidos INTEGER NOT NULL DEFAULT 0,
    fecha_apertura TIMESTAMPTZ NOT NULL,
    fecha_cierre TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    cerrado_por BIGINT
);

ALTER TABLE proveedor ADD COLUMN IF NOT EXISTS nombre VARCHAR(150);
ALTER TABLE proveedor ADD COLUMN IF NOT EXISTS telefono VARCHAR(30);
ALTER TABLE proveedor ADD COLUMN IF NOT EXISTS email VARCHAR(150);
ALTER TABLE proveedor ADD COLUMN IF NOT EXISTS direccion VARCHAR(255);
ALTER TABLE proveedor ADD COLUMN IF NOT EXISTS ciudad VARCHAR(100);
ALTER TABLE proveedor ADD COLUMN IF NOT EXISTS fecha_creacion TIMESTAMPTZ;
ALTER TABLE proveedor ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT true;
ALTER TABLE producto ADD COLUMN IF NOT EXISTS id_proveedor BIGINT REFERENCES proveedor(id_proveedor);

UPDATE proveedor SET nombre = 'Proveedor ' || id_proveedor WHERE nombre IS NULL;
UPDATE proveedor SET fecha_creacion = NOW() WHERE fecha_creacion IS NULL;
UPDATE proveedor SET ciudad = 'Sin ciudad' WHERE ciudad IS NULL;
UPDATE proveedor SET activo = true WHERE activo IS NULL;

ALTER TABLE proveedor ALTER COLUMN correo DROP NOT NULL;
ALTER TABLE proveedor ALTER COLUMN razon_social DROP NOT NULL;
ALTER TABLE proveedor ALTER COLUMN cuit_rut DROP NOT NULL;
ALTER TABLE proveedor ALTER COLUMN fecha_alta DROP NOT NULL;
ALTER TABLE proveedor DROP CONSTRAINT IF EXISTS uk5gqejexbar9nni42kh4vfusu0;
ALTER TABLE proveedor DROP CONSTRAINT IF EXISTS uk_cuit_rut;
ALTER TABLE proveedor DROP CONSTRAINT IF EXISTS proveedor_cuit_rut_key;
