package com.example.booksserver.repository;

import com.example.booksserver.entity.AuthorEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends PagingAndSortingRepository<AuthorEntity, Long>, ListCrudRepository<AuthorEntity, Long> {
    @NonNull
    List<AuthorEntity> findAll(@NonNull Sort sort);
    @NonNull
    Page<AuthorEntity> findAll(@NonNull Pageable pageable);
}
