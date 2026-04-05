package com.portfolio.controller;

import com.portfolio.dto.request.MemberCreateRequest;
import com.portfolio.dto.response.MemberResponse;
import com.portfolio.service.MockMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mock/members")
@RequiredArgsConstructor
@Tag(name = "API de Membros (Mock)", description = "Simula a API externa de membros")
public class MockMemberController {

    private final MockMemberService memberService;

    @PostMapping
    @Operation(summary = "Criar membro", description = "Cria um novo membro na API externa simulada.")
    public ResponseEntity<MemberResponse> create(@Valid @RequestBody MemberCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar membro por ID")
    public ResponseEntity<MemberResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos os membros")
    public ResponseEntity<List<MemberResponse>> findAll() {
        return ResponseEntity.ok(memberService.findAll());
    }
}