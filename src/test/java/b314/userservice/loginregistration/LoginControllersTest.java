package b314.userservice.loginregistration;

import b314.userservice.TestUtils;
import b314.userservice.controller.Message;
import b314.userservice.dto.login.LoginDTO;
import b314.userservice.repository.UserRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class LoginControllersTest {

    @Component
    @Data
    @NoArgsConstructor
    private static class LoginResponse {

        private String email;

        private String token;

    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LoginRegistrationTestEntityHolder holder;

    @MockBean
    private UserRepository userRepository;

    @Test
    @SneakyThrows
    void successfulLoginTest() {
        // Enable test user manually
        holder.getUser().setEnabled(true);

        // Mocking findByEmail method of UserRepository for user1@gmail.com
        Mockito.doReturn(Optional.of(holder.getUser())).when(userRepository).findByEmail("user1@gmail.com");

        // Creating login dto
        LoginDTO loginDTO = LoginDTO.builder()
                                    .email("user1@gmail.com")
                                    .password("123")
                                    .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/login")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(loginDTO)))
                              .andExpect(status().isOk())
                              .andReturn();

        // Getting response
        LoginResponse temp = (LoginResponse) TestUtils.jsonToClass(result.getResponse().getContentAsString(), LoginResponse.class);

        // Asserting
        assertEquals("user1@gmail.com", temp.getEmail());
        assertNotNull(temp.getToken());
    }

    @Test
    @SneakyThrows
    void wrongCredentialsLoginTest() {
        // Mocking findByEmail method of UserRepository for user2@gmail.com
        Mockito.doReturn(Optional.empty()).when(userRepository).findByEmail("user2@gmail.com");

        // Creating login dto
        LoginDTO loginDTO = LoginDTO.builder()
                                    .email("user2@gmail.com")
                                    .password("456")
                                    .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/login")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(loginDTO)))
                              .andExpect(status().isUnauthorized())
                              .andReturn();

        // Checking error message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);
        assertEquals("Wrong email or password", message.getMessage());
    }

    @Test
    @SneakyThrows
    void disabledUserLoginTest() {
        // Mocking findByEmail method of UserRepository for user1@gmail.com
        Mockito.doReturn(Optional.of(holder.getUser())).when(userRepository).findByEmail("user1@gmail.com");

        // Creating login dto
        LoginDTO loginDTO = LoginDTO.builder()
                                    .email("user1@gmail.com")
                                    .password("123")
                                    .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/login")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(loginDTO)))
                              .andExpect(status().isUnauthorized())
                              .andReturn();

        // Checking error message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);
        assertEquals("Your account has not been confirmed. Check your email.",
                     message.getMessage());
    }

    @Test
    @SneakyThrows
    void invalidFormatLoginDataTest() {
        // Creating login dto
        LoginDTO loginDTO = LoginDTO.builder()
                                    .email("user1")
                                    .password("123")
                                    .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/login")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(loginDTO)))
                              .andExpect(status().isBadRequest())
                              .andReturn();

        // Checking error message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);
        assertEquals("Invalid login data",
                     message.getMessage());
    }

}
