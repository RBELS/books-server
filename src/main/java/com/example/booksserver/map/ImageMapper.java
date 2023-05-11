package com.example.booksserver.map;

import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.entity.image.ImageType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ImageMapper {
    public abstract BookImageDTO entityToDto(BookImage entity);
    public abstract List<BookImageDTO> entityToDto(List<BookImage> entityList);
    public abstract BookImage dtoToEntity(BookImageDTO dto);
    public abstract List<BookImage> dtoToEntity(List<BookImageDTO> dtoList);

    public BookImageDTO mapMainImage(List<BookImage> entityList) {
        for (BookImage entity : entityList) {
            if (entity.getType() == ImageType.MAIN) {
                return entityToDto(entity);
            }
        }
        return null;
    }

    public List<BookImageDTO> mapContentImages(List<BookImage> entityList) {
        List<BookImageDTO> dtoList = new ArrayList<>();
        for (BookImage entity : entityList) {
            if (entity.getType() == ImageType.CONTENT) {
                dtoList.add(entityToDto(entity));
            }
        }
        return dtoList;
    }

    public List<BookImageDTO> extractFromBookDto(BookDTO dto) {
        List<BookImageDTO> imageDTOList = new ArrayList<>();
        imageDTOList.add(dto.getMainFile());
        imageDTOList.addAll(dto.getImagesFileList());
        return imageDTOList;
    }
}
