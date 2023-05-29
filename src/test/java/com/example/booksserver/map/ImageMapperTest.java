package com.example.booksserver.map;

import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.image.BookImage;
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

    private final BookImage someEntityMain;
    private final BookImage someEntityContent;
    private final BookImageDTO someDtoMain;
    private final BookImageDTO someDtoContent;
    private final MultipartFile someFile;

    {
        someEntityMain = new BookImage()
                .setId(10L)
                .setBook(mock(Book.class))
                .setContent(new byte[1024])
                .setType(ImageType.MAIN);
        someEntityContent = new BookImage()
                .setId(10L)
                .setBook(mock(Book.class))
                .setContent(new byte[1024])
                .setType(ImageType.CONTENT);
        someDtoMain = new BookImageDTO()
                .setId(10L)
                .setContent(new byte[1024])
                .setType(ImageType.MAIN);
        someDtoContent = new BookImageDTO()
                .setId(10L)
                .setContent(new byte[1024])
                .setType(ImageType.CONTENT);
        someFile = new MockMultipartFile(
                "filename.ext", "filename.ext",
                "text/plain", new byte[1024]
        );
    }

    private void compareEntityToDto(BookImage entity, BookImageDTO dto) {
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getContent()).isEqualTo(dto.getContent());
        assertThat(entity.getType()).isEqualTo(dto.getType());
    }

    @Test
    void entityToDto() {
        BookImageDTO dto = imageMapper.entityToDto(someEntityMain);
        compareEntityToDto(someEntityMain, dto);
    }

    private void compareEntityToDtoList(List<BookImage> entityList, List<BookImageDTO> dtoList) {
        assertThat(entityList).hasSameSizeAs(dtoList);
        for (int i = 0;i < entityList.size();i++) {
            compareEntityToDto(entityList.get(i), dtoList.get(i));
        }
    }

    @Test
    void entityToDtoList() {
        List<BookImage> entityList = Arrays.asList(someEntityMain, someEntityMain, someEntityMain);
        List<BookImageDTO> dtoList = imageMapper.entityToDto(entityList);
        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void dtoToEntity() {
        BookImage entity = imageMapper.dtoToEntity(someDtoMain);
        compareEntityToDto(entity, someDtoMain);
    }

    @Test
    void dtoToEntityList() {
        List<BookImageDTO> dtoList = Arrays.asList(someDtoMain, someDtoMain, someDtoMain);
        List<BookImage> entityList = imageMapper.dtoToEntity(dtoList);
        compareEntityToDtoList(entityList, dtoList);
    }

    @Test
    void fileToDto() throws IOException {
        ImageType checkType = ImageType.CONTENT;
        BookImageDTO dto = imageMapper.fileToDto(someFile, checkType);
        assertThat(dto.getType()).isEqualTo(checkType);
        assertThat(dto.getContent()).isEqualTo(someFile.getBytes());
    }

    @Test
    void fileToDtoList() throws IOException {
        ImageType checkType = ImageType.MAIN;
        List<MultipartFile> fileList = Arrays.asList(someFile, someFile, someFile);
        List<BookImageDTO> dtoList = imageMapper.fileToDto(fileList, checkType);

        assertThat(fileList.size()).isEqualTo(dtoList.size());
        for (int i = 0;i < fileList.size();i++) {
            MultipartFile file = fileList.get(i);
            BookImageDTO dto = dtoList.get(i);

            assertThat(dto.getType()).isEqualTo(checkType);
            assertThat(dto.getContent()).isEqualTo(file.getBytes());
        }
    }

    @Test
    void mapMainImage() {
        List<BookImage> entityList = Arrays.asList(someEntityContent, someEntityContent, someEntityMain);
        BookImageDTO mainImageDTO = imageMapper.mapMainImage(entityList);

        compareEntityToDto(someEntityMain, mainImageDTO);
    }

    @Test
    void mapContentImages() {
        List<BookImage> entityList = Arrays.asList(someEntityContent, someEntityContent, someEntityMain);
        List<BookImageDTO> contentDtoList = imageMapper.mapContentImages(entityList);

        List<BookImage> contentEntityList = entityList.stream()
                .filter(bookImage -> bookImage.getType().equals(ImageType.CONTENT))
                .toList();

        compareEntityToDtoList(contentEntityList, contentDtoList);
    }

    @Test
    void extractFromBookDto() {
        List<BookImageDTO> contentImageDtoList = Arrays.asList(
                someDtoContent, someDtoContent
        );

        BookDTO bookDTO = new BookDTO()
                .setMainFile(someDtoMain)
                .setImagesFileList(contentImageDtoList);

        List<BookImageDTO> allImageDTOList = imageMapper.extractFromBookDto(bookDTO);
        assertThat(allImageDTOList.size()).isEqualTo(bookDTO.getImagesFileList().size() + 1);
        assertThat(allImageDTOList).containsExactlyInAnyOrder(someDtoMain, someDtoContent, someDtoContent);
    }
}