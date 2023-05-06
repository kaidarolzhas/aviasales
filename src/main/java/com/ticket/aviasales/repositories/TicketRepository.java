package com.ticket.aviasales.repositories;

import com.ticket.aviasales.models.AirPlaneTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<AirPlaneTicket, Integer> {
    List<AirPlaneTicket> findByDeparturePointAndArrivalPoint(String departure, String arrival);
}
