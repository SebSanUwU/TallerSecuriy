package co.edu.escuelaing.securing_web.service;


import co.edu.escuelaing.securing_web.controller.UserDto;
import co.edu.escuelaing.securing_web.data.User;
import co.edu.escuelaing.securing_web.exception.InvalidCredentialsException;
import co.edu.escuelaing.securing_web.exception.UserAlreadyExistException;
import co.edu.escuelaing.securing_web.exception.UserBadRequestException;
import co.edu.escuelaing.securing_web.exception.UserNotFoundException;
import co.edu.escuelaing.securing_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final Map<String, User> userDatabase = UserRepository.userDatabase;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor que inicializa el servicio con el codificador de contraseñas.
     *
     * @param passwordEncoder el codificador de contraseñas utilizado para cifrar las contraseñas de los usuarios.
     */
    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Método privado para guardar un usuario en la base de datos. Cifra la contraseña antes de guardarla.
     *
     * @param userDto objeto DTO que contiene los datos del usuario (correo electrónico y contraseña).
     */
    private void saveUser(UserDto userDto) {
        String passwordEncrypt = passwordEncoder.encode(userDto.getPassword());
        User userSave = new User(userDto.getMail(), passwordEncrypt);
        userDatabase.put(userSave.getMail(), userSave);
    }

    /**
     * Obtiene una lista de todos los usuarios almacenados en la base de datos simulada.
     *
     * @return una lista de objetos {@link User} que representan a todos los usuarios registrados.
     */
    public List<User> findUsers() {
        // Convierte la colección de valores a una lista
        Collection<User> userCollection = userDatabase.values();
        return new ArrayList<>(userCollection); // Convierte a ArrayList
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     *
     * @param mail la dirección de correo electrónico del usuario a buscar.
     * @return el objeto {@link User} si se encuentra el usuario.
     * @throws UserNotFoundException si no se encuentra un usuario con el correo electrónico proporcionado.
     */
    public User findUserByMail(String mail) throws UserNotFoundException {
        User userFound = userDatabase.get(mail);
        if (userFound == null) {
            throw new UserNotFoundException(mail);
        }
        return userFound;
    }

    /**
     * Crea un nuevo usuario a partir del DTO proporcionado.
     * Verifica si el usuario ya existe o si los datos son válidos.
     *
     * @param userDto el objeto {@link UserDto} que contiene la información del nuevo usuario (correo electrónico y contraseña).
     * @throws UserAlreadyExistException si el usuario con el correo electrónico ya existe.
     * @throws UserBadRequestException si los datos proporcionados son inválidos o incompletos.
     */
    public void createUser(UserDto userDto) throws UserAlreadyExistException, UserBadRequestException {
        if (userDto.getMail() == null || userDto.getPassword() == null) {
            throw new UserBadRequestException();
        }
        if (!(userDatabase.get(userDto.getMail()) == null)) {
            throw new UserAlreadyExistException(userDto.getMail());
        }
        saveUser(userDto);
    }

    /**
     * Autentica a un usuario basado en sus credenciales (correo electrónico y contraseña).
     *
     * @param loginDto el objeto {@link UserDto} que contiene las credenciales del usuario (correo electrónico y contraseña).
     * @return el objeto {@link User} si la autenticación es exitosa.
     * @throws UserNotFoundException si no se encuentra un usuario con el correo electrónico proporcionado.
     * @throws InvalidCredentialsException si la contraseña proporcionada es incorrecta.
     */
    public User authenticate(UserDto loginDto) throws UserNotFoundException, InvalidCredentialsException {
        User user = findUserByMail(loginDto.getMail());

        // Verificar si el usuario existe
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + loginDto.getMail());
        }

        // Verificar la contraseña
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Devuelve el usuario autenticado
        return user; // Aquí se devuelve el objeto User
    }
}


