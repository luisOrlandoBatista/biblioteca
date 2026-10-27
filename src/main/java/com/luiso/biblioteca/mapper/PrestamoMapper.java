package com.luiso.biblioteca.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.luiso.biblioteca.dto.PrestamoResponse;
import com.luiso.biblioteca.entity.Prestamo;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PrestamoMapper {

    // target: el campo del DESTINO. source: de dónde sale, navegando con puntos
    @Mapping(target = "libroId", source = "libro.id")
    @Mapping(target = "tituloLibro", source = "libro.titulo")
    // expression: código Java literal, para lo que no es un getter
    @Mapping(target = "activo", expression = "java(prestamo.estaActivo())")
    PrestamoResponse aResponse(Prestamo prestamo);

    List<PrestamoResponse> aResponseLista(List<Prestamo> prestamos);
}