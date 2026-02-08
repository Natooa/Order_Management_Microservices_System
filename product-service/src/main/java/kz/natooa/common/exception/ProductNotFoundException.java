package kz.natooa.common.exception;

public class ProductNotFoundException extends BaseApiException{
    public ProductNotFoundException(String msg){
        super(msg);
    }
}
