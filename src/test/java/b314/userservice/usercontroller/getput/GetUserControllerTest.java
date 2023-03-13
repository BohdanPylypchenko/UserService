package b314.userservice.usercontroller.getput;

import b314.userservice.TestUtils;
import b314.userservice.controller.Message;
import b314.userservice.dto.user.UserProfileDto;
import b314.userservice.entity.role.Role;
import b314.userservice.entity.role.RoleType;
import b314.userservice.entity.user.User;
import b314.userservice.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class GetUserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GetPutTestEntityHolder holder;

    @MockBean
    private UserRepository userRepository;

    private String bearerJwt;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInHs}")
    private int jwtExpirationInHours;

    /**
     * Initializes some test properties
     */
    @BeforeEach
    void initialize() {
        // Enabling expected user
        holder.getExpectedUser1().setEnabled(true);

        // Creating jwt
        bearerJwt = TestUtils.createJwt(jwtSecret, jwtExpirationInHours, holder.getExpectedUser1());
    }

    @Test
    @SneakyThrows
    void successGetTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findByEmail(any(String.class));
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findById(any(Integer.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/users/1")
                                                             .header("Authorization", bearerJwt)
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE))
                              .andExpect(status().isOk())
                              .andReturn();

        // Getting user profile from response
        UserProfileDto actual = (UserProfileDto) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                       UserProfileDto.class);

        // Asserting
        assertEquals(holder.getUserProfileDto(), actual);
    }

    @Test
    @SneakyThrows
    void forbiddenGetTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findByEmail(any(String.class));
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findById(any(Integer.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/users/1")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE))
                              .andExpect(status().isUnauthorized())
                              .andReturn();

        // Getting error message
        Message actual = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                   Message.class);

        // Asserting
        assertEquals("Anonymous access is not allowed", actual.getMessage());
    }

    @Test
    @SneakyThrows
    void noUserWithRequestIdTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findByEmail(any(String.class));
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findById(any(Integer.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/users/2")
                                                             .header("Authorization", bearerJwt)
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE))
                              .andExpect(status().isForbidden())
                              .andReturn();

        // Getting error
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);

        // Asserting
        assertEquals("Trying to access other user profile", message.getMessage());
    }

    @Test
    @SneakyThrows
    void wrongIdGetAttemptTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findByEmail(holder.getExpectedUser1().getEmail());
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findById(1);
        List<Role> roles = new ArrayList<>();
        roles.add(new Role());
        roles.get(0).setRoleType(RoleType.USER);
        User second = User.builder()
                          .id(2)
                          .email("Obi-Van-Kenoby@gmail.com")
                          .password("hello there 111")
                          .firstName("Obi-van")
                          .lastName("Kenoby")
                          .age(46)
                          .driverLicense("hfoagu389q")
                          .enabled(true)
                          .roles(roles)
                          .build();
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findByEmail(second.getEmail());
        Mockito.doReturn(Optional.of(second))
               .when(userRepository).findById(2);

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/users/2")
                                                             .header("Authorization", bearerJwt)
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE))
                              .andExpect(status().isForbidden())
                              .andReturn();

        // Getting error
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);

        // Asserting
        assertEquals("Trying to access other user profile", message.getMessage());
    }

}
