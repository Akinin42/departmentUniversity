package org.university.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.university.exceptions.InvalidTokenException;
import org.university.service.TemporaryUserService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class RegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TemporaryUserService userServiceMock;

    private RegistrationController registrationController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        registrationController = new RegistrationController(userServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void testVerifyUserWhenTokenIsValid() throws Exception {
        String token = "validToken";
        when(userServiceMock.verifyUser(token)).thenReturn(true);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/register").param("token", token);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/login"));
        verify(userServiceMock, times(1)).verifyUser(token);
    }
    
    @Test
    void testVerifyUserWhenTokenEmpty() throws Exception {
        String token = "";        
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/register").param("token", token);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/login"));
        verify(userServiceMock, never()).verifyUser(token);
    }
    
    @Test
    void testVerifyUserWhenTokenInvalid() throws Exception {
        String token = "invalid token";
        doThrow(new InvalidTokenException("Token is not valid")).when(userServiceMock).verifyUser(token);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/register").param("token", token);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(MockMvcResultMatchers.view().name("redirect:/login"));        
    }
}
