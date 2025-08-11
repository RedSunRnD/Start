package com.example.start.rule;

import com.example.start.dto.Recommendation;
import com.example.start.repository.RecommendationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRule implements RecommendationRuleSet {

    private final RecommendationRepository repository;

    private static final String ID = "59efc529-2fff-41af-baff-90ccd7402925";
    private static final String NAME = "Top Saving";
    private static final String TEXT = """
        Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!

        Преимущества «Копилки»:

        Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.

        Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.

        Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.

        Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!
        """;

    public TopSavingRule(RecommendationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Recommendation> getRecommendationForUser(UUID userId) {
        boolean usesDebit = repository.userHasProductType(userId, "DEBIT");
        double debitDeposits = repository.getSumTransactions(userId, "DEBIT", "DEPOSIT");
        double savingDeposits = repository.getSumTransactions(userId, "SAVING", "DEPOSIT");
        double debitWithdrawals = repository.getSumTransactions(userId, "DEBIT", "WITHDRAWAL");

        boolean depositsCondition = (debitDeposits >= 50000 || savingDeposits >= 50000);
        boolean depositsGreaterThanWithdrawals = debitDeposits > debitWithdrawals;

        if (usesDebit && depositsCondition && depositsGreaterThanWithdrawals) {
            return Optional.of(new Recommendation(ID, NAME, TEXT));
        }
        return Optional.empty();
    }
}