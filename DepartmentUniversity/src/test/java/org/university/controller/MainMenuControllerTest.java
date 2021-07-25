package org.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class MainMenuControllerTest {

    private MockMvc mockMvc;
    
    private MainMenuController mainMenuController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        mainMenuController = new MainMenuController();
        mockMvc = MockMvcBuilders.standaloneSetup(mainMenuController).build();
    }

    @Test
    void testMainMenu() throws Exception {        
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/");
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("mainmenu"));
    }   
}
