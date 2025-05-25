-- Drop the old unique index on id_user (el nombre puede variar; usa SHOW INDEX para confirmarlo)
ALTER TABLE Carrito
DROP INDEX UK2n2s0x5xd11oyj0cl7jxlhicb;

-- (Opcional) Recrear un índice normal para rendimiento de consultas
CREATE INDEX idx_carrito_user ON Carrito(id_user);
