package co.edu.escuelaing.securing_web.exception;

public class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException(String message) {
        super("User mail "+message+" already exist");
    }
}
