package com.portfolio.client;

import com.portfolio.dto.response.MemberResponse;
import com.portfolio.exception.ResourceNotFoundException;
import com.portfolio.util.MessageUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberClientService {

    private final MemberClient memberClient;

    private final Map<Long, MemberResponse> memberCache = new ConcurrentHashMap<>();

    @CircuitBreaker(name = "member-api", fallbackMethod = "findByIdFallback")
    @Retry(name = "member-api")
    public MemberResponse findById(Long id) {
        log.debug("Chamando API de membros: findById({})", id);
        MemberResponse response = memberClient.findById(id);
        memberCache.put(id, response);
        return response;
    }

    @CircuitBreaker(name = "member-api", fallbackMethod = "findAllFallback")
    @Retry(name = "member-api")
    public List<MemberResponse> findAll() {
        log.debug("Chamando API de membros: findAll()");
        List<MemberResponse> response = memberClient.findAll();
        response.forEach(m -> memberCache.put(m.id(), m));
        return response;
    }

    private MemberResponse findByIdFallback(Long id, Throwable ex) {
        log.warn("Circuit Breaker/Retry esgotado para findById({}). Causa: {}. Consultando cache...",
                id, ex.getClass().getSimpleName());

        MemberResponse cached = memberCache.get(id);
        if (cached != null) {
            log.info("Membro id={} retornado do cache de fallback", id);
            return cached;
        }


        log.warn("Cache vazio para membro id={}. Lançando exceção de fallback.", id);
        throw new ResourceNotFoundException(
                MessageUtil.get("error.member.api.fallback", id)
        );
    }

    private List<MemberResponse> findAllFallback(Throwable ex) {
        log.warn("Circuit Breaker/Retry esgotado para findAll(). Causa: {}. Retornando cache.",
                ex.getClass().getSimpleName());

        if (!memberCache.isEmpty()) {
            log.info("Retornando {} membros do cache de fallback", memberCache.size());
            return new ArrayList<>(memberCache.values());
        }

        log.warn("Cache de membros vazio. Retornando lista vazia.");
        return List.of();
    }
}