package com.walletwise.Walletwise.repository;

import com.walletwise.Walletwise.entity.Transaction;
import com.walletwise.Walletwise.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends MongoRepository<Transaction, String> {
    public Optional<Transaction> findById(String id);
    Page<Transaction> findByUser(User user, Pageable pageable);
}
