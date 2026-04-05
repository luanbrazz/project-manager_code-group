package com.portfolio.client;

import com.portfolio.dto.request.MemberCreateRequest;
import com.portfolio.dto.response.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "member-client", url = "${member-api.base-url}")
public interface MemberClient {

    @PostMapping("/mock/members")
    MemberResponse create(@RequestBody MemberCreateRequest request);

    @GetMapping("/mock/members/{id}")
    MemberResponse findById(@PathVariable("id") Long id);

    @GetMapping("/mock/members")
    List<MemberResponse> findAll();
}