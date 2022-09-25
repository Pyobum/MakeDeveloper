package com.fastcampus.programming.spring.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fastcampus.programming.spring.dto.CreateDeveloper;
import com.fastcampus.programming.spring.dto.DeveloperDetailDto;
import com.fastcampus.programming.spring.dto.DeveloperDto;
import com.fastcampus.programming.spring.dto.EditDeveloper;
import com.fastcampus.programming.spring.dto.SpringErrorResponse;
import com.fastcampus.programming.spring.exception.SpringException;
import com.fastcampus.programming.spring.service.SpringService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * @author Snow
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SpringController {
    private final SpringService springService;

    // 개발자 전체 보기
    @GetMapping("/developer")
    public List<DeveloperDto> getAllDevelopers() {
        log.info("Get /developers HTTP/1.1");

        return springService.getAllEmployedDevelopers();
    }

    // 개발자 상세 보기
    @GetMapping("/developer/{memberId}")
    public DeveloperDetailDto getDeveloperDetail(@PathVariable final String memberId) {
        log.info("Get /developers HTTP/1.1");

        return springService.getDeveloperDetail(memberId);
    }

    // 개발자 생성
    @PostMapping("/create-developer")
    public CreateDeveloper.Response createDevelopers(@Valid @RequestBody final CreateDeveloper.Request request) {
        log.info("request : {}", request);

        return springService.createDeveloper(request);
    }

    // 개발자 삭제
    @PutMapping("/developer/{memberId}")
    public DeveloperDetailDto editDeveloper (@PathVariable final String memberId, @Valid @RequestBody final EditDeveloper.Request request) {
        log.info("Get /developers HTTP/1.1");

        return springService.editDeveloper(memberId, request);
    }

    @DeleteMapping("/developer/{memberId}")
    public DeveloperDetailDto deleteDeveloper(@PathVariable String memberId) {
        return springService.deleteDeveloper(memberId);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    @ExceptionHandler(SpringException.class)
    public SpringErrorResponse handleException(SpringException e, HttpServletRequest request) {
        log.error("errorCode: {}, url: {}, message: {}}", e.getSpringErrorCode(), request.getRequestURI(), e.getDetailMessage());

        return SpringErrorResponse.builder()
                    .errCode(e.getSpringErrorCode())
                    .errorMessage(e.getDetailMessage())
                    .build();
    }

}