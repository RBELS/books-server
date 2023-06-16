package com.example.booksserver.map;

import com.example.booksserver.model.service.Book;
import com.example.booksserver.model.service.BookImage;
import com.example.booksserver.model.entity.BookEntity;
import com.example.booksserver.model.entity.BookImageEntity;
import com.example.booksserver.model.entity.ImageType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ImageMapperTest {
    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ImageMapper imageMapper;

    private final BookImageEntity someEntityMain;
    private final BookImageEntity someEntityContent;
    private final BookImage someDtoMain;
    private final BookImage someDtoContent;
    private final MultipartFile someFile;

    {
        someEntityMain = new BookImageEntity()
                .setId(10L)
                .setBook(mock(BookEntity.class))
                .setContent(new byte[1024])
                .setType(ImageType.MAIN);
        someEntityContent = new BookImageEntity()
                .setId(10L)
                .setBook(mock(BookEntity.class))
                .setContent(new byte[1024])
                .setType(ImageType.CONTENT);
        someDtoMain = new BookImage()
                .setId(10L)
                .setContent(new byte[1024])
                .setType(ImageType.MAIN);
        someDtoContent = new BookImage()
                .setId(10L)
                .setContent(new byte[1024])
                .setType(ImageType.CONTENT);
        someFile = new MockMultipartFile(
                "filename.ext", "filename.ext",
                "text/plain", new byte[1024]
        );
    }

    private void compareEntityToService(BookImageEntity entity, BookImage serviceObj) {
        assertThat(entity.getId()).isEqualTo(serviceObj.getId());
        assertThat(entity.getContent()).isEqualTo(serviceObj.getContent());
        assertThat(entity.getType()).isEqualTo(serviceObj.getType());
    }

    @Test
    void entityToService() {
        BookImage serviceObj = imageMapper.entityToService(someEntityMain);
        compareEntityToService(someEntityMain, serviceObj);
    }

    private void compareEntityToServiceList(List<BookImageEntity> entityList, List<BookImage> serviceList) {
        assertThat(entityList).hasSameSizeAs(serviceList);
        for (int i = 0;i < entityList.size();i++) {
            compareEntityToService(entityList.get(i), serviceList.get(i));
        }
    }

    @Test
    void entityToServiceList() {
        List<BookImageEntity> entityList = Arrays.asList(someEntityMain, someEntityMain, someEntityMain);
        List<BookImage> dtoList = imageMapper.entityToService(entityList);
        compareEntityToServiceList(entityList, dtoList);
    }

    @Test
    void serviceToEntity() {
        BookImageEntity entity = imageMapper.serviceToEntity(someDtoMain);
        compareEntityToService(entity, someDtoMain);
    }

    @Test
    void serviceToEntityList() {
        List<BookImage> dtoList = Arrays.asList(someDtoMain, someDtoMain, someDtoMain);
        List<BookImageEntity> entityList = imageMapper.serviceToEntity(dtoList);
        compareEntityToServiceList(entityList, dtoList);
    }

    @Test
    void fileToService() throws IOException {
        ImageType checkType = ImageType.CONTENT;
        BookImage dto = imageMapper.fileToService(someFile, checkType);
        assertThat(dto.getType()).isEqualTo(checkType);
        assertThat(dto.getContent()).isEqualTo(someFile.getBytes());
    }

    @Test
    void fileToServiceList() throws IOException {
        ImageType checkType = ImageType.MAIN;
        List<MultipartFile> fileList = Arrays.asList(someFile, someFile, someFile);
        List<BookImage> dtoList = imageMapper.fileToService(fileList, checkType);

        assertThat(fileList.size()).isEqualTo(dtoList.size());
        for (int i = 0;i < fileList.size();i++) {
            MultipartFile file = fileList.get(i);
            BookImage dto = dtoList.get(i);

            assertThat(dto.getType()).isEqualTo(checkType);
            assertThat(dto.getContent()).isEqualTo(file.getBytes());
        }
    }

    @Test
    void mapMainImage() {
        List<BookImageEntity> entityList = Arrays.asList(someEntityContent, someEntityContent, someEntityMain);
        BookImage mainImageDTO = imageMapper.mapMainImage(entityList);

        compareEntityToService(someEntityMain, mainImageDTO);
    }

    @Test
    void mapContentImages() {
        List<BookImageEntity> entityList = Arrays.asList(someEntityContent, someEntityContent, someEntityMain);
        List<BookImage> contentDtoList = imageMapper.mapContentImages(entityList);

        List<BookImageEntity> contentEntityList = entityList.stream()
                .filter(bookImage -> bookImage.getType().equals(ImageType.CONTENT))
                .toList();

        compareEntityToServiceList(contentEntityList, contentDtoList);
    }

    @Test
    void extractFromBookService() {
        List<BookImage> contentImageDtoList = Arrays.asList(
                someDtoContent, someDtoContent
        );

        Book book = new Book()
                .setMainFile(someDtoMain)
                .setImagesFileList(contentImageDtoList);

        List<BookImage> allImageDTOList = imageMapper.extractFromBookServiceObj(book);
        assertThat(allImageDTOList.size()).isEqualTo(book.getImagesFileList().size() + 1);
        assertThat(allImageDTOList).containsExactlyInAnyOrder(someDtoMain, someDtoContent, someDtoContent);
    }
}