package com.future.accountservice.controller;

import com.future.accountservice.payload.*;
import com.future.accountservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account-service/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.updateUser(userId, userUpdateDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/{userId}/change-password")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<String> changePassword(@PathVariable Long userId, @RequestBody PasswordChangeDTO passwordChangeDTO) {
        userService.changePassword(userId, passwordChangeDTO);
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    // Address endpoints
    @PostMapping("/{userId}/addresses")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<AddressDTO> addAddress(@PathVariable Long userId, @RequestBody AddressDTO addressDTO) {
        AddressDTO savedAddress = userService.addAddress(userId, addressDTO);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddress = userService.updateAddress(userId, addressId, addressDTO);
        return new ResponseEntity<>(updatedAddress, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<String> deleteAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        userService.deleteAddress(userId, addressId);
        return new ResponseEntity<>("Address deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/{userId}/addresses")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@PathVariable Long userId) {
        List<AddressDTO> addresses = userService.getUserAddresses(userId);
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    // Payment Method endpoints
    @PostMapping("/{userId}/payment-methods")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<PaymentMethodDTO> addPaymentMethod(
            @PathVariable Long userId,
            @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethodDTO savedPaymentMethod = userService.addPaymentMethod(userId, paymentMethodDTO);
        return new ResponseEntity<>(savedPaymentMethod, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/payment-methods/{paymentMethodId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(
            @PathVariable Long userId,
            @PathVariable Long paymentMethodId,
            @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethodDTO updatedPaymentMethod = userService.updatePaymentMethod(userId, paymentMethodId, paymentMethodDTO);
        return new ResponseEntity<>(updatedPaymentMethod, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/payment-methods/{paymentMethodId}")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<String> deletePaymentMethod(
            @PathVariable Long userId,
            @PathVariable Long paymentMethodId) {
        userService.deletePaymentMethod(userId, paymentMethodId);
        return new ResponseEntity<>("Payment method deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/{userId}/payment-methods")
    @PreAuthorize("authentication.principal.username == @userService.getUserById(#userId).email || hasRole('ADMIN')")
    public ResponseEntity<List<PaymentMethodDTO>> getUserPaymentMethods(@PathVariable Long userId) {
        List<PaymentMethodDTO> paymentMethods = userService.getUserPaymentMethods(userId);
        return new ResponseEntity<>(paymentMethods, HttpStatus.OK);
    }
}