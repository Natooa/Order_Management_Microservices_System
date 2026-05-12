package kz.natooa.exception;

public class BaseApiException extends Exception{
    protected BaseApiException(String msg){
        super(msg);
    }
}
