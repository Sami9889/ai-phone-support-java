package com.sami9889.aiphonesupport.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class WebsiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void landingPageRenders() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(content().string(containsString("AI phone support")));
    }

    @Test
    void signupPageAcceptsRegistration() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("companyName", "Acme Corp")
                        .param("contactName", "Jane Doe")
                        .param("email", "jane@acmecorp.com")
                        .param("countryCode", "US")
                        .param("useCase", "Customer support")
                        .param("phoneNumber", "+14155550123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/signup/success*"));
    }
}
