package com.example.start.rule;

import com.example.start.dto.Recommendation;
import com.example.start.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500Rule implements RecommendationRuleSet {

    private final RecommendationRepository repository;

    private static final String ID = "147f6a0f-3b91-413b-ab99-87f081d60d5a";
    private static final String NAME = "Invest 500";
    private static final String TEXT = "Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!";

    public Invest500Rule(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Recommendation> getRecommendationForUser(UUID userId) {
        boolean usesDebit = repository.userHasProductType(userId, "DEBIT");
        boolean usesInvest = repository.userHasProductType(userId, "INVEST");
        double savingDeposits = repository.getSumTransactions(userId, "SAVING", "DEPOSIT");

        if (usesDebit && !usesInvest && savingDeposits > 1000) {
            return Optional.of(new Recommendation(ID, NAME, TEXT));
        }
        return Optional.empty();
    }
}