package b314.userservice.usercontroller.balance;

import b314.userservice.TestUtils;
import b314.userservice.controller.Message;
import b314.userservice.dto.user.UserBalanceDto;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class FromBalanceUserTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BalanceTestEntityHolder holder;

    @MockBean
    private UserRepository userRepository;

    private String adminJwt;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInHs}")
    private int jwtExpirationInHours;

    @BeforeEach
    void initialize() {
        adminJwt = TestUtils.createJwt(jwtSecret, jwtExpirationInHours, holder.getAdmin());
    }

    @Test
    @SneakyThrows
    void noCredentialsTest() {
        // Performing request
        mvc.perform(MockMvcRequestBuilders.patch("/users/frombalance/2?balanceDecrement=100")
                                          .contentType(MediaType.APPLICATION_JSON_VALUE))
           .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void regularUserTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getRegular()))
               .when(userRepository).findByEmail(any(String.class));

        // Creating regular user jwt
        String jwt = TestUtils.createJwt(jwtSecret, jwtExpirationInHours, holder.getRegular());

        // Performing request
        mvc.perform(MockMvcRequestBuilders.patch("/users/frombalance/2?balanceDecrement=100")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", jwt))
           .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    void successTest() {
        // Initializing regular user with needed balance
        holder.initializeRegularWithBalance(10);

        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getAdmin()))
               .when(userRepository).findByEmail(any(String.class));
        Mockito.doReturn(Optional.of(holder.getRegular()))
               .when(userRepository).findById(any(Integer.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.patch("/users/frombalance/2?balanceDecrement=7")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .header("Authorization", adminJwt))
                              .andExpect(status().isOk())
                              .andReturn();

        // Asserting
        UserBalanceDto userBalanceDto = (UserBalanceDto) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                               UserBalanceDto.class);
        assertEquals(3.0f, userBalanceDto.getBalance());
    }

    @Test
    @SneakyThrows
    void negativeDecrementTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getAdmin()))
               .when(userRepository).findByEmail(any(String.class));
        Mockito.doReturn(Optional.of(holder.getRegular()))
               .when(userRepository).findById(any(Integer.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.patch("/users/frombalance/2?balanceDecrement=-0.5")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .header("Authorization", adminJwt))
                              .andExpect(status().isBadRequest())
                              .andReturn();

        // Asserting message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(), Message.class);
        assertEquals("Invalid data", message.getMessage());
    }

    @Test
    @SneakyThrows
    void negativeBalanceCaseTest() {
        // Initializing regular user with needed balance
        holder.initializeRegularWithBalance(4.7f);

        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getAdmin()))
                .when(userRepository).findByEmail(any(String.class));
        Mockito.doReturn(Optional.of(holder.getRegular()))
                .when(userRepository).findById(any(Integer.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.patch("/users/frombalance/2?balanceDecrement=6.9")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .header("Authorization", adminJwt))
                              .andExpect(status().is(400))
                              .andReturn();

        // Asserting
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(), Message.class);
        assertEquals("Can't decrease user balance (id = 2): user balance = 4.7 < balance decrement = 6.9", message.getMessage());
    }

}
