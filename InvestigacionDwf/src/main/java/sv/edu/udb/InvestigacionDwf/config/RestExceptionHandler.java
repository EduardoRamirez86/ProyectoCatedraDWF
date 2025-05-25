package sv.edu.udb.InvestigacionDwf.config;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;
import sv.edu.udb.InvestigacionDwf.config.web.ApiError;
import sv.edu.udb.InvestigacionDwf.config.web.ApiErrorWrapper;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

/**
 * Manejador global de excepciones para la API REST.
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        ApiErrorWrapper apiErrors = processErrors(ex.getBindingResult().getAllErrors());
        return handleExceptionInternal(ex, apiErrors, headers, status, request);
    }

    @ExceptionHandler(value = {ValidationException.class})
    protected final ResponseEntity<Object> handleValidation(
            final ValidationException ex,
            final WebRequest request) {
        ApiErrorWrapper apiErrors = message(HttpStatus.BAD_REQUEST, ex);
        return handleExceptionInternal(ex, apiErrors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({AccessDeniedException.class})
    protected ResponseEntity<Object> handleAccessDenied(
            final AccessDeniedException ex,
            final WebRequest request) {
        ApiErrorWrapper apiErrors = message(HttpStatus.FORBIDDEN, ex);
        return handleExceptionInternal(ex, apiErrors, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    /**
     * Maneja excepciones cuando no se encuentra una entidad (404 - NOT FOUND).
     */
    @ExceptionHandler({EntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFound(
            final RuntimeException ex,
            final WebRequest request) {
        ApiErrorWrapper apiErrors = message(HttpStatus.NOT_FOUND, ex);
        return handleExceptionInternal(ex, apiErrors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Maneja errores relacionados con el acceso a datos (409 - CONFLICT).
     */
    @ExceptionHandler({DataAccessException.class})
    protected ResponseEntity<Object> handleDataAccess(
            final DataAccessException ex,
            final WebRequest request) {
        ApiErrorWrapper apiErrors = message(HttpStatus.CONFLICT, ex);
        return handleExceptionInternal(ex, apiErrors, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    /**
     * Maneja argumentos inválidos (400 - BAD REQUEST).
     */
    @ExceptionHandler({IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgument(
            final IllegalArgumentException ex,
            final WebRequest request) {
        ApiErrorWrapper apiErrors = message(HttpStatus.BAD_REQUEST, ex);
        return handleExceptionInternal(ex, apiErrors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Captura cualquier excepción no manejada (500 - INTERNAL SERVER ERROR).
     */
    @ExceptionHandler({Exception.class})
    protected ResponseEntity<Object> handleAllExceptions(
            final Exception ex,
            final WebRequest request) {
        ApiErrorWrapper apiErrors = message(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        return handleExceptionInternal(ex, apiErrors, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        Object responseBody = body;
        if (Objects.isNull(responseBody)) {
            responseBody = message((HttpStatus) status, ex);
        }

        return new ResponseEntity<>(responseBody, headers, status);
    }

    /**
     * Construye un objeto ApiError desde una excepción y un estado HTTP.
     */
    private ApiError buildApiError(final HttpStatus httpStatus, final Exception ex) {
        String typeException = ex.getClass().getSimpleName();
        String description = StringUtils.defaultIfBlank(ex.getLocalizedMessage(), ex.getMessage());

        String source = "base";
        if (ex instanceof MissingServletRequestParameterException paramEx) {
            source = paramEx.getParameterName();
        } else if (ex instanceof MissingPathVariableException pathEx) {
            source = pathEx.getVariableName();
        }

        return ApiError.builder()
                .status(httpStatus.value())
                .type(typeException)
                .title(httpStatus.getReasonPhrase())
                .description(description)
                .source(source)
                .build();
    }

    /**
     * Crea un ApiErrorWrapper desde un ApiError individual.
     */
    private ApiErrorWrapper message(final ApiError error) {
        ApiErrorWrapper errors = new ApiErrorWrapper();
        errors.addApiError(error); // Debe estar implementado en ApiErrorWrapper
        return errors;
    }

    /**
     * Crea un ApiErrorWrapper desde un estado HTTP y una excepción.
     */
    private ApiErrorWrapper message(final HttpStatus httpStatus, final Exception ex) {
        return message(buildApiError(httpStatus, ex));
    }

    /**
     * Procesa los errores de validación y los agrupa en un ApiErrorWrapper.
     */
    private ApiErrorWrapper processErrors(final List<ObjectError> errors) {
        ApiErrorWrapper dto = new ApiErrorWrapper();

        for (ObjectError objError : errors) {
            if (objError instanceof FieldError fieldError) {
                dto.addFieldError(
                        fieldError.getClass().getSimpleName(),
                        "Invalid Attribute",
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                );
            } else {
                dto.addFieldError(
                        objError.getClass().getSimpleName(),
                        "Invalid Attribute",
                        "base",
                        objError.getDefaultMessage()
                );
            }
        }

        return dto;
    }
}