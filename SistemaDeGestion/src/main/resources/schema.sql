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
