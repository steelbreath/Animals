package ua.pumb.animals.exception;

public class SuchFormatNotSupportedException extends RuntimeException {

    public SuchFormatNotSupportedException() {
    }

    public SuchFormatNotSupportedException(String message) {
        super(message);
    }
}
