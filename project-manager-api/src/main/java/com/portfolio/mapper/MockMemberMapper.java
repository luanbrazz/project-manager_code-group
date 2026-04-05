package com.portfolio.mapper;

import com.portfolio.dto.request.MemberCreateRequest;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.entity.MockMember;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MockMemberMapper {

    private final ModelMapper modelMapper;

    public MemberResponse toResponse(MockMember member) {
        return modelMapper.map(member, MemberResponse.class);
    }

    public MockMember toEntity(MemberCreateRequest request) {
        return modelMapper.map(request, MockMember.class);
    }
}