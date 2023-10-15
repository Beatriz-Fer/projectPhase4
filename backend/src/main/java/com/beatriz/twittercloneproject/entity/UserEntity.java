package com.beatriz.twittercloneproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank(message = "First name is mandatory")
    @NotEmpty
    private String firstName;

    @NotNull
    @NotBlank(message = "Last name is mandatory")
    @NotEmpty
    private String lastName;

    @NotNull
    @NotBlank(message = "Username is mandatory")
    @NotEmpty
    @Column(unique = true)
    private String username;

    @Email
    @NotEmpty(message = "Email is mandatory")
    private String email;

    @NotNull
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_pic", columnDefinition = "BLOB")
    private String profilePic;

    private String bio;

    boolean isEmailUnique;

}
