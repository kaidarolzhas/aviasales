package com.ticket.aviasales.util;

import com.ticket.aviasales.models.Ticket;
import com.ticket.aviasales.services.TicketsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class TicketValidator implements Validator {
    private final TicketsService ticketsService;

    @Autowired
    public TicketValidator(TicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Ticket.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Ticket ticket = (Ticket) target;

        LocalDateTime arrivalTime = ticket.getArrivalTime();
        LocalDateTime departureTime = ticket.getDepartureTime();

        if (arrivalTime != null && departureTime != null && arrivalTime.isBefore(departureTime)) {
            errors.rejectValue("arrivalTime", "", "Arrival time cannot be before departure time");
        }
    }
}
