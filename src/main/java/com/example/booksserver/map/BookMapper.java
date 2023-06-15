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

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, ImageMapper.class, StockMapper.class})
public abstract class BookMapper {

    @Autowired
    public ImageMapper imageMapper;

    @Autowired
    public AuthorMapper authorMapper;

    @Mapping(target = "mainFile", expression = "java(imageMapper.mapMainImage(entity.getImages()))")
    @Mapping(target = "imagesFileList", expression = "java(imageMapper.mapContentImages(entity.getImages()))")
    @Mapping(target = "stock", source = "stock")
    public abstract Book entityToDto(BookEntity entity);

    public abstract List<Book> entityToDto(List<BookEntity> entityList);

    @Mapping(target = "images", expression = "java(imageMapper.dtoToEntity(imageMapper.extractFromBookDto(dto)))")
    @Mapping(target = "stock", source = "stock")
    public abstract BookEntity dtoToEntity(Book dto);

    @AfterMapping
    public void afterDtoToEntity(Book source, @MappingTarget BookEntity target) {
        target.getImages().forEach(bookImage -> {
            if (Objects.isNull(bookImage.getBook())) {
                bookImage.setBook(target);
            }
        });
    }

    public Page<Book> entityToDtoPage(Page<BookEntity> books) {
        return books.map(this::entityToDto);
    }
}
