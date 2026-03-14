package tests;

import config.DriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.WebDriver;
import pages.BooksPage;
import pages.LoginPage;

public class BookCRUDTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private BooksPage booksPage;

    // Données de test
    private static final String USERNAME = "test";
    private static final String PASSWORD = "password";
    private static final String BOOK_TITLE = "Clean Code";
    private static final String BOOK_AUTHOR = "Robert C. Martin";
    private static final String BOOK_ISBN = "978-0132350884";
    private static final String BOOK_DESCRIPTION = "Un guide pour écrire du code propre et maintenable";

    // Données modifiées
    private static final String UPDATED_TITLE = "The Pragmatic Programmer";
    private static final String UPDATED_AUTHOR = "David Thomas & Andrew Hunt";
    private static final String UPDATED_ISBN = "978-0201616224";
    private static final String UPDATED_DESCRIPTION = "Votre guide vers une maîtrise pragmatique";

    @BeforeEach
    void setup() {
        // Initialiser le driver et les pages
        driver = DriverFactory.createDriver();
        loginPage = new LoginPage(driver);
        booksPage = new BooksPage(driver);

        // Se connecter avant chaque test
        loginPage.open();
        loginPage.login(USERNAME, PASSWORD);

        // Attendre la redirection vers la page d'accueil
        loginPage.isRedirectedToHome();

        // Naviguer vers la page des livres
        booksPage.open();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Test complet du cycle CRUD: Création -> Consultation -> Modification -> Suppression
     * C'est le test principal qui valide tout le cycle utilisateur
     */
    @Test
    @DisplayName("Cycle complet CRUD: Créer, consulter, modifier et supprimer un livre")
    void testCompleteCRUDCycle() {
        // ÉTAPE 1: CREATE (Création d'un livre)
        System.out.println("=== Étape 1: Création du livre ===");
        int initialCount = booksPage.getBooksCount();
        booksPage.createBook(BOOK_TITLE, BOOK_AUTHOR, BOOK_ISBN, BOOK_DESCRIPTION);

        // Vérifier que le message de succès est affiché
        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isSuccessMessageDisplayed(),
                "Un message de succès devrait être affiché après la création du livre"
        );

        // Attendre que le message disparaisse et vérifier que le livre est dans la liste
        try {
            Thread.sleep(1000); // Attendre l'ajout à la liste
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isBookPresent(BOOK_TITLE),
                "Le livre '" + BOOK_TITLE + "' devrait être présent dans la liste"
        );

        int countAfterCreation = booksPage.getBooksCount();
        org.junit.jupiter.api.Assertions.assertEquals(
                initialCount + 1,
                countAfterCreation,
                "Le nombre de livres devrait augmenter de 1 après la création"
        );

        // ÉTAPE 2: READ (Consultation du livre)
        System.out.println("=== Étape 2: Consultation du livre ===");
        booksPage.viewBook(BOOK_TITLE);

        // Récupérer et vérifier les informations du livre
        BooksPage.BookInfo bookInfo = booksPage.getBookInfo(BOOK_TITLE);
        org.junit.jupiter.api.Assertions.assertNotNull(
                bookInfo,
                "Les informations du livre devraient être récupérées"
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                BOOK_TITLE,
                bookInfo.title,
                "Le titre du livre consulté devrait correspondre"
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                BOOK_AUTHOR,
                bookInfo.author,
                "L'auteur du livre consulté devrait correspondre"
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                BOOK_ISBN,
                bookInfo.isbn,
                "L'ISBN du livre consulté devrait correspondre"
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                BOOK_DESCRIPTION,
                bookInfo.description,
                "La description du livre consulté devrait correspondre"
        );

        // ÉTAPE 3: UPDATE (Modification du livre)
        System.out.println("=== Étape 3: Modification du livre ===");
        booksPage.editBook(BOOK_TITLE, UPDATED_TITLE, UPDATED_AUTHOR, UPDATED_ISBN, UPDATED_DESCRIPTION);

        // Vérifier que le message de succès est affiché
        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isSuccessMessageDisplayed(),
                "Un message de succès devrait être affiché après la modification du livre"
        );

        // Attendre que la modification soit appliquée
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Vérifier que le livre modifié existe et le livre original n'existe plus
        org.junit.jupiter.api.Assertions.assertFalse(
                booksPage.isBookPresent(BOOK_TITLE),
                "L'ancien titre du livre ne devrait plus exister"
        );

        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isBookPresent(UPDATED_TITLE),
                "Le nouveau titre du livre devrait exister"
        );

        // Vérifier les informations mises à jour
        BooksPage.BookInfo updatedBookInfo = booksPage.getBookInfo(UPDATED_TITLE);
        org.junit.jupiter.api.Assertions.assertNotNull(
                updatedBookInfo,
                "Les informations mises à jour du livre devraient être récupérées"
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                UPDATED_TITLE,
                updatedBookInfo.title,
                "Le titre mis à jour devrait correspondre"
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                UPDATED_AUTHOR,
                updatedBookInfo.author,
                "L'auteur mis à jour devrait correspondre"
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                UPDATED_ISBN,
                updatedBookInfo.isbn,
                "L'ISBN mis à jour devrait correspondre"
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                UPDATED_DESCRIPTION,
                updatedBookInfo.description,
                "La description mise à jour devrait correspondre"
        );

        // ÉTAPE 4: DELETE (Suppression du livre)
        System.out.println("=== Étape 4: Suppression du livre ===");
        int countBeforeDeletion = booksPage.getBooksCount();
        booksPage.deleteBook(UPDATED_TITLE);

        // Vérifier que le message de succès est affiché
        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isSuccessMessageDisplayed(),
                "Un message de succès devrait être affiché après la suppression du livre"
        );

        // Attendre que la suppression soit appliquée
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Vérifier que le livre n'existe plus
        org.junit.jupiter.api.Assertions.assertFalse(
                booksPage.isBookPresent(UPDATED_TITLE),
                "Le livre supprimé ne devrait plus être dans la liste"
        );

        int countAfterDeletion = booksPage.getBooksCount();
        org.junit.jupiter.api.Assertions.assertEquals(
                countBeforeDeletion - 1,
                countAfterDeletion,
                "Le nombre de livres devrait diminuer de 1 après la suppression"
        );

        System.out.println("=== Cycle CRUD complété avec succès ===");
    }

    /**
     * Test de création d'un livre
     */
    @Test
    @DisplayName("Créer un nouveau livre")
    void testCreateBook() {
        booksPage.createBook(BOOK_TITLE, BOOK_AUTHOR, BOOK_ISBN, BOOK_DESCRIPTION);

        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isSuccessMessageDisplayed(),
                "Un message de succès devrait être affiché"
        );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isBookPresent(BOOK_TITLE),
                "Le livre créé devrait être dans la liste"
        );

        // Nettoyage: supprimer le livre créé
        booksPage.deleteBook(BOOK_TITLE);
    }

    /**
     * Test de consultation d'un livre
     */
    @Test
    @DisplayName("Consulter les détails d'un livre")
    void testViewBook() {
        // Créer un livre d'abord
        booksPage.createBook(BOOK_TITLE, BOOK_AUTHOR, BOOK_ISBN, BOOK_DESCRIPTION);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Consulter le livre
        booksPage.viewBook(BOOK_TITLE);
        BooksPage.BookInfo bookInfo = booksPage.getBookInfo(BOOK_TITLE);

        org.junit.jupiter.api.Assertions.assertNotNull(bookInfo, "Les informations du livre devraient être visibles");
        org.junit.jupiter.api.Assertions.assertEquals(BOOK_TITLE, bookInfo.title);
        org.junit.jupiter.api.Assertions.assertEquals(BOOK_AUTHOR, bookInfo.author);

        // Nettoyage
        booksPage.deleteBook(BOOK_TITLE);
    }

    /**
     * Test de modification d'un livre
     */
    @Test
    @DisplayName("Modifier un livre existant")
    void testUpdateBook() {
        // Créer un livre d'abord
        booksPage.createBook(BOOK_TITLE, BOOK_AUTHOR, BOOK_ISBN, BOOK_DESCRIPTION);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Modifier le livre
        booksPage.editBook(BOOK_TITLE, UPDATED_TITLE, UPDATED_AUTHOR, UPDATED_ISBN, UPDATED_DESCRIPTION);

        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isSuccessMessageDisplayed(),
                "Un message de succès devrait être affiché après la modification"
        );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Vérifier la modification
        BooksPage.BookInfo updatedBookInfo = booksPage.getBookInfo(UPDATED_TITLE);
        org.junit.jupiter.api.Assertions.assertNotNull(updatedBookInfo);
        org.junit.jupiter.api.Assertions.assertEquals(UPDATED_TITLE, updatedBookInfo.title);
        org.junit.jupiter.api.Assertions.assertEquals(UPDATED_AUTHOR, updatedBookInfo.author);

        // Nettoyage
        booksPage.deleteBook(UPDATED_TITLE);
    }

    /**
     * Test de suppression d'un livre
     */
    @Test
    @DisplayName("Supprimer un livre")
    void testDeleteBook() {
        // Créer un livre d'abord
        booksPage.createBook(BOOK_TITLE, BOOK_AUTHOR, BOOK_ISBN, BOOK_DESCRIPTION);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Supprimer le livre
        booksPage.deleteBook(BOOK_TITLE);

        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isSuccessMessageDisplayed(),
                "Un message de succès devrait être affiché après la suppression"
        );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        org.junit.jupiter.api.Assertions.assertFalse(
                booksPage.isBookPresent(BOOK_TITLE),
                "Le livre supprimé ne devrait plus exister"
        );
    }

    /**
     * Test de la validation des champs du formulaire
     */
    @Test
    @DisplayName("Vérifier la validation du formulaire")
    void testFormValidation() {
        // Essayer de créer un livre sans les champs requis
        booksPage.clickAddBookButton();

        // Soumettre un formulaire vide
        booksPage.submitBookForm();

        // Vérifier qu'une erreur est affichée
        org.junit.jupiter.api.Assertions.assertTrue(
                booksPage.isErrorMessageDisplayed(),
                "Un message d'erreur devrait être affiché pour les champs vides"
        );
    }

}
