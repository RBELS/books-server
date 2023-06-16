package com.example.booksserver.map;

import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.service.BookImage;
import com.example.booksserver.model.entity.BookImageEntity;
import com.example.booksserver.model.entity.ImageType;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ImageMapper {
    public abstract BookImage entityToService(BookImageEntity entity);
    public abstract List<BookImage> entityToService(List<BookImageEntity> entityList);
    public abstract BookImageEntity serviceToEntity(BookImage serviceObj);
    public abstract List<BookImageEntity> serviceToEntity(List<BookImage> serviceObjList);

    public BookImage fileToService(MultipartFile imageFile, ImageType imageType) throws IOException {
        return new BookImage(null, imageType, imageFile.getBytes());
    }

    public List<BookImage> fileToService(List<MultipartFile> imageFiles, ImageType imageType) throws IOException {
        List<BookImage> fileServiceObjList = new ArrayList<>();
        if (!Objects.isNull(imageFiles)) {
            for (MultipartFile file : imageFiles) {
                fileServiceObjList.add(fileToService(file, imageType));
            }
        }
        return fileServiceObjList;
    }

    public BookImage mapMainImage(List<BookImageEntity> entityList) {
        for (BookImageEntity entity : entityList) {
            if (entity.getType() == ImageType.MAIN) {
                return entityToService(entity);
            }
        }
        return null;
    }

    public List<BookImage> mapContentImages(List<BookImageEntity> entityList) {
        List<BookImage> serviceObjList = new ArrayList<>();
        for (BookImageEntity entity : entityList) {
            if (entity.getType() == ImageType.CONTENT) {
                serviceObjList.add(entityToService(entity));
            }
        }
        return serviceObjList;
    }

    public List<BookImage> extractFromBookServiceObj(Book serviceObj) {
        List<BookImage> serviceObjList = new ArrayList<>();
        serviceObjList.add(serviceObj.getMainFile());
        serviceObjList.addAll(serviceObj.getImagesFileList());
        return serviceObjList;
    }
}
