package com.portfolio.mapper;

import com.portfolio.dto.request.MemberCreateRequest;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.entity.MockMember;
import org.springframework.stereotype.Component;

@Component
public class MockMemberMapper {

    public MemberResponse toResponse(MockMember member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getRole()
        );
    }

    public MockMember toEntity(MemberCreateRequest request) {
        return MockMember.builder()
                .name(request.name())
                .role(request.role())
                .build();
    }
}