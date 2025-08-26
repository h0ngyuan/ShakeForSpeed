package com.sfs.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomException {

    private static final Logger log = LoggerFactory.getLogger(CustomException.class);

    @ExceptionHandler(Exception.class)
    public void handler(Exception ex) {
        log.warn("Thread:"+Thread.currentThread()
                +" Time:"+LocalDateTime.now()
                +", An"+ex.getClass().getSimpleName()
                +"{"+ex.getMessage()+"}"
                +"has happen because "+ex.getCause());

    }
}
