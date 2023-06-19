package org.university.api.v1;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.university.controller.GlobalExceptionHandler;
import org.university.exceptions.InvalidTokenException;
import org.university.service.TemporaryUserService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class RegistrationControllerRestTest {

    private MockMvc mockMvc;

    @Mock
    private TemporaryUserService userServiceMock;

    private RegistrationControllerRest registrationController;

    @BeforeEach
    public void setUpBeforeClass() throws Exception {
        registrationController = new RegistrationControllerRest(userServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController)
                .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

    @Test
    void testVerifyUserWhenTokenIsValid() throws Exception {
        String token = "validToken";
        when(userServiceMock.verifyUser(token)).thenReturn(true);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/register/validToken")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(status().isOk());
        verify(userServiceMock, times(1)).verifyUser(token);
    }
    
    @Test
    void testVerifyUserWhenTokenEmpty() throws Exception {
        String token = " ";        
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/register/ ")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Secure token is empty!\""))                
                .andExpect(status().isBadRequest());
        verify(userServiceMock, never()).verifyUser(token);
    }
    
    @Test
    void testVerifyUserWhenTokenInvalid() throws Exception {
        String token = "invalid token";
        doThrow(new InvalidTokenException("Token is not valid")).when(userServiceMock).verifyUser(token);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1/register/invalid token")
                .contentType(MediaType.APPLICATION_JSON);
        ResultActions result = mockMvc.perform(request);
        result.andExpect(exception -> assertEquals(
                exception.getResolvedException().getMessage(), "400 BAD_REQUEST \"Token is not valid\""))                
                .andExpect(status().isBadRequest());
    }
}
