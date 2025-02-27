package com.future.accountservice.service;

import com.future.accountservice.entity.User;
import com.future.accountservice.exception.ResourceNotFoundException;
import com.future.accountservice.payload.UserDTO;
import com.future.accountservice.payload.UserUpdateDTO;
import com.future.accountservice.repository.AddressRepository;
import com.future.accountservice.repository.PaymentMethodRepository;
import com.future.accountservice.repository.UserRepository;
import com.future.futurecommon.util.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private UserUpdateDTO userUpdateDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhone("1234567890");
        user.setRoles(new HashSet<>());

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setPhone("1234567890");
        userDTO.setRoles(new HashSet<>());

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName("Updated");
        userUpdateDTO.setLastName("Name");
        userUpdateDTO.setPhone("0987654321");
    }

    @Test
    void getUserById_ShouldReturnUserDTO_WhenUserExists() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(userDTO.getId(), result.getId());
        verify(userRepository).findById(1L);
        verify(modelMapper).map(user, UserDTO.class);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
        assertEquals("Resource User not found with id : 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserDTO_WhenUserExists() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserDTO.class))).thenReturn(userDTO);

        // Act
        UserDTO result = userService.updateUser(1L, userUpdateDTO);

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);

        // Verify that user fields were updated
        assertEquals(userUpdateDTO.getFirstName(), user.getFirstName());
        assertEquals(userUpdateDTO.getLastName(), user.getLastName());
        assertEquals(userUpdateDTO.getPhone(), user.getPhone());
    }
}