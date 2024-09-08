package system.payments.poc.controller.advice;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import system.payments.poc.dto.ErrorDTO;
import system.payments.poc.exceptions.MerchantHasTransactionsException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler({MerchantHasTransactionsException.class, ConstraintViolationException.class})
    @ResponseBody
    public ResponseEntity<ErrorDTO> handleCustomException(Exception ex) {
        return new ResponseEntity<>(createErrorResponseBody(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AuthorizationDeniedException.class, LockedException.class})
    @ResponseBody
    public ResponseEntity<ErrorDTO> handleAuthException(Exception ex) {
        log.error("Authorization exception", ex);
        return new ResponseEntity<>(createErrorResponseBody("Access denied for this user"), HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        Object[] detailMessageArguments = ex.getDetailMessageArguments();
        if (Objects.nonNull(detailMessageArguments) && detailMessageArguments.length > 0) {
            return new ResponseEntity<>(createErrorResponseBody((String) detailMessageArguments[0]), HttpStatus.BAD_REQUEST);
        }

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

        return new ResponseEntity<>(ErrorDTO.builder().timestamp(LocalDateTime.now()).errorMessage(errors).build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ErrorDTO> handleGlobalException(RuntimeException ex) {
        log.error("General exception", ex);
        return new ResponseEntity<>(createErrorResponseBody("Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDTO createErrorResponseBody(String errorMessage) {
        log.error(errorMessage);
        return ErrorDTO.builder().timestamp(LocalDateTime.now()).errorMessage(new ArrayList<>() {{
            add(errorMessage);
        }}).build();
    }
}
