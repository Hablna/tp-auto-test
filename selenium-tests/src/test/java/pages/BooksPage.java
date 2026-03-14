package pages;

import config.Config;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BooksPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Sélecteurs pour la page de liste des livres
    private final By addBookBtn = By.id("add-book-btn");
    private final By booksList = By.id("books-list");
    private final By bookItem = By.className("book-item");

    // Sélecteurs pour le formulaire de création/modification
    private final By titleInput = By.id("book-title");
    private final By authorInput = By.id("book-author");
    private final By isbnInput = By.id("book-isbn");
    private final By descriptionInput = By.id("book-description");
    private final By saveBtn = By.id("save-book-btn");
    private final By cancelBtn = By.id("cancel-btn");

    // Sélecteurs pour les actions sur les livres
    private final By editBtn = By.className("edit-btn");
    private final By deleteBtn = By.className("delete-btn");
    private final By confirmDeleteBtn = By.id("confirm-delete-btn");
    private final By cancelDeleteBtn = By.id("cancel-delete-btn");

    // Sélecteurs pour les messages de succès/erreur
    private final By successMessage = By.className("success-message");
    private final By errorMessage = By.className("error-message");
    private final By noResultsMessage = By.id("no-results");

    public BooksPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Navigue vers la page des livres
     */
    public void open() {
        driver.get(Config.BASE_URL + "/books");
    }

    /**
     * Clique sur le bouton pour ajouter un nouveau livre
     */
    public void clickAddBookButton() {
        wait.until(ExpectedConditions.elementToBeClickable(addBookBtn)).click();
    }

    /**
     * Remplit le formulaire de création/modification d'un livre
     */
    public void fillBookForm(String title, String author, String isbn, String description) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(titleInput)).clear();
        driver.findElement(titleInput).sendKeys(title);

        driver.findElement(authorInput).clear();
        driver.findElement(authorInput).sendKeys(author);

        driver.findElement(isbnInput).clear();
        driver.findElement(isbnInput).sendKeys(isbn);

        driver.findElement(descriptionInput).clear();
        driver.findElement(descriptionInput).sendKeys(description);
    }

    /**
     * Soumet le formulaire de création/modification
     */
    public void submitBookForm() {
        wait.until(ExpectedConditions.elementToBeClickable(saveBtn)).click();
    }

    /**
     * Crée un nouveau livre en remplissant et soumettant le formulaire
     */
    public void createBook(String title, String author, String isbn, String description) {
        clickAddBookButton();
        fillBookForm(title, author, isbn, description);
        submitBookForm();
    }

    /**
     * Recherche un livre par son titre dans la liste
     */
    public WebElement findBookByTitle(String title) {
        // Attendre que la liste des livres soit chargée
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(bookItem));

        // Chercher le livre avec le titre correspondant
        java.util.List<WebElement> books = driver.findElements(bookItem);
        for (WebElement book : books) {
            String bookTitle = book.findElement(By.className("book-title")).getText();
            if (bookTitle.equals(title)) {
                return book;
            }
        }
        return null;
    }

    /**
     * Consulte un livre en cliquant dessus (le sélectionne)
     */
    public void viewBook(String title) {
        WebElement book = findBookByTitle(title);
        if (book != null) {
            book.click();
        }
    }

    /**
     * Récupère les informations affichées d'un livre sélectionné
     */
    public BookInfo getBookInfo(String title) {
        WebElement book = findBookByTitle(title);
        if (book == null) {
            return null;
        }

        String bookTitle = book.findElement(By.className("book-title")).getText();
        String author = book.findElement(By.className("book-author")).getText();
        String isbn = book.findElement(By.className("book-isbn")).getText();
        String description = book.findElement(By.className("book-description")).getText();

        return new BookInfo(bookTitle, author, isbn, description);
    }

    /**
     * Modifie un livre existant
     */
    public void editBook(String title, String newTitle, String newAuthor, String newIsbn, String newDescription) {
        WebElement book = findBookByTitle(title);
        if (book != null) {
            book.findElement(editBtn).click();
            fillBookForm(newTitle, newAuthor, newIsbn, newDescription);
            submitBookForm();
        }
    }

    /**
     * Supprime un livre après confirmation
     */
    public void deleteBook(String title) {
        WebElement book = findBookByTitle(title);
        if (book != null) {
            book.findElement(deleteBtn).click();
            // Attendre la modal de confirmation
            wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn)).click();
        }
    }

    /**
     * Annule la suppression d'un livre
     */
    public void cancelDelete() {
        wait.until(ExpectedConditions.elementToBeClickable(cancelDeleteBtn)).click();
    }

    /**
     * Vérifie si un message de succès est affiché
     */
    public boolean isSuccessMessageDisplayed() {
        try {
            WebElement message = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(successMessage)
            );
            return message.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Récupère le texte du message de succès
     */
    public String getSuccessMessage() {
        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(successMessage)
        ).getText();
    }

    /**
     * Vérifie si un message d'erreur est affiché
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return driver.findElement(errorMessage).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Vérifie si un livre existe dans la liste
     */
    public boolean isBookPresent(String title) {
        return findBookByTitle(title) != null;
    }

    /**
     * Vérifie si la liste est vide (aucun livre)
     */
    public boolean isListEmpty() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(noResultsMessage)
            ).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Compte le nombre de livres affichés dans la liste
     */
    public int getBooksCount() {
        java.util.List<WebElement> books = driver.findElements(bookItem);
        return books.size();
    }

    /**
     * Classe interne pour stocker les informations d'un livre
     */
    public static class BookInfo {
        public String title;
        public String author;
        public String isbn;
        public String description;

        public BookInfo(String title, String author, String isbn, String description) {
            this.title = title;
            this.author = author;
            this.isbn = isbn;
            this.description = description;
        }

        @Override
        public String toString() {
            return "BookInfo{" +
                    "title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", isbn='" + isbn + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}
