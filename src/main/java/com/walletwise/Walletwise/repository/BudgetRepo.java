package com.walletwise.Walletwise.repository;

import com.walletwise.Walletwise.entity.Budget;
import com.walletwise.Walletwise.entity.User;
import com.walletwise.Walletwise.enums.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepo extends MongoRepository<Budget, String> {
    List<Budget> findByUserAndMonth(User user, String month);

    Optional<Budget> findByUserAndCategoryAndMonth(User user, Category category, String month);
}
