package b314.userservice.usercontroller.getput;

import b314.userservice.TestUtils;
import b314.userservice.controller.Message;
import b314.userservice.dto.user.UpdateUserDto;
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
import org.springframework.dao.DataIntegrityViolationException;
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
public class PutUserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GetPutTestEntityHolder holder;

    @MockBean
    private UserRepository userRepository;

    private String bearerJwt1;

    private String bearerJwt2;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInHs:}")
    private int jwtExpirationInHours;

    private User saved;

    /**
     * Initializes some test properties
     */
    @BeforeEach
    void initialize() {
        // Enabling expected user
        holder.getExpectedUser1().setEnabled(true);

        // Creating jwt for user 1
        bearerJwt1 = TestUtils.createJwt(jwtSecret, jwtExpirationInHours, holder.getExpectedUser1());

        // Creating jwt for user 2
        bearerJwt2 = TestUtils.createJwt(jwtSecret, jwtExpirationInHours, holder.getExpectedUser2());
    }

    @Test
    @SneakyThrows
    void successPutTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findById(1);
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findByEmail(holder.getExpectedUser1().getEmail());

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                saved = invocation.getArgument(0);
                return saved;
            }
        }).when(userRepository).save(any(User.class));

        // Creating update user dto
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                                                   .email("SteveJ0bs@apple.com")
                                                   .firstName("Steve")
                                                   .lastName("Jobs")
                                                   .age(63)
                                                   .driverLicense("IphoneGuy217")
                                                   .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/users/1")
                                                             .header("Authorization", bearerJwt1)
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(updateUserDto)))
                              .andExpect(status().isOk())
                              .andReturn();

        // Asserting saved and changed users
        assertEquals(holder.getExpectedChangedUser(), saved);

        // Getting user profile dto from response
        UserProfileDto actual = (UserProfileDto) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                       UserProfileDto.class);

        // Asserting
        assertEquals(holder.getExpectedChangedUserProfileDto(), actual);
    }

    @Test
    @SneakyThrows
    void invalidUpdateUserDtoTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findById(any(Integer.class));
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findByEmail(any(String.class));

        // Creating dto
        UpdateUserDto invalidDto = UpdateUserDto.builder()
                                                .email("SteveJ0bs@apple.com")
                                                .firstName("Steve")
                                                .lastName("Jobs")
                                                .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/users/1")
                                                             .header("Authorization", bearerJwt1)
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(invalidDto)))
                              .andExpect(status().isBadRequest())
                              .andReturn();

        // Getting message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                          Message.class);
        // Asserting message
        assertEquals("Invalid data", message.getMessage());
    }

    @Test
    @SneakyThrows
    void wrongIdAttempt() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findByEmail(holder.getExpectedUser1().getEmail());
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findById(1);
        Mockito.doReturn(Optional.of(holder.getExpectedUser2()))
               .when(userRepository).findById(2);

        // Creating dto
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                                                   .email("SteveJ0bs@apple.com")
                                                   .firstName("Steve")
                                                   .lastName("Jobs")
                                                   .age(63)
                                                   .driverLicense("IphoneGuy217")
                                                   .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/users/2")
                                                             .header("Authorization", bearerJwt1)
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(updateUserDto)))
                              .andExpect(status().isForbidden())
                              .andReturn();

        // Getting message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);

        // Asserting message
        assertEquals("Trying to access other user profile", message.getMessage());
    }

    @Test
    @SneakyThrows
    void noUserWithRequiredIdTest() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findById(any(Integer.class));
        Mockito.doReturn(Optional.of(holder.getExpectedUser1()))
               .when(userRepository).findByEmail(any(String.class));

        // Creating dto
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                                                   .email("SteveJ0bs@apple.com")
                                                   .firstName("Steve")
                                                   .lastName("Jobs")
                                                   .age(63)
                                                   .driverLicense("IphoneGuy217")
                                                   .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/users/2")
                                                             .header("Authorization", bearerJwt1)
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(updateUserDto)))
                              .andExpect(status().isForbidden())
                              .andReturn();

        // Getting message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);

        // Asserting message
        assertEquals("Trying to access other user profile", message.getMessage());
    }

    @Test
    @SneakyThrows
    void forbiddenTest() {
        // Creating dto
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                                                   .email("SteveJ0bs@apple.com")
                                                   .firstName("Steve")
                                                   .lastName("Jobs")
                                                   .age(63)
                                                   .driverLicense("IphoneGuy217")
                                                   .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/users/1")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(updateUserDto)))
                              .andExpect(status().isUnauthorized())
                              .andReturn();

        // Getting message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);

        // Asserting message
        assertEquals("Anonymous access is not allowed", message.getMessage());
    }

    @Test
    @SneakyThrows
    void updateEmailWithAlreadyUsed() {
        // Configuring mocks
        Mockito.doReturn(Optional.of(holder.getExpectedUser2())).when(userRepository).findById(2);
        Mockito.doReturn(Optional.of(holder.getExpectedUser2())).when(userRepository).findByEmail("user2@outlook.com");
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new DataIntegrityViolationException("Multiple users with same id");
            }
        }).when(userRepository).save(any(User.class));

        // Creating dto
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                                                   .email("user1@gmail.com")
                                                   .firstName("Steve")
                                                   .lastName("Jobs")
                                                   .age(63)
                                                   .driverLicense("IphoneGuy217")
                                                   .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.put("/users/2")
                                                             .header("Authorization", bearerJwt2)
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(updateUserDto)))
                              .andExpect(status().isBadRequest())
                              .andReturn();

        // Getting message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);

        // Asserting message
        assertEquals("Provided email is already used by another account.", message.getMessage());
    }

}
