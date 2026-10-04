package com.sami9889.aiphonesupport.controller;

import com.sami9889.aiphonesupport.domain.Client;
import com.sami9889.aiphonesupport.dto.ClientRegistrationRequest;
import com.sami9889.aiphonesupport.service.PhoneNumberProvisioningService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    private final PhoneNumberProvisioningService phoneNumberProvisioningService;

    public WebController(PhoneNumberProvisioningService phoneNumberProvisioningService) {
        this.phoneNumberProvisioningService = phoneNumberProvisioningService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        if (!model.containsAttribute("client")) {
            model.addAttribute("client", new ClientRegistrationRequest(
                    "",
                    "",
                    "",
                    "US",
                    "",
                    ""
            ));
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String register(@Valid @ModelAttribute("client") ClientRegistrationRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        try {
            Client client = phoneNumberProvisioningService.registerClient(request);
            redirectAttributes.addAttribute("clientCode", client.getClientCode());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your phone support setup is ready. We will contact you shortly.");
            return "redirect:/signup/success";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("email", "error.client", ex.getMessage());
            return "signup";
        }
    }

    @GetMapping("/signup/success")
    public String signupSuccess(@RequestParam(value = "clientCode", required = false) String clientCode,
                                Model model) {
        model.addAttribute("clientCode", clientCode);
        return "signup-success";
    }
}
