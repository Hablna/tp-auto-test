package com.alicedeveloppementweb.tp_automatisation_tests.service;

import com.alicedeveloppementweb.tp_automatisation_tests.model.Book;
import com.alicedeveloppementweb.tp_automatisation_tests.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookService service;

    @Test
    void should_create_book() {
        // ARRANGE
        Book book = new Book(null, "1984", "Orwell", 1948);

        // ACT
        when(repository.save(book)).thenReturn(
                new Book(1L, "1984", "Orwell", 1948)
        );

        Book saved = service.create(book);

        // ASSERT
        assertNotNull(saved.getId());
        assertEquals("1984", saved.getTitre());
        verify(repository).save(book);
    }

    @Test
    void should_update_book() {
        Book book = new Book(1L, "1984", "Orwell", 1948);
        when(repository.save(book)).thenReturn(book);

        Book saved = service.update(book.getId(), book);

        assertEquals(1L, saved.getId());
        assertEquals("1984", saved.getTitre());
        verify(repository).save(book);
    }

    @Test
    void should_get_all_book(){
        //Arrange, les données que le repository(mocké) va retourner
        List<Book> books = List.of(
                new Book(1L, "1984", "Orwell", 1948),
                new Book(2L, "Le Meilleur des Mondes", "Huxley", 1932)
        );

        when(repository.findAll()).thenReturn(books);

        //Act
        List<Book> booksFound = service.getAll();

        //Assert
        assertEquals(2, booksFound.size());
        assertEquals(1948, booksFound.getFirst().getAnnee());
        assertEquals("Huxley", booksFound.getLast().getAuteur());
        verify(repository).findAll();
    }
}
