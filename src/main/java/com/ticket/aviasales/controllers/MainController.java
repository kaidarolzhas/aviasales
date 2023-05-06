package com.ticket.aviasales.controllers;

import com.ticket.aviasales.models.AirPlaneTicket;
import com.ticket.aviasales.models.TicketOrder;
import com.ticket.aviasales.models.Person;
import com.ticket.aviasales.services.TicketOrdersService;
import com.ticket.aviasales.services.PeopleService;
import com.ticket.aviasales.services.AirPlaneTicketsService;
import jakarta.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
public class MainController {

    @Value("${file.img.viewPath}")
    private String viewPath;

    @Value("${file.img.defaultPicture}")
    private String defaultPicture;

    @Value("${file.img.uploadPath}")
    private String uploadPath;

    private final PeopleService peopleService;
    private final AirPlaneTicketsService ticketsService;
    private final TicketOrdersService ordersService;

    @Autowired
    public MainController(PeopleService peopleService, AirPlaneTicketsService ticketsService, TicketOrdersService ordersService) {
        this.peopleService = peopleService;
        this.ticketsService = ticketsService;
        this.ordersService = ordersService;
    }

    private void checkStatus(List<AirPlaneTicket> airPlaneTicketList){
        for (AirPlaneTicket airPlaneTicket : airPlaneTicketList) {
            airPlaneTicket.setTime(java.time.Duration.between(airPlaneTicket.getDepartureTime(), airPlaneTicket.getArrivalTime()).toHours());
            if(airPlaneTicket.getDepartureTime().compareTo(LocalDateTime.now()) > 0){
                airPlaneTicket.setStatus("ACTIVE");
            } else {
                airPlaneTicket.setStatus("OVERDUE");
            }
        }
    }

    @GetMapping("/")
    public String about() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = peopleService.findByUsername(authentication.getName());

        if (person != null && person.getRole().equals("ROLE_ADMIN")) {
            return "redirect:/admin";
        }

        return "user/about.html";
    }

    @GetMapping("/tickets")
    public String tickets(Model m) {
        List<AirPlaneTicket> airPlaneTicketList = ticketsService.findAllTicket();
        checkStatus(airPlaneTicketList);

        m.addAttribute("tickets", airPlaneTicketList);

        return "user/tickets.html";
    }

    @GetMapping("/search")
    public String search(Model m) {
        List<AirPlaneTicket> airPlaneTicketList = ticketsService.findAllTicket();
        checkStatus(airPlaneTicketList);

        m.addAttribute("ticket", airPlaneTicketList);

        return "user/search.html";
    }

    @PostMapping ("/search")
    public String searchValue(Model m, @RequestParam("search_value1") String search_value1, @RequestParam("search_value2") String search_value2) {

        List<AirPlaneTicket> airPlaneTicketList = ticketsService.findAllTicket();
        checkStatus(airPlaneTicketList);

        if ((search_value1!=null && search_value2!=null) || (!Objects.equals(search_value1, "") && !Objects.equals(search_value2, "")) ) {

            List<AirPlaneTicket> airPlaneTicketList2 = ticketsService.findByDeparturePointAndArrivalPoint(search_value1, search_value2);
            checkStatus(airPlaneTicketList2);
            m.addAttribute("ticket", airPlaneTicketList2);
        }
        else{
            m.addAttribute("ticket", airPlaneTicketList);
        }

        return "user/search.html";
    }

    @GetMapping(value = "/my_tickets")
    public String myBasket(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = peopleService.findByUsername(authentication.getName());

        List<TicketOrder> ticketOrders = ordersService.findByPerson(person);

        for (TicketOrder ticketOrder : ticketOrders) {
            if(ticketOrder.getAirPlaneTicket().getDepartureTime().compareTo(LocalDateTime.now()) > 0){
                ticketOrder.getAirPlaneTicket().setStatus("ACTIVE");
            } else {
                ticketOrder.getAirPlaneTicket().setStatus("OVERDUE");
            }
        }

        model.addAttribute("myOrders", ticketOrders);

        return "user/myOrders";
    }

    @GetMapping(value = "/view/{url}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody byte[] viewPicture(@PathVariable(name = "url") String url) throws IOException {
        String pictureURL = viewPath + defaultPicture;

        if(url != null && !url.equals("null")) {
            pictureURL = viewPath + url + ".jpg";
        }
        InputStream in;

        try {
            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath + defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }
        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/profile")
    public String myProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Person person = peopleService.findByUsername(authentication.getName());
        model.addAttribute("person", person);

        return "user/profile";
    }

    @GetMapping(value = "/profile/edit")
    public String profileEdit(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Person person = peopleService.findByUsername(authentication.getName());
        model.addAttribute("person", person);

        return "user/profileEdit";
    }

    @PatchMapping("/profile")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @RequestParam(name = "file") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            return "user/profileEdit";
        }

        peopleService.updatePerson(person.getId(), person);
        return "redirect:/profile";
    }

    @GetMapping("/tickets/order/{id}")
    public String order(Model model, @PathVariable("id") int id, @ModelAttribute("order") TicketOrder ticketOrder) {
        model.addAttribute("ticket_id", ticketsService.findOne(id).getId());
        model.addAttribute("ticket_count", ticketsService.findOne(id).getCount());

        return "user/order";
    }

    @PostMapping(value = "/tickets/order")
    public String addOrder(@ModelAttribute("order") @Valid TicketOrder ticketOrder, BindingResult bindingResult,
                           @RequestParam("count") int count,
                           @RequestParam("ticket_id") int id){
        if (bindingResult.hasErrors()) {
            return "user/order";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = peopleService.findByUsername(authentication.getName());

        ticketOrder.setCount(count);
        ticketOrder.setPerson(person);
        ticketOrder.setTotalPrice(count * ticketsService.findOne(id).getPrice());
        ticketOrder.setAirPlaneTicket(ticketsService.findOne(id));
        ticketOrder.getAirPlaneTicket().setCount(ticketsService.findOne(id).getCount() - count);

        ordersService.saveOrder(ticketOrder);

        return "redirect:/";
    }
}
