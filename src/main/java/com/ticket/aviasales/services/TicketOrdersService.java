package com.ticket.aviasales.services;

import com.ticket.aviasales.models.TicketOrder;
import com.ticket.aviasales.models.Person;
import com.ticket.aviasales.repositories.TicketOrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TicketOrdersService {
    private final TicketOrdersRepository ticketOrdersRepository;

    @Autowired
    public TicketOrdersService(TicketOrdersRepository ticketOrdersRepository) {
        this.ticketOrdersRepository = ticketOrdersRepository;
    }

    @Transactional
    public void saveOrder(TicketOrder ticketOrder){
        ticketOrdersRepository.save(ticketOrder);
    }

    public List<TicketOrder> findByPerson(Person person) {
        return ticketOrdersRepository.findByPerson(person);
    }

    public List<TicketOrder> findAllOrder() {
        return ticketOrdersRepository.findAll();
    }
}
