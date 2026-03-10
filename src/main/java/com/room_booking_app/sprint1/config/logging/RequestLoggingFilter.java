// package com.room_booking_app.sprint1.config.logging;

// import java.io.IOException;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// @Component
// public class RequestLoggingFilter extends OncePerRequestFilter {

//     private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

//     @Override
//     protected void doFilterInternal(HttpServletRequest request,
//                                     HttpServletResponse response,
//                                     FilterChain filterChain)
//             throws ServletException, IOException {

//         long start = System.currentTimeMillis();
//         String method = request.getMethod();
//         String path = request.getRequestURI();

//         try {
//             filterChain.doFilter(request, response);
//         } finally {
//             long ms = System.currentTimeMillis() - start;
//             int status = response.getStatus();
//             log.info("{} {} -> {} ({}ms)", method, path, status, ms);
//         }
//     }
// }