package system.payments.poc.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import system.payments.poc.model.Merchant;
import system.payments.poc.repository.MerchantRepository;

@RequiredArgsConstructor
public class MerchantProcessor implements ItemProcessor<Merchant, Merchant> {
    private final MerchantRepository merchantRepository;

    @Override
    public Merchant process(Merchant item) {
        if (merchantRepository.existsByEmail(item.getEmail())) {
            return null;
        }

        return item;
    }
}
