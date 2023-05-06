package com.ticket.aviasales.repositories;

import com.ticket.aviasales.models.TicketOrder;
import com.ticket.aviasales.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<TicketOrder, Integer> {
    List<TicketOrder> findByPerson (Person person);
}
