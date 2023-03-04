package com.emailattachment.repository;

import com.emailattachment.entity.UploadDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface UploadRepository extends JpaRepository<UploadDetails, Long> {

    @Query("SELECT p FROM UploadDetails p WHERE " +
            "p.isabend LIKE CONCAT('%',:keyword, '%')" +
            "Or p.sub_type LIKE CONCAT('%', :keyword, '%')")
    List<UploadDetails> findByAccountNumber(@Param("keyword") String keyword);
}
