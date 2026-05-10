package kz.natooa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class PaymentServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
