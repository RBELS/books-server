package com.example.booksserver.map;

import com.example.booksserver.dto.Book;
import com.example.booksserver.dto.BookImage;
import com.example.booksserver.entity.BookEntity;
import com.example.booksserver.entity.image.BookImageEntity;
import com.example.booksserver.entity.image.ImageType;
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

    private void compareEntityToDto(BookImageEntity entity, BookImage dto) {
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getContent()).isEqualTo(dto.getContent());
        assertThat(entity.getType()).isEqualTo(dto.getType());
    }

    @Test
    void entityToDto() {
        BookImage dto = imageMapper.entityToDto(someEntityMain);
        compareEntityToDto(someEntityMain, dto);
    }

    private void compareEntityToDtoList(List<BookImageEntity> entityList, List<BookImage> dtoList) {
        assertThat(entityList).hasSameSizeAs(dtoList);
        for (int i = 0;i < entityList.size();i++) {
            compareEntityToDto(entityList.get(i), dtoList.get(i));
        }
    }

    @Test
    void entityToDtoList() {
        List<BookImageEntity> entityList = Arrays.asList(someEntityMain, someEntityMain, someEntityMain);
        List<BookImage> dtoList = imageMapper.entityToDto(entityList);
        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void dtoToEntity() {
        BookImageEntity entity = imageMapper.dtoToEntity(someDtoMain);
        compareEntityToDto(entity, someDtoMain);
    }

    @Test
    void dtoToEntityList() {
        List<BookImage> dtoList = Arrays.asList(someDtoMain, someDtoMain, someDtoMain);
        List<BookImageEntity> entityList = imageMapper.dtoToEntity(dtoList);
        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void fileToDto() throws IOException {
        ImageType checkType = ImageType.CONTENT;
        BookImage dto = imageMapper.fileToDto(someFile, checkType);
        assertThat(dto.getType()).isEqualTo(checkType);
        assertThat(dto.getContent()).isEqualTo(someFile.getBytes());
    }

    @Test
    void fileToDtoList() throws IOException {
        ImageType checkType = ImageType.MAIN;
        List<MultipartFile> fileList = Arrays.asList(someFile, someFile, someFile);
        List<BookImage> dtoList = imageMapper.fileToDto(fileList, checkType);

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

        compareEntityToDto(someEntityMain, mainImageDTO);
    }

    @Test
    void mapContentImages() {
        List<BookImageEntity> entityList = Arrays.asList(someEntityContent, someEntityContent, someEntityMain);
        List<BookImage> contentDtoList = imageMapper.mapContentImages(entityList);

        List<BookImageEntity> contentEntityList = entityList.stream()
                .filter(bookImage -> bookImage.getType().equals(ImageType.CONTENT))
                .toList();

        compareEntityToDtoList(contentEntityList, contentDtoList);
    }

    @Test
    void extractFromBookDto() {
        List<BookImage> contentImageDtoList = Arrays.asList(
                someDtoContent, someDtoContent
        );

        Book book = new Book()
                .setMainFile(someDtoMain)
                .setImagesFileList(contentImageDtoList);

        List<BookImage> allImageDTOList = imageMapper.extractFromBookDto(book);
        assertThat(allImageDTOList.size()).isEqualTo(book.getImagesFileList().size() + 1);
        assertThat(allImageDTOList).containsExactlyInAnyOrder(someDtoMain, someDtoContent, someDtoContent);
    }
}