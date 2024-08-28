package system.payments.poc.controller.advice;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import system.payments.poc.dto.ErrorDTO;
import system.payments.poc.exceptions.MerchantHasTransactionsException;
import system.payments.poc.exceptions.MerchantInactiveException;
import system.payments.poc.exceptions.MerchantNotFoundException;
import system.payments.poc.exceptions.TransactionNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler({MerchantHasTransactionsException.class, MerchantInactiveException.class,
            MerchantNotFoundException.class, TransactionNotFoundException.class, IllegalArgumentException.class})
    @ResponseBody
    public ResponseEntity<ErrorDTO> handleCustomException(Exception ex) {
        return new ResponseEntity<>(createErrorResponseBody(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                               HttpStatusCode status, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

        return new ResponseEntity<>(ErrorDTO.builder().timestamp(LocalDateTime.now()).errorMessage(errors).build(),
                HttpStatus.BAD_REQUEST);
    }

    private ErrorDTO createErrorResponseBody(String errorMessage) {
        log.error(errorMessage);
        return ErrorDTO.builder().timestamp(LocalDateTime.now()).errorMessage(new ArrayList<>() {{
            add(errorMessage);
        }}).build();
    }
}
