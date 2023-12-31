package com.ticket.aviasales.services;

import com.ticket.aviasales.models.AirPlaneTicket;
import com.ticket.aviasales.repositories.AirPlaneTicketsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AirPlaneTicketsService {
    private final AirPlaneTicketsRepository ticketRepository;

    @Autowired
    public AirPlaneTicketsService(AirPlaneTicketsRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public void saveTicket(AirPlaneTicket airPlaneTicket) {
        ticketRepository.save(airPlaneTicket);
    }

    public List<AirPlaneTicket> findAllTicket() {
        return ticketRepository.findAll();
    }

    public List<AirPlaneTicket> findByDeparturePointAndArrivalPoint(String departure, String arrival){
        return ticketRepository.findByDeparturePointAndArrivalPoint(departure, arrival);
    }

    public AirPlaneTicket findOne(int id){
        Optional<AirPlaneTicket> ticket = ticketRepository.findById(id);
        return ticket.orElse(null);
    }

    @Transactional
    public void deleteTicket(int id){
        ticketRepository.deleteById(id);
    }

    @Transactional
    public void updateTicket(int id, AirPlaneTicket airPlaneTicket){
        airPlaneTicket.setId(id);
        ticketRepository.save(airPlaneTicket);
    }
}
