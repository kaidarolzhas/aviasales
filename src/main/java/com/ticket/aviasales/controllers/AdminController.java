package com.ticket.aviasales.controllers;

import com.ticket.aviasales.models.Order;
import com.ticket.aviasales.models.Ticket;
import com.ticket.aviasales.services.OrdersService;
import com.ticket.aviasales.services.TicketsService;
import com.ticket.aviasales.util.TicketValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final TicketsService ticketsService;
    private final OrdersService ordersService;
    private final TicketValidator ticketValidator;

    @Autowired
    public AdminController(TicketsService ticketsService, OrdersService ordersService, TicketValidator ticketValidator) {
        this.ticketsService = ticketsService;
        this.ordersService = ordersService;
        this.ticketValidator = ticketValidator;
    }

    @GetMapping("/add")
    public String newTicket(@ModelAttribute("ticket") Ticket ticket) {
        return "admin/add";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("ticket") @Valid Ticket ticket, BindingResult bindingResult) {
        ticketValidator.validate(ticket, bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/add";
        }

        ticketsService.saveTicket(ticket);
        return "redirect:/";
    }

    @GetMapping()
    public String main(Model model) {
        List<Ticket> ticketList = ticketsService.findAllTicket();
        checkStatus(ticketList);

        model.addAttribute("allTicket", ticketList);
        return "admin/index";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        ticketsService.deleteTicket(id);
        return "redirect:/";
    }

    @GetMapping("/update/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("ticket", ticketsService.findOne(id));

        return "admin/update";
    }

    @PatchMapping("/update/{id}")
    public String update(@ModelAttribute("ticket") @Valid Ticket ticket, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "admin/update";
        }

        ticketsService.updateTicket(id, ticket);
        return "redirect:/";
    }

    @GetMapping("/order")
    public String allOrder(Model model) {
        List<Order> orders = ordersService.findAllOrder();

        for (Order order: orders) {
            if(order.getTicket().getDepartureTime().compareTo(LocalDateTime.now()) > 0){
                order.getTicket().setStatus("ACTIVE");
            } else {
                order.getTicket().setStatus("OVERDUE");
            }
        }

        model.addAttribute("orders", orders);

        return "admin/order";
    }

    private void checkStatus(List<Ticket> ticketList){
        for (Ticket ticket: ticketList) {
            if(ticket.getDepartureTime().compareTo(LocalDateTime.now()) > 0){
                ticket.setStatus("ACTIVE");
            } else {
                ticket.setStatus("OVERDUE");
            }
        }
    }
}
