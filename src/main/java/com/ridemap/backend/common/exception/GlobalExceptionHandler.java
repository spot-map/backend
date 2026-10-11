package com.ridemap.backend.common.exception;

import java.net.URI;
import java.util.List;
import java.util.Map;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice 
public class GlobalExceptionHandler {

@ExceptionHandler(MethodArgumentNotValidException.class)
public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
    List<Map<String, String>> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> Map.of(
            "field", error.getField(),
            "message", error.getDefaultMessage() != null
                ? error.getDefaultMessage()
                : "Некорректное значение"
        ))
        .toList();

    ProblemDetail problem = createProblem(
        HttpStatus.BAD_REQUEST,
        "Ошибка валидации",
        "Переданные данные не прошли проверку."
    );

    problem.setProperty("errors", errors);

    return problem;
}

@ExceptionHandler(ConstraintViolationException.class)
public ProblemDetail handleConstraintViolation( ConstraintViolationException ex) {
    List<Map<String, String>> errors = ex.getConstraintViolations()
        .stream()
        .map(this::toValidationError)
        .toList();

    ProblemDetail problem = createProblem(
        HttpStatus.BAD_REQUEST,
        "Ошибка валидации",
        "Один или несколько параметров запроса некорректны."
    );

    problem.setProperty("errors", errors);

    return problem;
}

@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    ProblemDetail problem = createProblem(
        HttpStatus.BAD_REQUEST,
        "Некорректный параметр",
        "Параметр запроса имеет недопустимый формат."
    );

    problem.setProperty("parameter", ex.getName());

    return problem;
}

@ExceptionHandler(HttpMessageNotReadableException.class)
public ProblemDetail handleUnreadableMessage(HttpMessageNotReadableException ex) {
    return createProblem(
        HttpStatus.BAD_REQUEST,
        "Некорректное тело запроса",
        "Не удалось прочитать тело запроса. Проверьте формат JSON."
    );
}

private Map<String, String> toValidationError(ConstraintViolation<?> violation) {
    return Map.of(
        "field", violation.getPropertyPath().toString(),
        "message", violation.getMessage()
    );
}

private ProblemDetail createProblem(HttpStatus status,String title, String detail) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        status,
        detail
    );

    problem.setTitle(title);
    problem.setType(URI.create(
        "https://api.ridemap.local/problems/" + status.value()
    ));

    return problem;
}
}
