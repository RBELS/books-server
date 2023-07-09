package com.example.booksserver.map;

import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.entity.BookEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {AuthorMapper.class, StockMapper.class})
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
