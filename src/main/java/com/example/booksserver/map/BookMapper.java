package com.example.booksserver.map;

import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.entity.BookEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, StockMapper.class})
public abstract class BookMapper {

    @Autowired
    public AuthorMapper authorMapper;

    @Mapping(target = "stock", source = "stock")
    public abstract Book entityToService(BookEntity entity);

    public abstract List<Book> entityToService(List<BookEntity> entityList);

    @Mapping(target = "stock", source = "stock")
    public abstract BookEntity serviceToEntity(Book serviceObj);

    public Page<Book> entityToServicePage(Page<BookEntity> books) {
        return books.map(this::entityToService);
    }
}
