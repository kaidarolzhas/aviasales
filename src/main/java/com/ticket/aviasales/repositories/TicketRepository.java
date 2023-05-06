package com.ticket.aviasales.repositories;

import com.ticket.aviasales.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByDeparturePointAndArrivalPoint(String departure, String arrival);
}
