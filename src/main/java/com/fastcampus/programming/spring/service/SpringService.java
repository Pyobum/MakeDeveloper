package com.fastcampus.programming.spring.service;

import static com.fastcampus.programming.spring.constant.SpringConstant.MAX_JUNIOR_EXPERIENCE_YEARS;
import static com.fastcampus.programming.spring.constant.SpringConstant.MIN_SENIOR_EXPERIENCE_YEARS;
import static com.fastcampus.programming.spring.exception.SpringErrorCode.NO_DEVELOPER;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;
import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fastcampus.programming.spring.dto.CreateDeveloper;
import com.fastcampus.programming.spring.dto.DeveloperDetailDto;
import com.fastcampus.programming.spring.dto.DeveloperDto;
import com.fastcampus.programming.spring.dto.DeveloperValitdaionDto;
import com.fastcampus.programming.spring.dto.EditDeveloper;
import com.fastcampus.programming.spring.entity.Developer;
import com.fastcampus.programming.spring.entity.RetiredDeveloper;
import com.fastcampus.programming.spring.exception.SpringErrorCode;
import com.fastcampus.programming.spring.exception.SpringException;
import com.fastcampus.programming.spring.repository.DeveloperRepository;
import com.fastcampus.programming.spring.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.spring.type.DeveloperLevel;
import com.fastcampus.programming.spring.type.DeveloperSkillType;
import com.fastcampus.programming.spring.type.StatusCode;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpringService {
    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;

    @Transactional
    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request) {
        validateCreateDeveloperRequest(request);

        // business logic start
        return CreateDeveloper.Response.fromEntity(
            developerRepository.save(
                createDeveloperFromRequest(request)
            )
        );
    }

    private Developer createDeveloperFromRequest(CreateDeveloper.Request request) {
        return Developer.builder()
            .developerLevel(request.getDeveloperLevel())
            .developerSkillType(request.getDeveloperSkillType())
            .experienceYears(request.getExperienceYears())
            .memberId(request.getMemberId())
            .statusCode(StatusCode.EMPLOYED)
            .name(request.getName())
            .age(request.getAge())
            .build(); 
    }

    private void validateCreateDeveloperRequest(@NonNull CreateDeveloper.Request request) {
        DeveloperValitdaionDto developerValitdaionDto = null;

        // business validation
        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYears());

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new SpringException(SpringErrorCode.DUPLICATED_MEMBER_ID);
                }));
    }

    @Transactional(readOnly = true)
    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return DeveloperDetailDto.fromEntity(getDeveloperByMemberId(memberId));
    }

    private Developer getDeveloperByMemberId(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new SpringException(NO_DEVELOPER));
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {
        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYears());

        return DeveloperDetailDto.fromEntity(
            getUpdatedDeveloperFromRequest(request, getDeveloperByMemberId(memberId))
        );
    }

    private Developer getUpdatedDeveloperFromRequest(EditDeveloper.Request request, Developer developer) {
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return developer;
    }

    private void validateDeveloperRequest(EditDeveloper.Request request, String memberId) {
        validateDeveloperLevel(request.getDeveloperLevel(), request.getExperienceYears());
    }

    private void validateDeveloperLevel(DeveloperLevel developerLevel, Integer experienceYears) {
        if(experienceYears < developerLevel.getMinExperienceYears()
                || experienceYears > developerLevel.getMaxExperienceYears()) {
            throw new SpringException(SpringErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // 1. EMPLOYED -> RETIRED
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new SpringException(NO_DEVELOPER));
        developer.setStatusCode(StatusCode.RETIRED);

        if(developer != null) throw new SpringException(NO_DEVELOPER);

        // 2. save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .age(developer.getAge())
                .build();
        
        retiredDeveloperRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);
    }
}