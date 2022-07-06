package seongsic.corona.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import seongsic.corona.elastic.Connector;
import seongsic.corona.service.CoronaService;

@Controller
@RequestMapping("/")
public class HomeController {

    @RequestMapping("/")
    public String home() throws Exception {
        return "home";
    }

}
