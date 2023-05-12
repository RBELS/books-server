package com.example.booksserver.rest.create;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.map.ImageMapper;
import com.example.booksserver.rest.request.AuthorPostRequest;
import com.example.booksserver.service.IContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
public class CreationController {
    private final IContentService contentService;
    private final ImageMapper imageMapper;

    public CreationController(IContentService contentService, ImageMapper imageMapper) {
        this.contentService = contentService;
        this.imageMapper = imageMapper;
    }

    @PostMapping(value = "/books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> createBook(
            @RequestParam("authors") List<Long> authorIdList,
            @RequestParam("name") String bookName,
            @RequestParam("releaseYear") Integer releaseYear,
            @RequestParam("price") Double price,
            @RequestParam("mainImage") MultipartFile mainImageFile,
            @RequestParam(value = "images", required = false) List<MultipartFile> contentImageFileList
    ) {
        List<AuthorDTO> authorDTOList = authorIdList.stream().map(contentService::getAuthorById).toList();

        BookImageDTO mainImageDTO;
        List<BookImageDTO> contentImageDTOList;

        try {
            mainImageDTO = imageMapper.fileToDto(mainImageFile, ImageType.MAIN);
            contentImageDTOList = imageMapper.fileToDto(contentImageFileList, ImageType.CONTENT);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        BookDTO dto = new BookDTO(
            null, bookName, releaseYear, price,
               mainImageDTO, contentImageDTOList, authorDTOList
        );
        contentService.createBook(dto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/authors", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createAuthor(
            @RequestBody AuthorPostRequest request
    ) {
        AuthorDTO newAuthorDTO = new AuthorDTO(null, request.getName());
        contentService.createAuthor(newAuthorDTO);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
