package com.ticket.aviasales.repositories;

import com.ticket.aviasales.models.Order;
import com.ticket.aviasales.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Integer> {
    List<Order> findByPerson (Person person);
}
