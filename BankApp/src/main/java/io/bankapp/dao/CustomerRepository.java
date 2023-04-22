package io.bankapp.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.bankapp.model.Accounts;
import io.bankapp.model.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

	Optional<Customer> findById(int acctID);

	void deleteById(int acctID);

}
