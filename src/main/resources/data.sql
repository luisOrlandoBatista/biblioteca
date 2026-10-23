-- ON CONFLICT (titulo) DO NOTHING: si ya existe un libro con ese título, no lo inserta.
-- Así, al reiniciar la aplicación, no se duplican los datos.
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