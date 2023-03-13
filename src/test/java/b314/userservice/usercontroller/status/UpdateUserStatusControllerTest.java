package b314.userservice.usercontroller.status;

import b314.userservice.TestUtils;
import b314.userservice.controller.Message;
import b314.userservice.dto.user.UserProfileDto;
import b314.userservice.entity.user.User;
import b314.userservice.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
public class UpdateUserStatusControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UpdateUserStatusTestEntityHolder holder;

    @MockBean
    private UserRepository userRepository;

    private String bearerJwt1;

    private String bearerJwt2;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInHs}")
    private int jwtExpirationInHours;

    private User saved;

    /**
     * Initializes some test properties
     */
    @BeforeEach
    void initialize() {
        // Creating jwt for 1st user
        bearerJwt1 = TestUtils.createJwt(jwtSecret, jwtExpirationInHours, holder.getUser1());

        // Creating jwt for 2nd user
        bearerJwt2 = TestUtils.createJwt(jwtSecret, jwtExpirationInHours, holder.getUser2());
    }

    @Test
    @SneakyThrows
    void forbiddenTest() {
        // Performing request
        mvc.perform(MockMvcRequestBuilders.patch("/users/status/2?status=enable")
                                          .contentType(MediaType.APPLICATION_JSON_VALUE))
           .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    void updateEnableTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getUser2()))
               .when(userRepository).findById(2);
        Mockito.doReturn(Optional.of(holder.getUser1()))
               .when(userRepository).findByEmail(any(String.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                saved = invocation.getArgument(0);
                return saved;
            }
        }).when(userRepository).save(any(User.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.patch("/users/status/2?status=ENABLE")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .header("Authorization", bearerJwt1))
                              .andExpect(status().isOk())
                              .andReturn();

        // Getting user profile dto from response
        UserProfileDto actual = (UserProfileDto) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                       UserProfileDto.class);

        // Asserting dtos
        assertEquals(holder.createUserProfileDtoFor2ndUserWithStatus(true), actual);

        // Asserting users
        holder.getUser2().setEnabled(true);
        assertEquals(holder.getUser2(), saved);
    }

    @Test
    @SneakyThrows
    void updateDisableTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getUser2()))
               .when(userRepository).findById(2);
        Mockito.doReturn(Optional.of(holder.getUser1()))
               .when(userRepository).findByEmail(any(String.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                saved = invocation.getArgument(0);
                return saved;
            }
        }).when(userRepository).save(any(User.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.patch("/users/status/2?status=disable")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .header("Authorization", bearerJwt1))
                              .andExpect(status().isOk())
                              .andReturn();

        // Getting user profile dto from response
        UserProfileDto actual = (UserProfileDto) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                       UserProfileDto.class);

        // Asserting dtos
        assertEquals(holder.createUserProfileDtoFor2ndUserWithStatus(false), actual);

        // Asserting users
        holder.getUser2().setEnabled(false);
        assertEquals(holder.getUser2(), saved);
    }

    @Test
    @SneakyThrows
    void invalidStatusTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getUser2()))
               .when(userRepository).findById(2);
        Mockito.doReturn(Optional.of(holder.getUser1()))
               .when(userRepository).findByEmail(any(String.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                saved = invocation.getArgument(0);
                return saved;
            }
        }).when(userRepository).save(any(User.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.patch("/users/status/2?status=undefined")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .header("Authorization", bearerJwt1))
                              .andExpect(status().isBadRequest())
                              .andReturn();

        // Getting error from response
        Message error = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                  Message.class);

        // Asserting
        assertEquals("Invalid new user status. Update was canceled.", error.getMessage());
    }

    @Test
    @SneakyThrows
    void regularUserAttemptTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getUser2()))
               .when(userRepository).findByEmail(any(String.class));

        // Performing request
        mvc.perform(MockMvcRequestBuilders.patch("/users/status/1?status=enable")
                                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                                          .header("Authorization", bearerJwt2))
           .andExpect(status().isForbidden())
           .andReturn();
    }

}
