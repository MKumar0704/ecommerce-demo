package com.ecom.userservice.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.stereotype.Indexed;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", indexes = @Index(name = "idx_username",columnList = "username"))
@EntityListeners(AuditingEntityListener.class)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;


    @Column(nullable = false,unique = true)
    private String username;

    @Column(nullable = false,unique = true)
    @Email(message = "Email is required")
    private String email;

    @Column(nullable = false)
    @Size(min = 8,message = "Password must be a minimum length of 8")
    private String password;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDate createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private List<UserAddress> address;

}
