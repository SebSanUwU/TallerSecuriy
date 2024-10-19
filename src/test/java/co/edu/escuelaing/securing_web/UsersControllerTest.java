package co.edu.escuelaing.securing_web;

import co.edu.escuelaing.securing_web.controller.UsersController;
import co.edu.escuelaing.securing_web.service.UserService;
import co.edu.escuelaing.securing_web.exception.*;
import co.edu.escuelaing.securing_web.data.User;
import co.edu.escuelaing.securing_web.controller.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsersControllerTest {

	@Mock
	private UserService userService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@InjectMocks
	private UsersController usersController;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testGetUsers() {
		// Mocking the service response
		User user1 = new User("user1@example.com", "password1");
		User user2 = new User("user2@example.com", "password2");
		List<User> users = Arrays.asList(user1, user2);
		when(userService.findUsers()).thenReturn(users);

		// Calling the controller method
		ResponseEntity<?> response = usersController.getUsers(request);

		// Asserting the response
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(users, response.getBody());
		verify(userService, times(1)).findUsers();
	}

	@Test
	public void testGetUserByMail_Success() throws UserNotFoundException {
		// Mocking the service response
		User user = new User("user@example.com", "password");
		when(userService.findUserByMail("user@example.com")).thenReturn(user);

		// Calling the controller method
		ResponseEntity<?> response = usersController.getUserByMail("user@example.com");

		// Asserting the response
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(user, response.getBody());
	}

	@Test
	public void testGetUserByMail_UserNotFound() throws UserNotFoundException {
		// Mocking the exception
		when(userService.findUserByMail("nonexistent@example.com")).thenThrow(new UserNotFoundException("User not found"));

		// Calling the controller method
		ResponseEntity<?> response = usersController.getUserByMail("nonexistent@example.com");

		// Asserting the response
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Not found by mail :User not found", response.getBody());
	}

	@Test
	public void testAddUser_Success() throws UserAlreadyExistException, UserBadRequestException {
		// Mocking the service call
		UserDto userDto = new UserDto("newuser@example.com", "password");
		doNothing().when(userService).createUser(userDto);

		// Calling the controller method
		ResponseEntity<String> response = usersController.addUser(userDto);

		// Asserting the response
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("newuser@example.com", response.getBody());
		verify(userService, times(1)).createUser(userDto);
	}

	@Test
	public void testAddUser_UserAlreadyExists() throws UserAlreadyExistException, UserBadRequestException {
		// Mocking the exception
		UserDto userDto = new UserDto("existing@example.com", "password");
		doThrow(new UserAlreadyExistException("User already exists")).when(userService).createUser(userDto);

		// Calling the controller method
		ResponseEntity<String> response = usersController.addUser(userDto);

		// Asserting the response
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals("User mail User already exists already exist", response.getBody());
	}

	@Test
	public void testLogin_Success() throws UserNotFoundException, InvalidCredentialsException {
		// Mocking the service response
		UserDto loginDto = new UserDto("user@example.com", "password");
		User user = new User("user@example.com", "password");
		when(userService.authenticate(loginDto)).thenReturn(user);

		// Calling the controller method
		ResponseEntity<String> response = usersController.login(loginDto, request);

		// Asserting the response
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Login successful for user: user@example.com", response.getBody());
	}

	@Test
	public void testLogin_UserNotFound() throws UserNotFoundException, InvalidCredentialsException {
		// Mocking the exception
		UserDto loginDto = new UserDto("nonexistent@example.com", "password");
		doThrow(new UserNotFoundException("User not found")).when(userService).authenticate(loginDto);

		// Calling the controller method
		ResponseEntity<String> response = usersController.login(loginDto, request);

		// Asserting the response
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals("User not found: Not found by mail :User not found", response.getBody());
	}


}

