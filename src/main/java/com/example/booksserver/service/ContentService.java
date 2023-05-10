package com.example.booksserver.service;

import com.example.booksserver.dto.AuthorDTO;
import com.example.booksserver.dto.BookDTO;
import com.example.booksserver.dto.BookImageDTO;
import com.example.booksserver.entity.Author;
import com.example.booksserver.entity.Book;
import com.example.booksserver.entity.image.BookImage;
import com.example.booksserver.entity.image.ImageType;
import com.example.booksserver.repository.AuthorRepository;
import com.example.booksserver.repository.BookRepository;
import com.example.booksserver.userstate.filters.AuthorsFilters;
import com.example.booksserver.userstate.filters.BooksFilters;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContentService implements IContentService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public ContentService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }


    @Transactional
    public List<BookDTO> getBooks(BooksFilters filters) {
        int page = Objects.isNull(filters.getPage()) ? 0 : filters.getPage();
        int count = Objects.isNull(filters.getCount()) ? BooksFilters.DEFAULT_COUNT_PER_PAGE : filters.getCount();
        long minPrice = filters.getMinPrice() == null ? bookRepository.findMinPrice() : filters.getMinPrice();
        long maxPrice = filters.getMaxPrice() == null ? bookRepository.findMaxPrice() : filters.getMaxPrice();

        List<Book> entityList;
        if (filters.getAuthorId() == null) {
            entityList = bookRepository.findAllByPriceBetween(minPrice, maxPrice, PageRequest.of(page, count)).stream().toList();
        } else {
            entityList = bookRepository
                    .findAllByAuthors_idAndPriceBetween(
                            filters.getAuthorId(),
                            minPrice, maxPrice,
                            PageRequest.of(page, count)
                    ).getContent();
        }

        return entityList.stream().map(bookEntity -> {
            BookDTO dto = new BookDTO();
            dto.setBookName(bookEntity.getName());
            dto.setPrice(bookEntity.getPrice());
            dto.setId(bookEntity.getId());
            dto.setReleaseYear(bookEntity.getReleaseYear());
            dto.setAuthorList(bookEntity.getAuthors().stream().map(author -> new AuthorDTO(author.getId(), author.getName())).toList());
            bookEntity.getImages().forEach(bookImage -> {
                BookImageDTO bookImageDTO = new BookImageDTO(bookImage.getId(), bookImage.getType(), bookImage.getContent());
                if (bookImage.getType() == ImageType.MAIN) {
                    dto.setMainFile(bookImageDTO);
                } else {
                    dto.getImagesFileList().add(bookImageDTO);
                }
            });
            return dto;
        }).toList();
    }

    // TESTS ONLY
    public void saveAuthor(Author author) {
        authorRepository.save(author);
    }

    // TESTS ONLY
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    private final Sort authorsAscSort = Sort.by("name", "id").ascending();

    // Return DTO?
    public List<AuthorDTO> getAuthors(AuthorsFilters filters) {
        List<Author> entityList;
        if (Objects.isNull(filters.getPage()) || Objects.isNull(filters.getCount())) {
            entityList = authorRepository.findAll(authorsAscSort);
        }
        entityList = authorRepository.findAll(
                PageRequest.of(filters.getPage(), filters.getCount(), authorsAscSort)
        ).getContent();

        return entityList.stream().map(authorEntity -> new AuthorDTO(authorEntity.getId(), authorEntity.getName())).toList();
    }

    public AuthorDTO getAuthorById(Long authorId) {
        Author entity = authorRepository.findAuthorById(authorId);
        AuthorDTO resultDTO = null;
        if (!Objects.isNull(entity)) {
            resultDTO = new AuthorDTO(entity.getId(), entity.getName());
        }
        return resultDTO;
    }

    public List<Double> getMinMaxPrices() {
        return Arrays.asList(bookRepository.findMinPrice() / 100.0, bookRepository.findMaxPrice() / 100.0);
    }



    private boolean isAuthorValid(AuthorDTO authorDTO) {
        return !authorDTO.getName().isBlank();
    }

    public boolean createAuthor(AuthorDTO newAuthorDTO) {
        boolean authorValid = isAuthorValid(newAuthorDTO);
        if (!authorValid) {
            return false;
        }

        Author authorEntity = new Author();
        authorEntity.setName(newAuthorDTO.getName());
        authorRepository.save(authorEntity);
        return true;
    }

    private boolean isBookValid(BookDTO bookDTO) {
        if (bookDTO.getBookName().isBlank() || bookDTO.getAuthorList().stream().anyMatch(Objects::isNull)) {
            return false;
        }
        return true;
    }

    public boolean createBook(BookDTO newBookDTO)  {
        if (!isBookValid(newBookDTO)) {
            return false;
        }
        Book entity = new Book();
        entity.setName(newBookDTO.getBookName());
        entity.setPrice((long) (newBookDTO.getPrice() * 100.0));
        entity.setReleaseYear(newBookDTO.getReleaseYear());

        entity.getImages().add(new BookImage(null, ImageType.MAIN, newBookDTO.getMainFile().getContent(), entity));
        newBookDTO.getImagesFileList().forEach(src -> {
            entity.getImages().add(new BookImage(null, ImageType.CONTENT, src.getContent(), entity));
        });

        entity.setAuthors(newBookDTO.getAuthorList().stream()
                .map(authorDTO -> authorRepository.findAuthorById(authorDTO.getId()))
                .collect(Collectors.toSet()));

        bookRepository.save(entity);

        return true;
    }
}
