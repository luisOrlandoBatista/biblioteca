package com.luiso.biblioteca.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.luiso.biblioteca.dto.LibroResponse;
import com.luiso.biblioteca.entity.Libro;

@Mapper(
        // La implementación generada será un bean de Spring (@Component): se inyecta como cualquier otro
        componentModel = MappingConstants.ComponentModel.SPRING,
        // Si un campo del DESTINO no se puede rellenar desde el origen → ERROR DE COMPILACIÓN.
        // Es lo que evita los null silenciosos
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface LibroMapper {

    // Relaciona los campos por nombre: getId() → id, getTitulo() → titulo...
    LibroResponse aResponse(Libro libro);

    // Para listas: MapStruct aplica el método anterior a cada elemento
    List<LibroResponse> aResponseLista(List<Libro> libros);
}