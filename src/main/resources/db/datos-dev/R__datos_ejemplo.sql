-- Migración REPETIBLE (R__): se ejecuta después de las versionadas.
-- Las sentencias son idempotentes (se pueden ejecutar varias veces sin duplicar datos),
-- porque Flyway la volverá a ejecutar si algún día cambias este archivo.

INSERT INTO libros (titulo, autor, paginas) VALUES
    ('Clean Code', 'Robert C. Martin', 464),
    ('Effective Java', 'Joshua Bloch', 412),
    ('Spring in Action', 'Craig Walls', 520),
    ('Refactoring', 'Martin Fowler', 448),
    ('Design Patterns', 'Erich Gamma', 395),
    ('The Pragmatic Programmer', 'David Thomas', 352),
    ('Domain-Driven Design', 'Eric Evans', 560),
    ('Clean Architecture', 'Robert C. Martin', 432),
    ('Java Concurrency in Practice', 'Brian Goetz', 424),
    ('Head First Design Patterns', 'Eric Freeman', 694),
    ('Working Effectively with Legacy Code', 'Michael Feathers', 456),
    ('Building Microservices', 'Sam Newman', 616)
ON CONFLICT (titulo) DO NOTHING;

INSERT INTO prestamos (libro_id, socio, fecha_prestamo, fecha_devolucion)
SELECT l.id, v.socio, v.fecha_prestamo, v.fecha_devolucion
FROM (VALUES
    ('Effective Java',         'Ana García',   DATE '2026-09-01', DATE '2026-09-15'),
    ('Effective Java',         'Pedro López',  DATE '2026-09-20', NULL),
    ('Refactoring',            'Ana García',   DATE '2026-09-05', DATE '2026-09-25'),
    ('Domain-Driven Design',   'Lucía Martín', DATE '2026-10-01', NULL),
    ('Building Microservices', 'Pedro López',  DATE '2026-10-03', NULL),
    ('Refactoring',            'Carlos Ruiz',  DATE '2026-10-10', NULL)
) AS v(titulo, socio, fecha_prestamo, fecha_devolucion)
JOIN libros l ON l.titulo = v.titulo
WHERE NOT EXISTS (SELECT 1 FROM prestamos);