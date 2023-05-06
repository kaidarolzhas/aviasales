package com.ticket.aviasales.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Ticket")
public class Ticket {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Пункт отправления не должно быть пустым")
    @Size(min = 2, max = 100, message = "Пункт отправления должно быть от 2 до 100 символов длиной")
    @Column(name = "departurePoint")
    private String departurePoint;

    @NotEmpty(message = "Пункт прибытия не должно быть пустым")
    @Size(min = 2, max = 100, message = "Пункт прибытия должно быть от 2 до 100 символов длиной")
    @Column(name = "arrivalPoint")
    private String arrivalPoint;

    @NotNull
    @Future(message = "Дата должна быть в будущем")
    @Column(name = "departureTime")
    private LocalDateTime departureTime;

    @NotNull
    @Future(message = "Дата должна быть в будущем")
    @Column(name = "arrivalTime")
    private LocalDateTime arrivalTime;

    @Min(value = 1, message = "Количество мест должно быть больше 1")
    @Column(name = "count")
    private int count;

    @Min(value = 0, message = "Количество мест должно быть больше 0")
    @Column(name = "price")
    private int price;

    @Column(name = "status")
    private String status;

    private Long time;
}
