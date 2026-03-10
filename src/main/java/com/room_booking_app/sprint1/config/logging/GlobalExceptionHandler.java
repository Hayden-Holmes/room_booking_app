// package com.room_booking_app.sprint1.config.logging;

// import java.util.stream.Collectors;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.validation.BindException;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// import jakarta.servlet.http.HttpServletRequest;

// @ControllerAdvice
// public class GlobalExceptionHandler {

//     private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//     @ExceptionHandler(BindException.class)
//     public String handleBindException(BindException ex, HttpServletRequest req) {
//         String errors = ex.getBindingResult().getAllErrors().stream()
//                 .map(e -> e.getDefaultMessage())
//                 .collect(Collectors.joining("; "));
//         log.warn("BINDING ERROR {} {}: {}", req.getMethod(), req.getRequestURI(), errors);
//         return "error";
//     }

//     @ExceptionHandler(MethodArgumentTypeMismatchException.class)
//     public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
//         log.warn("TYPE MISMATCH {} {}: param={} value={} expectedType={}",
//                 req.getMethod(), req.getRequestURI(),
//                 ex.getName(), ex.getValue(), ex.getRequiredType());
//         return "error";
//     }

//     @ExceptionHandler(Exception.class)
//     public String handleAny(Exception ex, HttpServletRequest req) {
//         log.error("UNHANDLED {} {}: {}", req.getMethod(), req.getRequestURI(), ex.toString(), ex);
//         return "error";
//     }
// }