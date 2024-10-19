package co.edu.escuelaing.securing_web.controller;

import co.edu.escuelaing.securing_web.data.User;
import co.edu.escuelaing.securing_web.exception.InvalidCredentialsException;
import co.edu.escuelaing.securing_web.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import co.edu.escuelaing.securing_web.exception.UserAlreadyExistException;
import co.edu.escuelaing.securing_web.exception.UserBadRequestException;
import co.edu.escuelaing.securing_web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://myapachearep.duckdns.org")
public class UsersController {
    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUsers(HttpServletRequest request) {
        return ResponseEntity.ok(userService.findUsers());
    }

    @GetMapping("{mail}")
    public ResponseEntity<?> getUserByMail(@PathVariable String mail){
        try {
            User user = userService.findUserByMail(mail);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody UserDto userDto) {
        try {
            userService.createUser(userDto);
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UserBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok(userDto.getMail());
    }

    // Controlador de login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto loginDto, HttpServletRequest request) {
        try {
            // Verificar credenciales y obtener el usuario autenticado
            User user = userService.authenticate(loginDto);
            return ResponseEntity.ok("Login successful for user: " + loginDto.getMail());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found: " + e.getMessage());
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials: " + e.getMessage());
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate(); // Invalida la sesión
        return ResponseEntity.ok().build(); // Retorna un 200 OK para confirmar el logout
    }
}


