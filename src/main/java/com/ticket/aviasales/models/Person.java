package com.ticket.aviasales.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Person")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов длиной")
    @Column(name = "firstname")
    private String firstname;

    @NotEmpty(message = "Фамилия не должно быть пустым")
    @Size(min = 2, max = 100, message = "Фамилия должно быть от 2 до 100 символов длиной")
    @Column(name = "lastname")
    private String lastname;

    @NotEmpty(message = "Логин не должно быть пустым")
    @Size(min = 2, max = 100, message = "Логин должно быть от 2 до 100 символов длиной")
    @Column(name = "username")
    private String username;

    @NotEmpty(message = "Пароль не должно быть пустым")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    private String avatarImg;
}
