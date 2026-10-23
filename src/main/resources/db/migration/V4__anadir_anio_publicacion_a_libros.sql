-- Columna nueva. Es NULLABLE (sin NOT NULL) porque los libros que ya existen no tienen año.
-- Añadir una columna NOT NULL a una tabla con datos fallaría, salvo que se indique un DEFAULT.
ALTER TABLE libros ADD COLUMN anio_publicacion INTEGER;