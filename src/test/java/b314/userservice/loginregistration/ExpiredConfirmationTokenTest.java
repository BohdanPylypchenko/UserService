package b314.userservice.loginregistration;

import b314.userservice.TestUtils;
import b314.userservice.controller.Message;
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
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ExpiredConfirmationTokenTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${userservice.user-registration-confirmation-token.hsLifetime}")
    private int hsLifetime;

    @Value("${userservice.user-registration-confirmation-token.maxFailedCount}")
    private int maxFailedCount;

    private String confirmationUri;

    private User actualUser;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserRegistrationConfirmationTokenRepository tokenRepository;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("service@mymail.com", "password"))
            .withPerMethodLifecycle(false);

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("userservice.user-registration-confirmation-token.hsLifetime", () -> 0);
    }

    @BeforeEach
    void initializeTestProperties() {
        // Setting up confirmation uri properties
        String rawToken = "90875q";
        confirmationUri = "http://localhost:8080/registration-confirm?token=" + rawToken;

        // Setting up getUserRegistrationConfirmationTokenByRawToken mock
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
    }

    @Test
    @SneakyThrows
    void tryConfirmAndGetRepeatEmailTest() {
        Mockito.doReturn(1)
               .when(tokenRepository)
               .countByUser(any(User.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(confirmationUri))
                                          .andExpect(status().isBadRequest())
                              .andReturn();

        // Checking error message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);
        assertEquals("Sorry, your confirmation token expired. Another confirmation email was sent to you.",
                     message.getMessage());

        // Checking for another confirmation message income
        MimeMessage confirmationEmail = greenMail.getReceivedMessages()[greenMail.getReceivedMessages().length - 1];
        assertEquals("You did not confirm your account with last confirmation mail. You have 3 confirmation attempts left.",
                     GreenMailUtil.getBody(confirmationEmail).split("\r\n")[0]);
        assertNotEquals("", GreenMailUtil.getBody(confirmationEmail).split("\r\n")[1]);

        // Asserting user status
        assertFalse(actualUser.isEnabled());
    }

    @Test
    @SneakyThrows
    void failedAllAttemptsTest() {
        // Setting up repositories
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                actualUser = null;
                return actualUser;
            }
        }).when(userRepository).delete(any(User.class));
        Mockito.doReturn(maxFailedCount)
               .when(tokenRepository)
               .countByUser(any(User.class));

        // Performing request
        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(confirmationUri))
                              .andExpect(status().isBadRequest())
                              .andReturn();

        // Checking error message
        Message message = (Message) TestUtils.jsonToClass(result.getResponse().getContentAsString(),
                                                                    Message.class);
        assertEquals("Sorry, max failed confirmation attempts count was reached.",
                     message.getMessage());

        // Asserting deletion
        assertNull(actualUser);

        // Checking for fails message income
        MimeMessage confirmationEmail = greenMail.getReceivedMessages()[greenMail.getReceivedMessages().length - 1];
        assertEquals("Sorry, you last token has expired, you did not confirm your account so it was deleted. Try to register again.",
                     GreenMailUtil.getBody(confirmationEmail));
    }

}
