package com.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mock_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String role;
}