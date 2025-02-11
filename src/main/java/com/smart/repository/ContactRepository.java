package com.smart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.smart.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

// pagination but Custom Method 

//	current Page-page
//	contact per-page
	@Query("from Contact as c where c.user.id=:userid")
	public Page<Contact> findContactsByUser(@Param("userid") int userId, Pageable pageable);
}
