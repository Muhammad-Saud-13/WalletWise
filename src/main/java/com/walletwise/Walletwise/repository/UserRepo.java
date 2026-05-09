package com.walletwise.Walletwise.repository;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.walletwise.Walletwise.entity.User;

@Repository
public interface UserRepo extends MongoRepository<User, String>{

    public Optional<User> findByEmail(String email);
}