package com.proyecto.tienda.controller;

import com.proyecto.tienda.exception.RecursoNoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ModelAndView noEncontrado(RecursoNoEncontradoException e) {
        ModelAndView view = new ModelAndView("error/404");
        view.setStatus(HttpStatus.NOT_FOUND);
        view.addObject("mensajeError", e.getMessage());
        return view;
    }

    @ExceptionHandler({ConstraintViolationException.class, TypeMismatchException.class, BindException.class,
            MissingServletRequestParameterException.class, HttpMessageNotReadableException.class})
    public ModelAndView datosInvalidos() {
        return pagina(HttpStatus.BAD_REQUEST, "Revisa los datos", "La solicitud contiene datos inválidos. Vuelve al formulario y comprueba los campos.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView conflicto() {
        return pagina(HttpStatus.CONFLICT, "No se pudo completar la operación", "El registro está duplicado o tiene otros datos relacionados. Revisa la información antes de volver a intentarlo.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView sinPermiso() {
        ModelAndView view = new ModelAndView("error/403");
        view.setStatus(HttpStatus.FORBIDDEN);
        return view;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView inesperado(Exception e, HttpServletRequest request) {
        if (e instanceof ErrorResponse error && error.getStatusCode().value() < 500) {
            int code = error.getStatusCode().value();
            if (code == 404) return noEncontrado(new RecursoNoEncontradoException("La página solicitada no existe."));
            return pagina(HttpStatus.valueOf(code), "No pudimos procesar la solicitud",
                    "El enlace o los datos enviados no son válidos para esta operación. Vuelve atrás y revisa la información.");
        }
        log.error("Error inesperado en {} {}", request.getMethod(), request.getRequestURI(), e);
        return pagina(HttpStatus.INTERNAL_SERVER_ERROR, "Algo salió mal",
                "No pudimos completar la operación. Inténtalo de nuevo en unos momentos.");
    }

    private ModelAndView pagina(HttpStatus status, String titulo, String mensaje) {
        ModelAndView view = new ModelAndView("error/general");
        view.setStatus(status);
        view.addObject("codigoError", status.value());
        view.addObject("tituloError", titulo);
        view.addObject("mensajeError", mensaje);
        return view;
    }
}
