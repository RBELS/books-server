package com.example.booksserver.rest.create;

import com.example.booksserver.components.ErrorResponseFactory;
import com.example.booksserver.components.ResponseStatusWithBodyExceptionFactory;
import com.example.booksserver.config.AppConfig;
import com.example.booksserver.dto.Author;
import com.example.booksserver.dto.Book;
import com.example.booksserver.dto.BookImage;
import com.example.booksserver.dto.Stock;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.map.ImageMapper;
import com.example.booksserver.userstate.request.PostAuthorsRequest;
import com.example.booksserver.service.IContentService;
import com.example.booksserver.userstate.response.PostAuthorsResponse;
import com.example.booksserver.userstate.response.PostBooksResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
public class CreationController {
    private final IContentService contentService;
    private final ImageMapper imageMapper;
    private final String baseImageUrl;
    private final ResponseStatusWithBodyExceptionFactory exceptionFactory;

    public CreationController(IContentService contentService, ImageMapper imageMapper, AppConfig appConfig, ResponseStatusWithBodyExceptionFactory exceptionFactory) {
        this.contentService = contentService;
        this.imageMapper = imageMapper;
        this.baseImageUrl = appConfig.getServerAddress() + "/static/image/";
        this.exceptionFactory = exceptionFactory;
    }

    @PostMapping(value = "/books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostBooksResponse> createBook(
            @RequestParam("authors") List<Long> authorIdList,
            @RequestParam("name") String bookName,
            @RequestParam("releaseYear") Integer releaseYear,
            @RequestParam("price") BigDecimal price,
            @RequestParam("mainImage") MultipartFile mainImageFile,
            @RequestParam(value = "images", required = false) List<MultipartFile> contentImageFileList
    ) {
        List<Author> authorList = authorIdList.stream().map(contentService::getAuthorById).toList();

        BookImage mainImageDTO;
        List<BookImage> contentImageDTOList;

        try {
            if (mainImageFile.isEmpty()) {
                throw exceptionFactory.create(HttpStatus.BAD_REQUEST, ErrorResponseFactory.InternalErrorCode.BOOK_BAD_IMAGES);
            }
            mainImageDTO = imageMapper.fileToDto(mainImageFile, ImageType.MAIN);
            contentImageDTOList = imageMapper.fileToDto(contentImageFileList, ImageType.CONTENT);
        } catch (IOException e) {
            throw exceptionFactory.create(HttpStatus.INTERNAL_SERVER_ERROR, ErrorResponseFactory.InternalErrorCode.INTERNAL_ERROR_IMAGES);
        }

        Book dto = new Book(
                null, bookName, releaseYear, price,
                mainImageDTO, contentImageDTOList, authorList,
                new Stock(null, 10, 0, 0)
        );
        dto = contentService.createBook(dto);

        return new ResponseEntity<>(new PostBooksResponse(dto, baseImageUrl), HttpStatus.OK);
    }

    @PostMapping(value = "/authors", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostAuthorsResponse> createAuthor(
            @RequestBody PostAuthorsRequest request
    ) {
        Author newAuthor = new Author(null, request.getName());
        Author createdAuthor = contentService.createAuthor(newAuthor);

        return new ResponseEntity<>(new PostAuthorsResponse(createdAuthor), HttpStatus.OK);
    }
}
