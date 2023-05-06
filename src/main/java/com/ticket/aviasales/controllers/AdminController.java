package com.ticket.aviasales.controllers;

import com.ticket.aviasales.models.AirPlaneTicket;
import com.ticket.aviasales.models.TicketOrder;
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
    public String newTicket(@ModelAttribute("ticket") AirPlaneTicket airPlaneTicket) {
        return "admin/add";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("ticket") @Valid AirPlaneTicket airPlaneTicket, BindingResult bindingResult) {
        ticketValidator.validate(airPlaneTicket, bindingResult);

        if (bindingResult.hasErrors()) {
            return "admin/add";
        }

        ticketsService.saveTicket(airPlaneTicket);
        return "redirect:/";
    }

    @GetMapping()
    public String main(Model model) {
        List<AirPlaneTicket> airPlaneTicketList = ticketsService.findAllTicket();
        checkStatus(airPlaneTicketList);

        model.addAttribute("allTicket", airPlaneTicketList);
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
    public String update(@ModelAttribute("ticket") @Valid AirPlaneTicket airPlaneTicket, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "admin/update";
        }

        ticketsService.updateTicket(id, airPlaneTicket);
        return "redirect:/";
    }

    @GetMapping("/order")
    public String allOrder(Model model) {
        List<TicketOrder> ticketOrders = ordersService.findAllOrder();

        for (TicketOrder ticketOrder : ticketOrders) {
            if(ticketOrder.getAirPlaneTicket().getDepartureTime().compareTo(LocalDateTime.now()) > 0){
                ticketOrder.getAirPlaneTicket().setStatus("ACTIVE");
            } else {
                ticketOrder.getAirPlaneTicket().setStatus("OVERDUE");
            }
        }

        model.addAttribute("orders", ticketOrders);

        return "admin/order";
    }

    private void checkStatus(List<AirPlaneTicket> airPlaneTicketList){
        for (AirPlaneTicket airPlaneTicket : airPlaneTicketList) {
            if(airPlaneTicket.getDepartureTime().compareTo(LocalDateTime.now()) > 0){
                airPlaneTicket.setStatus("ACTIVE");
            } else {
                airPlaneTicket.setStatus("OVERDUE");
            }
        }
    }
}
