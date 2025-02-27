package com.future.accountservice.service;

import com.future.accountservice.entity.Address;
import com.future.accountservice.entity.PaymentMethod;
import com.future.accountservice.entity.User;
import com.future.accountservice.exception.AccountAPIException;
import com.future.accountservice.exception.ResourceNotFoundException;
import com.future.accountservice.payload.*;
import com.future.accountservice.repository.AddressRepository;
import com.future.accountservice.repository.PaymentMethodRepository;
import com.future.accountservice.repository.UserRepository;
import com.future.futurecommon.util.SnowflakeIdGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public UserService(UserRepository userRepository,
                       AddressRepository addressRepository,
                       PaymentMethodRepository paymentMethodRepository,
                       ModelMapper modelMapper,
                       PasswordEncoder passwordEncoder,
                       SnowflakeIdGenerator snowflakeIdGenerator) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    // Get user by ID
    public UserDTO getUserById(Long userId) {
        User user = getUserOrThrow(userId);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        // Map roles to string names
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
        userDTO.setRoles(roleNames);

        return userDTO;
    }

    // Update user information
    public UserDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = getUserOrThrow(userId);

        // Update fields
        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());
        user.setPhone(userUpdateDTO.getPhone());

        User updatedUser = userRepository.save(user);
        return mapToUserDTO(updatedUser);
    }

    // Change password
    public void changePassword(Long userId, PasswordChangeDTO passwordChangeDTO) {
        User user = getUserOrThrow(userId);

        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
            throw new AccountAPIException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userRepository.save(user);
    }

    // Add an address
    @Transactional
    public AddressDTO addAddress(Long userId, AddressDTO addressDTO) {
        User user = getUserOrThrow(userId);

        Address address = modelMapper.map(addressDTO, Address.class);
        address.setId(snowflakeIdGenerator.generateId());
        address.setUser(user);

        // If this is set as default, update any existing default address
        if (address.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(defaultAddress -> {
                        defaultAddress.setDefault(false);
                        addressRepository.save(defaultAddress);
                    });
        }

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    // Update an address
    @Transactional
    public AddressDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        User user = getUserOrThrow(userId);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        // Verify address belongs to user
        if (!address.getUser().getId().equals(userId)) {
            throw new AccountAPIException(HttpStatus.FORBIDDEN, "You don't have permission to update this address");
        }

        // Update fields
        address.setRecipientName(addressDTO.getRecipientName());
        address.setPhone(addressDTO.getPhone());
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(addressDTO.getAddressLine2());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setCountry(addressDTO.getCountry());
        address.setAddressType(addressDTO.getAddressType());

        // If this is set as default, update any existing default address
        if (addressDTO.isDefault() && !address.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(defaultAddress -> {
                        defaultAddress.setDefault(false);
                        addressRepository.save(defaultAddress);
                    });
            address.setDefault(true);
        }

        Address updatedAddress = addressRepository.save(address);
        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    // Delete an address
    public void deleteAddress(Long userId, Long addressId) {
        User user = getUserOrThrow(userId);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        // Verify address belongs to user
        if (!address.getUser().getId().equals(userId)) {
            throw new AccountAPIException(HttpStatus.FORBIDDEN, "You don't have permission to delete this address");
        }

        addressRepository.delete(address);
    }

    // Get all addresses for a user
    public List<AddressDTO> getUserAddresses(Long userId) {
        getUserOrThrow(userId); // Verify user exists

        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toList());
    }

    // Add a payment method
    @Transactional
    public PaymentMethodDTO addPaymentMethod(Long userId, PaymentMethodDTO paymentMethodDTO) {
        User user = getUserOrThrow(userId);

        PaymentMethod paymentMethod = modelMapper.map(paymentMethodDTO, PaymentMethod.class);
        paymentMethod.setId(snowflakeIdGenerator.generateId());
        paymentMethod.setUser(user);

        // If this is set as default, update any existing default payment method
        if (paymentMethod.isDefault()) {
            paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(defaultPaymentMethod -> {
                        defaultPaymentMethod.setDefault(false);
                        paymentMethodRepository.save(defaultPaymentMethod);
                    });
        }

        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return modelMapper.map(savedPaymentMethod, PaymentMethodDTO.class);
    }

    // Update a payment method
    @Transactional
    public PaymentMethodDTO updatePaymentMethod(Long userId, Long paymentMethodId, PaymentMethodDTO paymentMethodDTO) {
        User user = getUserOrThrow(userId);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", paymentMethodId));

        // Verify payment method belongs to user
        if (!paymentMethod.getUser().getId().equals(userId)) {
            throw new AccountAPIException(HttpStatus.FORBIDDEN, "You don't have permission to update this payment method");
        }

        // Update fields
        paymentMethod.setCardNumber(paymentMethodDTO.getCardNumber());
        paymentMethod.setNameOnCard(paymentMethodDTO.getNameOnCard());
        paymentMethod.setExpirationMonth(paymentMethodDTO.getExpirationMonth());
        paymentMethod.setExpirationYear(paymentMethodDTO.getExpirationYear());
        paymentMethod.setSecurityCode(paymentMethodDTO.getSecurityCode());
        paymentMethod.setCardType(paymentMethodDTO.getCardType());
        paymentMethod.setBillingAddressId(paymentMethodDTO.getBillingAddressId());

        // If this is set as default, update any existing default payment method
        if (paymentMethodDTO.isDefault() && !paymentMethod.isDefault()) {
            paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(defaultPaymentMethod -> {
                        defaultPaymentMethod.setDefault(false);
                        paymentMethodRepository.save(defaultPaymentMethod);
                    });
            paymentMethod.setDefault(true);
        }

        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        return modelMapper.map(updatedPaymentMethod, PaymentMethodDTO.class);
    }

    // Delete a payment method
    public void deletePaymentMethod(Long userId, Long paymentMethodId) {
        User user = getUserOrThrow(userId);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod", "id", paymentMethodId));

        // Verify payment method belongs to user
        if (!paymentMethod.getUser().getId().equals(userId)) {
            throw new AccountAPIException(HttpStatus.FORBIDDEN, "You don't have permission to delete this payment method");
        }

        paymentMethodRepository.delete(paymentMethod);
    }

    // Get all payment methods for a user
    public List<PaymentMethodDTO> getUserPaymentMethods(Long userId) {
        getUserOrThrow(userId); // Verify user exists

        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserId(userId);
        return paymentMethods.stream()
                .map(paymentMethod -> modelMapper.map(paymentMethod, PaymentMethodDTO.class))
                .collect(Collectors.toList());
    }

    // Helper method to get user or throw exception
    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    // Helper method to map User to UserDTO
    private UserDTO mapToUserDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        // Map roles to string names
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
        userDTO.setRoles(roleNames);

        return userDTO;
    }
}