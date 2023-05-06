package com.ticket.aviasales.controllers;

import com.ticket.aviasales.models.Order;
import com.ticket.aviasales.models.Person;
import com.ticket.aviasales.models.Ticket;
import com.ticket.aviasales.services.OrdersService;
import com.ticket.aviasales.services.PeopleService;
import com.ticket.aviasales.services.TicketsService;
import jakarta.validation.Valid;
import org.apache.commons.codec.digest.DigestUtils;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final TicketsService ticketsService;
    private final OrdersService ordersService;

    @Autowired
    public MainController(PeopleService peopleService, TicketsService ticketsService, OrdersService ordersService) {
        this.peopleService = peopleService;
        this.ticketsService = ticketsService;
        this.ordersService = ordersService;
    }

    private void checkStatus(List<Ticket> ticketList){
        for (Ticket ticket: ticketList) {
            ticket.setTime(java.time.Duration.between(ticket.getDepartureTime(), ticket.getArrivalTime()).toHours());
            if(ticket.getDepartureTime().compareTo(LocalDateTime.now()) > 0){
                ticket.setStatus("ACTIVE");
            } else {
                ticket.setStatus("OVERDUE");
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
        List<Ticket> ticketList = ticketsService.findAllTicket();
        checkStatus(ticketList);

        m.addAttribute("tickets", ticketList);

        return "user/tickets.html";
    }

    @GetMapping("/search")
    public String search(Model m) {
        List<Ticket> ticketList = ticketsService.findAllTicket();
        checkStatus(ticketList);

        m.addAttribute("ticket", ticketList);

        return "user/search.html";
    }

    @PostMapping ("/search")
    public String searchValue(Model m, @RequestParam("search_value1") String search_value1, @RequestParam("search_value2") String search_value2) {

        List<Ticket> ticketList = ticketsService.findAllTicket();
        checkStatus(ticketList);

        if ((search_value1!=null && search_value2!=null) || (!Objects.equals(search_value1, "") && !Objects.equals(search_value2, "")) ) {

            List<Ticket> ticketList2 = ticketsService.findByDeparturePointAndArrivalPoint(search_value1, search_value2);
            checkStatus(ticketList2);
            m.addAttribute("ticket", ticketList2);
        }
        else{
            m.addAttribute("ticket", ticketList);
        }

        return "user/search.html";
    }

    @GetMapping(value = "/my_tickets")
    public String myBasket(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = peopleService.findByUsername(authentication.getName());

        List<Order> orders = ordersService.findByPerson(person);

        for (Order order: orders) {
            if(order.getTicket().getDepartureTime().compareTo(LocalDateTime.now()) > 0){
                order.getTicket().setStatus("ACTIVE");
            } else {
                order.getTicket().setStatus("OVERDUE");
            }
        }

        model.addAttribute("myOrders", orders);

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

        if(!file.isEmpty() && (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))) {
            try {
                String picName = DigestUtils.sha1Hex("img" + person.getUsername() + "ava");

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                person.setAvatarImg(picName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        peopleService.updatePerson(person.getId(), person);
        return "redirect:/profile";
    }

    @GetMapping("/tickets/order/{id}")
    public String order(Model model, @PathVariable("id") int id, @ModelAttribute("order") Order order) {
        model.addAttribute("ticket_id", ticketsService.findOne(id).getId());
        model.addAttribute("ticket_count", ticketsService.findOne(id).getCount());

        return "user/order";
    }

    @PostMapping(value = "/tickets/order")
    public String addOrder(@ModelAttribute("order") @Valid Order order, BindingResult bindingResult,
                           @RequestParam("count") int count,
                           @RequestParam("ticket_id") int id){
        if (bindingResult.hasErrors()) {
            return "user/order";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = peopleService.findByUsername(authentication.getName());

        order.setCount(count);
        order.setPerson(person);
        order.setTotalPrice(count * ticketsService.findOne(id).getPrice());
        order.setTicket(ticketsService.findOne(id));
        order.getTicket().setCount(ticketsService.findOne(id).getCount() - count);

        ordersService.saveOrder(order);

        return "redirect:/";
    }
}
