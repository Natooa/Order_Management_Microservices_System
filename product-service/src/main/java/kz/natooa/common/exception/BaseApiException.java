package kz.natooa.common.exception;

public class BaseApiException extends RuntimeException{
    protected BaseApiException(String msg){
        super(msg);
    }
}
