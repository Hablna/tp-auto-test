package com.alicedeveloppementweb.tp_automatisation_tests.service;

import com.alicedeveloppementweb.tp_automatisation_tests.model.Book;
import com.alicedeveloppementweb.tp_automatisation_tests.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@SpringBootTest
public class BookServiceIntegrationTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    void should_get_all_books(){
        //Arra
        bookRepository.save(new Book(null, "1984", "Orwell", 1948));
        bookRepository.save(new Book(null, "Le Meilleur des Mondes", "Huxley", 1932));

        //Act
        List<Book> books = bookRepository.findAll();

        //Assert
        assertEquals(2, books.size());
        assertEquals("1984", books.getFirst().getTitre());
        assertEquals("Huxley", books.getLast().getAuteur());
    }
}
