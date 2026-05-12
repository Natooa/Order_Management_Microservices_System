package kz.natooa.exception;

public class PaymentAlreadyProcessedException extends BaseApiException{
    public PaymentAlreadyProcessedException(String msg){
        super(msg);
    }
}
