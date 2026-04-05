package com.portfolio.repository;

import com.portfolio.entity.MockMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MockMemberRepository extends JpaRepository<MockMember, Long> {
}