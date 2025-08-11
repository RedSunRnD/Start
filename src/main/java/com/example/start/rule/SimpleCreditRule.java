package com.example.start.rule;

import com.example.start.dto.Recommendation;
import com.example.start.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
public class SimpleCreditRule implements RecommendationRuleSet {

    private final RecommendationRepository repository;

    private static final String ID = "ab138afb-f3ba-4a93-b74f-0fcee86d447f";
    private static final String NAME = "Простой кредит";
    private static final String TEXT = """
        Откройте мир выгодных кредитов с нами!
        Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.
        Почему выбирают нас:
        Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.
        Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.
        Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.
        Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!
        """;
    ;

    public SimpleCreditRule(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Recommendation> getRecommendationForUser(UUID userId) {
        boolean usesCredit = repository.userHasProductType(userId, "CREDIT");
        if (usesCredit) return Optional.empty();

        double debitDeposits = repository.getSumTransactions(userId, "DEBIT", "DEPOSIT");
        double debitWithdrawals = repository.getSumTransactions(userId, "DEBIT", "WITHDRAWAL");

        if (debitDeposits > debitWithdrawals && debitWithdrawals > 100_000) {
            return Optional.of(new Recommendation(ID, NAME, TEXT));
        }
        return Optional.empty();
    }
}