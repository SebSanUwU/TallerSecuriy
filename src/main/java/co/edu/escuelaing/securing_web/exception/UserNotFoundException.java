package co.edu.escuelaing.securing_web.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String mail){
        super("Not found by mail :"+mail);
    }
}

