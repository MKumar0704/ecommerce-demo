package com.ecom.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "Username must valid")
    private String username;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email should not be empty")
    private String email;

    @Size(min = 8, message = "password must contain 8 characters")
    private String password;


}
