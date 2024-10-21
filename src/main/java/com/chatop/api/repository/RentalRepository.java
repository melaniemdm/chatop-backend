package com.chatop.api.repository;

import com.chatop.api.model.Rental;
import org.springframework.data.repository.CrudRepository;

public interface RentalRepository  extends CrudRepository<Rental, Long> {
}
