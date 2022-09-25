package com.fastcampus.programming.spring.dto;

import com.fastcampus.programming.spring.exception.SpringErrorCode;
import com.fastcampus.programming.spring.type.DeveloperSkillType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeveloperValitdaionDto {
    private SpringErrorCode errorCode;
    private String memberId;
}
