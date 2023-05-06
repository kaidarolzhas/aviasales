package com.ticket.aviasales.services;

import com.ticket.aviasales.models.Ticket;
import com.ticket.aviasales.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TicketsService {
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketsService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public void saveTicket(Ticket ticket) {
        ticketRepository.save(ticket);
    }

    public List<Ticket> findAllTicket() {
        return ticketRepository.findAll();
    }

    public List<Ticket> findByDeparturePointAndArrivalPoint(String departure, String arrival){
        return ticketRepository.findByDeparturePointAndArrivalPoint(departure, arrival);
    }

    public Ticket findOne(int id){
        Optional<Ticket> ticket = ticketRepository.findById(id);
        return ticket.orElse(null);
    }

    @Transactional
    public void deleteTicket(int id){
        ticketRepository.deleteById(id);
    }

    @Transactional
    public void updateTicket(int id, Ticket ticket){
        ticket.setId(id);
        ticketRepository.save(ticket);
    }
}
