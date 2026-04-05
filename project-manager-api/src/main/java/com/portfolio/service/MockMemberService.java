package com.portfolio.service;

import com.portfolio.dto.request.MemberCreateRequest;
import com.portfolio.dto.response.MemberResponse;

import java.util.List;

public interface MockMemberService {
    MemberResponse create(MemberCreateRequest request);

    MemberResponse findById(Long id);

    List<MemberResponse> findAll();
}