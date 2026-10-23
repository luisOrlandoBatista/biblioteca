package com.luiso.biblioteca.demo;

import org.springframework.stereotype.Service;

@Service // singleton: Spring crea UNA sola instancia
public class ContadorService {

    private int visitas = 0; // estado guardado dentro del bean

    public int registrarVisita() {
        visitas++;
        return visitas;
    }
}
