package com.luiso.biblioteca;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModularidadTest {

	@Test
	void muestraLosModulos() {
		ApplicationModules.of(BibliotecaApplication.class).forEach(System.out::println);
	}

	@Test
	void verificaLaEstructuraModular() {
		ApplicationModules.of(BibliotecaApplication.class).verify();
	}
}
