package com.example.booksserver.map;

import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.entity.Book;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

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
    public abstract BookDTO entityToDto(Book entity);

    public abstract List<BookDTO> entityToDto(List<Book> entityList);

    @Mapping(target = "images", expression = "java(imageMapper.dtoToEntity(imageMapper.extractFromBookDto(dto)))")
    @Mapping(target = "stock", source = "stock")
    public abstract Book dtoToEntity(BookDTO dto);

    @AfterMapping
    public void afterDtoToEntity(BookDTO source, @MappingTarget Book target) {
        target.getImages().forEach(bookImage -> {
            if (Objects.isNull(bookImage.getBook())) {
                bookImage.setBook(target);
            }
        });
    }
}
