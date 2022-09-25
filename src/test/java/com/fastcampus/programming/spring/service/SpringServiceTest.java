package com.fastcampus.programming.spring.service;

import static com.fastcampus.programming.spring.constant.SpringConstant.MAX_JUNIOR_EXPERIENCE_YEARS;
import static com.fastcampus.programming.spring.constant.SpringConstant.MIN_SENIOR_EXPERIENCE_YEARS;
import static com.fastcampus.programming.spring.exception.SpringErrorCode.DUPLICATED_MEMBER_ID;
import static com.fastcampus.programming.spring.exception.SpringErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED;
import static com.fastcampus.programming.spring.type.DeveloperLevel.JUNGNIOR;
import static com.fastcampus.programming.spring.type.DeveloperLevel.JUNIOR;
import static com.fastcampus.programming.spring.type.DeveloperLevel.SENIOR;
import static com.fastcampus.programming.spring.type.DeveloperSkillType.FRONT_END;
import static com.fastcampus.programming.spring.type.DeveloperSkillType.FULL_STACK;
import static com.fastcampus.programming.spring.type.StatusCode.EMPLOYED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fastcampus.programming.spring.dto.CreateDeveloper;
import com.fastcampus.programming.spring.dto.DeveloperDetailDto;
import com.fastcampus.programming.spring.dto.DeveloperDto;
import com.fastcampus.programming.spring.entity.Developer;
import com.fastcampus.programming.spring.exception.SpringException;
import com.fastcampus.programming.spring.repository.DeveloperRepository;
import com.fastcampus.programming.spring.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.spring.type.DeveloperLevel;
import com.fastcampus.programming.spring.type.DeveloperSkillType;

@ExtendWith(MockitoExtension.class)
// @SpringBootTest
class SpringServiceTest {
    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private RetiredDeveloperRepository retiredDeveloperRepository;

    // @Autowired
    @InjectMocks
    private SpringService springService;

    private final Developer defaultDeveloper = Developer.builder()
            .developerLevel(SENIOR)
            .developerSkillType(FRONT_END)
            .experienceYears(12)
            .statusCode(EMPLOYED)
            .name("name")
            .age(12)
            .build();

    private CreateDeveloper.Request getCreateRequest(
        DeveloperLevel developerLevel,
        DeveloperSkillType developerSkillType,
        Integer experienceYears
    ) {
        return CreateDeveloper.Request.builder()
            .developerLevel(developerLevel)
            .developerSkillType(developerSkillType)
            .experienceYears(experienceYears)
            .memberId("memberId")
            .name("name")
            .age(12)
            .build();
    }
    @Test
    public void testSomething() {
        // given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

        // when
        DeveloperDetailDto developerDetail = springService.getDeveloperDetail("memberId");

        // then
        assertEquals(SENIOR, developerDetail.getDeveloperLevel());
        assertEquals(FRONT_END, developerDetail.getDeveloperSkillType());
        assertEquals(12, developerDetail.getExperienceYears());
    }

    @Test
    void createDeveloperTest_success() {
        //given
        given(developerRepository.findByMemberId(anyString()))
            .willReturn(Optional.empty());
        given(developerRepository.save(any()))
            .willReturn(defaultDeveloper);
        ArgumentCaptor<Developer> captor = ArgumentCaptor.forClass(Developer.class);

        //when
        springService.createDeveloper(getCreateRequest(SENIOR, FRONT_END, 12));

        // then
        verify(developerRepository, times(1))
                .save(captor.capture());
        Developer savedDeveloper = captor.getValue();
        assertEquals(SENIOR, savedDeveloper.getDeveloperLevel());
        assertEquals(FRONT_END, savedDeveloper.getDeveloperSkillType());
        assertEquals(12, savedDeveloper.getExperienceYears());
    }

    @Test
    void createDeveloperTest_fail_low_senior() {
        //given
        // given(developerRepository.findByMemberId(anyString()))
        //     .willReturn(Optional.empty());

        //when
        // then
        SpringException springException = assertThrows(SpringException.class, 
            () -> springService.createDeveloper(
                getCreateRequest(SENIOR, FRONT_END, MIN_SENIOR_EXPERIENCE_YEARS-1)
            )
        );

        
        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED, springException.getSpringErrorCode());
    }

    @Test
    void createDeveloperTest_failed_with_unmatched_level() {
        // given
        // when
        // then
        SpringException springException = assertThrows(SpringException.class,
            () -> springService.createDeveloper(
                getCreateRequest(JUNIOR, FRONT_END, MAX_JUNIOR_EXPERIENCE_YEARS + 1)
            )
        );
        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED, springException.getSpringErrorCode());

        springException = assertThrows(SpringException.class,
            () -> springService.createDeveloper(
                getCreateRequest(JUNGNIOR, FRONT_END, MIN_SENIOR_EXPERIENCE_YEARS + 1)
            )
        );

        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED, springException.getSpringErrorCode());

        springException = assertThrows(SpringException.class,
            () -> springService.createDeveloper(
                getCreateRequest(SENIOR, FRONT_END, MIN_SENIOR_EXPERIENCE_YEARS - 1)
            )
        );

        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED, springException.getSpringErrorCode());
    }
}
