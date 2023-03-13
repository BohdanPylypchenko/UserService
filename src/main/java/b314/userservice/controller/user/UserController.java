package b314.userservice.controller.user;

import b314.userservice.dto.user.UpdateUserDto;
import b314.userservice.exception.user.EmailUsedByOtherAccountException;
import b314.userservice.exception.user.InvalidUserStatusException;
import b314.userservice.exception.user.NegativeBalanceException;
import b314.userservice.exception.user.NoUserWithSuchIdException;
import b314.userservice.service.user.UserBalanceService;
import b314.userservice.service.user.UserProfileService;
import b314.userservice.service.user.UserStatusService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserProfileService userProfileService;

    private final UserStatusService userStatusService;

    private final UserBalanceService userBalanceService;

    @Autowired
    public UserController(UserProfileService userProfileService,
                          UserStatusService userStatusService,
                          UserBalanceService userBalanceService) {
        this.userProfileService = userProfileService;
        this.userStatusService = userStatusService;
        this.userBalanceService = userBalanceService;
    }

    /**
     * GET mapping for user controller
     * Returns profile information of user with given id
     * @param id of User to get profile of
     */
    @GetMapping("/{id}")
    @PreAuthorize("@userIdVerificationService.hasSameId(#id) && hasRole('USER')")
    @SneakyThrows(NoUserWithSuchIdException.class)
    public ResponseEntity<?> getUserProfile(@PathVariable int id) {
        return ResponseEntity.ok()
                             .body(userProfileService.getUserProfileById(id));
    }

    /**
     * PUT mapping for user controller
     * Updates user with given info
     * @param id of user to update
     * @param updatedUserInfo contains new user info
     */
    @PutMapping("/{id}")
    @PreAuthorize("@userIdVerificationService.hasSameId(#id) && hasRole('USER')")
    @SneakyThrows({NoUserWithSuchIdException.class, EmailUsedByOtherAccountException.class})
    public ResponseEntity<?> updateUser(@PathVariable int id,
                                        @RequestBody @Valid UpdateUserDto updatedUserInfo) {
        // Updating and returning
        return ResponseEntity.ok()
                             .body(userProfileService.updateUserByIdWithInfo(id, updatedUserInfo));
    }

    /**
     * PATCH mapping for status
     * Updates user, represented by id, with new status
     * @param id of user to update status of
     * @param status new user status
     */
    @PatchMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SneakyThrows({NoUserWithSuchIdException.class, InvalidUserStatusException.class})
    public ResponseEntity<?> updateUserStatus(@PathVariable int id, @RequestParam String status) {
        // Updating user status and returning
        return ResponseEntity.ok()
                             .body(userStatusService.updateUserStatusById(id, status));
    }

    /**
     * PATCH mapping for tobalance
     * Increases user balance with given increment
     * @param id of user to increase balance of
     * @param balanceIncrement to add to user balance
     */
    @PatchMapping("/tobalance/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SneakyThrows(NoUserWithSuchIdException.class)
    public ResponseEntity<?> putMoneyToUser(@PathVariable int id,
                                            @RequestParam @Positive float balanceIncrement) {
        // Returning
        return ResponseEntity.status(200)
                             .body(userBalanceService.increaseUserBalanceById(id, balanceIncrement));
    }

    @PatchMapping("/frombalance/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SneakyThrows({NoUserWithSuchIdException.class, NegativeBalanceException.class})
    public ResponseEntity<?> takeMoneyFromUser(@PathVariable int id,
                                               @RequestParam @Positive float balanceDecrement) {
        // Returning
        return ResponseEntity.ok()
                             .body(userBalanceService.decreaseUserBalanceById(id, balanceDecrement));
    }

}
