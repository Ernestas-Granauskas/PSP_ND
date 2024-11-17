package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameSessionController {

    // Serve the static index.html for the main page
    @GetMapping("/")
    public String getIndexPage() {
        return "index";  // Maps to src/main/resources/templates/index.html
    }

    // Serve the dynamic game page, passing the session code to the frontend
    @GetMapping("/game/{sessionCode}")
    public String getSessionPage(@PathVariable String sessionCode, Model model) {
        model.addAttribute("sessionCode", sessionCode);  // Pass session code to the frontend
        return "game";  // Maps to src/main/resources/templates/game.html
    }
}
