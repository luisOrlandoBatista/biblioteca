-- PostgreSQL NO indexa automáticamente las claves foráneas.
-- Este índice acelera los JOIN entre prestamos y libros.
CREATE INDEX ix_prestamos_libro_id ON prestamos (libro_id);

-- Acelera la regla de "máximo de préstamos por socio" (paso 4.5)
CREATE INDEX ix_prestamos_socio ON prestamos (socio);