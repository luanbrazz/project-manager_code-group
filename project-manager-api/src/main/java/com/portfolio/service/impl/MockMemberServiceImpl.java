package com.portfolio.service.impl;

import com.portfolio.dto.request.MemberCreateRequest;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.entity.MockMember;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.mapper.MockMemberMapper;
import com.portfolio.repository.MockMemberRepository;
import com.portfolio.service.MockMemberService;
import com.portfolio.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MockMemberServiceImpl implements MockMemberService {

    private final MockMemberRepository repository;
    private final MockMemberMapper mapper;

    @Override
    @Transactional
    public MemberResponse create(MemberCreateRequest request) {
        MockMember member = mapper.toEntity(request);
        MockMember saved = repository.save(member);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse findById(Long id) {
        MockMember member = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MessageUtil.get("error.member.notFound", id)));
        return mapper.toResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}