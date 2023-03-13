package b314.userservice.loginregistration;

import b314.userservice.TestUtils;
import b314.userservice.controller.Message;
import b314.userservice.dto.registration.UserRegistrationConfirmationDTO;
import b314.userservice.dto.registration.UserRegistrationDTO;
import b314.userservice.entity.role.Role;
import b314.userservice.entity.role.RoleType;
import b314.userservice.entity.user.User;
import b314.userservice.entity.userregistrationconfirmation.UserRegistrationConfirmationToken;
import b314.userservice.repository.UserRegistrationConfirmationTokenRepository;
import b314.userservice.repository.UserRepository;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.mail.internet.MimeMessage;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LoginRegistrationTestEntityHolder holder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${userservice.user-registration-confirmation-token.hsLifetime}")
    private int hsLifetime;

    private User actualUser;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserRegistrationConfirmationTokenRepository tokenRepository;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("service@mymail.com", "password"))
            .withPerMethodLifecycle(false);

    @SneakyThrows
    @Test
    void registerUserWithoutConfirmationTest() {
        // Setting up user repository mock
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                actualUser = invocation.getArgument(0);
                return actualUser;
            }
        }).when(userRepository).save(any(User.class));

        // Setting up save method of user registration confirmation token repository mock
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
        }).when(tokenRepository).save(any(UserRegistrationConfirmationToken.class));

        // Creating register dto
        UserRegistrationDTO userRegistrationDTO = UserRegistrationDTO.builder()
                                                         .email("user1@gmail.com")
                                                         .password("123")
                                                         .repeatPassword("123")
                                                         .firstName("John")
                                                         .lastName("Smith")
                                                         .age(23)
                                                         .driverLicense("license1234")
                                                         .roles(new String[]{"USER", "CAR_OWNER"})
                                                         .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/registration")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(userRegistrationDTO)))
                              .andExpect(status().isCreated())
                              .andReturn();

        // Getting userFromResponse user
        User userFromResponse = (User) TestUtils.jsonToClass(result.getResponse().getContentAsString(), User.class);

        // Asserting expected and received
        assertEquals(holder.getUser(), userFromResponse);

        // Asserting expected and actual saved in db
        assertEquals(holder.getUser(), actualUser);

        // Getting initial user registration confirmation uri from email
        MimeMessage confirmationEmail = greenMail.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(confirmationEmail);

        // Asserting body
        assertNotEquals("", body);
    }

    @Test
    @SneakyThrows
    void goodConfirmationAttemptTest() {
        // Setting confirmation properties
        String rawToken = "90875q";
        String confirmationUri = "http://localhost:8080/registration-confirm?token=" + rawToken;

        // Setting up repositories
        List<Role> roles = List.of(new Role(), new Role());
        roles.get(0).setRoleType(RoleType.USER);
        roles.get(1).setRoleType(RoleType.CAR_OWNER);
        actualUser = User.builder()
                         .email("user1@gmail.com")
                         .password(passwordEncoder.encode("123"))
                         .firstName("John")
                         .lastName("Smith")
                         .age(23)
                         .driverLicense("license1234")
                         .roles(roles)
                         .enabled(false)
                         .build();
        Mockito.doReturn(Optional.of(new UserRegistrationConfirmationToken(rawToken, actualUser, hsLifetime)))
               .when(tokenRepository)
               .getUserRegistrationConfirmationTokenByRawToken(any(String.class));
        Mockito.doReturn(1)
               .when(tokenRepository)
               .countByUser(any(User.class));

        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(confirmationUri))
                              .andExpect(status().isOk())
                              .andReturn();

        // Getting actual UserRegistrationConfirmationDTO response
        UserRegistrationConfirmationDTO confirmed = (UserRegistrationConfirmationDTO) TestUtils.jsonToClass(result.getResponse().getContentAsString(), UserRegistrationConfirmationDTO.class);

        // Asserting DTOs
        assertEquals(holder.getConfirmedUserRegistrationConfirmationDTO(), confirmed);

        // Checking for confirmation message income
        MimeMessage confirmationEmail = greenMail.getReceivedMessages()[greenMail.getReceivedMessages().length - 1];
        assertEquals("Your account on DriveDynamics has been confirmed!", GreenMailUtil.getBody(confirmationEmail));

        // Asserting user status
        assertTrue(actualUser.isEnabled());
    }

    @Test
    @SneakyThrows
    void sameEmail2TimeRegistrationAttemptTest() {
        // Reconfiguring mock repository
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                throw new SQLIntegrityConstraintViolationException();
            }
        }).when(userRepository).save(any(User.class));

        // Attempt to register with same email again
        UserRegistrationDTO userRegistrationDTO = UserRegistrationDTO.builder()
                                                         .email("user1@gmail.com")
                                                         .password("abc")
                                                         .repeatPassword("abc")
                                                         .firstName("Amanda")
                                                         .lastName("Smith")
                                                         .age(17)
                                                         .driverLicense("aa_license1234")
                                                         .roles(new String[]{"USER"})
                                                         .build();

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/registration")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(TestUtils.mapToJson(userRegistrationDTO)))
                              .andExpect(status().isBadRequest())
                              .andReturn();

        // Checking email
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);
        assertEquals("Account with given email already exists!", message.getMessage());
    }

    @Test
    @SneakyThrows
    void passwordNotEqualsToRepeatPasswordRegistrationTest() {
        // Creating register dto
        UserRegistrationDTO userRegistrationDTO = UserRegistrationDTO.builder()
                                                         .email("user1@gmail.com")
                                                         .password("123")
                                                         .repeatPassword("abc")
                                                         .lastName("Smith")
                                                         .age(23)
                                                         .driverLicense("license1234")
                                                         .roles(new String[]{"USER", "CAR_OWNER"})
                                                         .build();

        String temp = TestUtils.mapToJson(userRegistrationDTO);

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/registration")
                                                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                             .content(temp))
                              .andExpect(status().isBadRequest())
                              .andReturn();

        // Checking email
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);
        assertEquals("Invalid registration data", message.getMessage());

    }

}
