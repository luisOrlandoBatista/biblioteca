package com.luiso.biblioteca.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// @RestControllerAdvice: esta clase captura las excepciones de TODOS los controladores.
// Al heredar de ResponseEntityExceptionHandler, todos los errores propios de Spring
// (400 por tipo incorrecto, 404 por ruta inexistente, 405 por método no permitido...)
// se devuelven automáticamente como ProblemDetail.
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404: el libro no existe
    @ExceptionHandler(LibroNoEncontradoException.class)
    public ProblemDetail manejarNoEncontrado(LibroNoEncontradoException ex) {
        log.warn("Libro no encontrado: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problema.setTitle("Libro no encontrado");
        return problema;
    }

    // 409: viola una regla de negocio (título duplicado)
    @ExceptionHandler(LibroDuplicadoException.class)
    public ProblemDetail manejarDuplicado(LibroDuplicadoException ex) {
        log.warn("Intento de crear libro duplicado: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problema.setTitle("Libro duplicado");
        return problema;
    }

    // 409 para TODAS las reglas de negocio que hereden de ConflictoNegocioException
    @ExceptionHandler(ConflictoNegocioException.class)
    public ProblemDetail manejarConflicto(ConflictoNegocioException ex) {
        log.warn("Regla de negocio incumplida: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problema.setTitle("Operación no permitida");
        return problema;
    }

    @ExceptionHandler(PrestamoNoEncontradoException.class)
    public ProblemDetail manejarPrestamoNoEncontrado(PrestamoNoEncontradoException ex) {
        log.warn("Préstamo no encontrado: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problema.setTitle("Préstamo no encontrado");
        return problema;
    }

    // 400: falla la validación de @Valid.
    // Este error ya lo gestiona la clase padre, así que SOBRESCRIBIMOS su método (@Override)
    // para añadir la lista de campos con error.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // LinkedHashMap: un mapa que respeta el orden de inserción
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos");
        problema.setTitle("Error de validación");
        problema.setProperty("errores", errores); // campo propio añadido al ProblemDetail

        return ResponseEntity.badRequest().body(problema);
    }

    // 500: cualquier error que no hayamos previsto.
    // Es la "red de seguridad": se aplica solo si ningún otro manejador es más específico.
    @ExceptionHandler(Exception.class)
    public ProblemDetail manejarErrorInesperado(Exception ex) {
        // Al log va TODO el detalle (con la excepción como último argumento: paso 2.6)...
        log.error("Error inesperado", ex);
        // ...pero al cliente, solo un mensaje genérico. Nunca detalles internos.
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno. Inténtalo más tarde.");
        problema.setTitle("Error interno del servidor");
        return problema;
    }

        // 401: credenciales incorrectas en el login, o token ausente, inválido o caducado
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail manejarNoAutenticado(AuthenticationException ex) {
        log.warn("Autenticación fallida: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Credenciales incorrectas, o token ausente, inválido o caducado");
        problema.setTitle("No autenticado");
        return problema;
    }

    // 403: autenticado, pero sin permiso (lo usaremos en el paso 6.5)
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail manejarAccesoDenegado(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta operación");
        problema.setTitle("Acceso denegado");
        return problema;
    }
}