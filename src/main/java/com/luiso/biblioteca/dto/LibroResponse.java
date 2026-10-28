package com.luiso.biblioteca.dto;

import java.io.Serializable;

public record LibroResponse(Long id, String titulo, String autor, int paginas) implements Serializable {

}