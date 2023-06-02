package com.example.booksserver.map;

import com.example.booksserver.dto.Book;
import com.example.booksserver.dto.BookImage;
import com.example.booksserver.entity.image.BookImageEntity;
import com.example.booksserver.entity.image.ImageType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ImageMapper {
    public abstract BookImage entityToDto(BookImageEntity entity);
    public abstract List<BookImage> entityToDto(List<BookImageEntity> entityList);
    public abstract BookImageEntity dtoToEntity(BookImage dto);
    public abstract List<BookImageEntity> dtoToEntity(List<BookImage> dtoList);

    public BookImage fileToDto(MultipartFile imageFile, ImageType imageType) throws IOException {
        return new BookImage(null, imageType, imageFile.getBytes());
    }

    public List<BookImage> fileToDto(List<MultipartFile> imageFiles, ImageType imageType) throws IOException {
        List<BookImage> fileDtoList = new ArrayList<>();
        for (MultipartFile file : imageFiles) {
            fileDtoList.add(fileToDto(file, imageType));
        }
        return fileDtoList;
    }

    public BookImage mapMainImage(List<BookImageEntity> entityList) {
        for (BookImageEntity entity : entityList) {
            if (entity.getType() == ImageType.MAIN) {
                return entityToDto(entity);
            }
        }
        return null;
    }

    public List<BookImage> mapContentImages(List<BookImageEntity> entityList) {
        List<BookImage> dtoList = new ArrayList<>();
        for (BookImageEntity entity : entityList) {
            if (entity.getType() == ImageType.CONTENT) {
                dtoList.add(entityToDto(entity));
            }
        }
        return dtoList;
    }

    public List<BookImage> extractFromBookDto(Book dto) {
        List<BookImage> imageDTOList = new ArrayList<>();
        imageDTOList.add(dto.getMainFile());
        imageDTOList.addAll(dto.getImagesFileList());
        return imageDTOList;
    }
}
