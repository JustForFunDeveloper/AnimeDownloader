package tapsi.exception;

/**
 * {@link MyException} extends from {@link Exception}.
 * Will implement some logging later on.
 */
public class MyException extends Exception {
    public MyException(String message) {
        super(message);
    }
}
