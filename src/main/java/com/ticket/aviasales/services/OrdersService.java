package com.ticket.aviasales.services;

import com.ticket.aviasales.models.TicketOrder;
import com.ticket.aviasales.models.Person;
import com.ticket.aviasales.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrdersService {
    private final OrdersRepository ordersRepository;

    @Autowired
    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Transactional
    public void saveOrder(TicketOrder ticketOrder){
        ordersRepository.save(ticketOrder);
    }

    public List<TicketOrder> findByPerson(Person person) {
        return ordersRepository.findByPerson(person);
    }

    public List<TicketOrder> findAllOrder() {
        return ordersRepository.findAll();
    }
}
