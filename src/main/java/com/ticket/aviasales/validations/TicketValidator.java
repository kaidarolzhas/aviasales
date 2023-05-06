package com.ticket.aviasales.validations;

import com.ticket.aviasales.models.AirPlaneTicket;
import com.ticket.aviasales.services.AirPlaneTicketsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;

@Component
public class TicketValidator implements Validator {
    private final AirPlaneTicketsService ticketsService;

    @Autowired
    public TicketValidator(AirPlaneTicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return AirPlaneTicket.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AirPlaneTicket airPlaneTicket = (AirPlaneTicket) target;

        LocalDateTime arrivalTime = airPlaneTicket.getArrivalTime();
        LocalDateTime departureTime = airPlaneTicket.getDepartureTime();

        if (arrivalTime != null && departureTime != null && arrivalTime.isBefore(departureTime)) {
            errors.rejectValue("arrivalTime", "", "Arrival time cannot be before departure time");
        }
    }
}
