package com.ticket.aviasales.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Ticket")
public class AirPlaneTicket {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "AirLine must not to be empty")
    @Size(min = 2, max = 100, message = "Airline must be from 2 to 100 characters long")
    @Column(name = "airLine")
    private String airLine;


    @NotEmpty(message = "Type must not to be empty")
    @Size(min = 2, max = 100, message = "Type must be from 2 to 100 characters long")
    @Column(name = "type")
    private String type;

    @NotEmpty(message = "The point of departure should not be empty")
    @Size(min = 2, max = 100, message = "The point of departure must be from 2 to 100 characters long")
    @Column(name = "departurePoint")
    private String departurePoint;

    @NotEmpty(message = "The arrival point should not be empty")
    @Size(min = 2, max = 100, message = "The point of arrival must be from 2 to 100 characters long")
    @Column(name = "arrivalPoint")
    private String arrivalPoint;

    @NotNull
    @Future(message = "The date should be in the future")
    @Column(name = "departureTime")
    private LocalDateTime departureTime;

    @NotNull
    @Future(message = "The date should be in the future")
    @Column(name = "arrivalTime")
    private LocalDateTime arrivalTime;

    @Min(value = 1, message = "The number of seats must be more than 1")
    @Column(name = "count")
    private int count;

    @Min(value = 0, message = "The number of seats must be more than 0")
    @Column(name = "price")
    private int price;

    @Column(name = "status")
    private String status;

    private Long time;

    @OneToMany(mappedBy = "airPlaneTicket", cascade = CascadeType.ALL)
    private List<TicketOrder> orders;
}
