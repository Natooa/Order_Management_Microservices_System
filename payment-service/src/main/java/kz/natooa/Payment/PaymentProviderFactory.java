package kz.natooa.Payment;

import kz.natooa.payment.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentProviderFactory {
    private final Map<PaymentMethod, PaymentProvider> providers;

    public PaymentProviderFactory(List<PaymentProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        PaymentProvider::getSupportedPaymentMethod,
                        Function.identity()
                ));
    }

    public PaymentProvider getProvider(PaymentMethod paymentMethod) {
        PaymentProvider provider = providers.get(paymentMethod);
        if (provider == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
        return provider;
    }
}

