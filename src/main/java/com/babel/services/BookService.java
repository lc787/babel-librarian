package com.babel.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.babel.entities.*;
import com.babel.exceptions.*;
import com.babel.repositories.BookRepo;
import com.babel.repositories.ItemRepo;
import com.babel.repositories.ItemTypeRepo;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentInformation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class BookService {
    private final BookRepo bookRepo;
    private final ItemRepo itemRepo;
    private final ItemTypeRepo itemTypeRepo;
    private final ItemService itemService;
    private final LibGenApiService libGenApiService;
    private final GenreService genreService;

    private final AuthorService authorService;
    private final AuthorItemRelationshipService authorItemRelationshipService;

    public BookService(BookRepo bookRepo, ItemRepo itemRepo, ItemTypeRepo itemTypeRepo, ItemService itemService, LibGenApiService libGenApiService, GenreService genreService, AuthorService authorService, AuthorItemRelationshipService authorItemRelationshipService) {
        this.bookRepo = bookRepo;
        this.itemRepo = itemRepo;
        this.itemTypeRepo = itemTypeRepo;
        this.itemService = itemService;
        this.libGenApiService = libGenApiService;
        this.genreService = genreService;
        this.authorService = authorService;
        this.authorItemRelationshipService = authorItemRelationshipService;
    }


    //TODO Test
    public void deleteBook(Book book) throws IOException {
        bookRepo.delete(book);
        itemService.deleteItem(book.getItem());
    }

    public Book getBook(int id) throws NotABookException {
        Book book = bookRepo.findById(id).orElse(null);
        if (book == null) throw new NotABookException("This book doesnt exist");
        return book;
    }

    public List<Book> getBooks() {
        return bookRepo.findAll();
    }

    public BookInformation getBookInformation(int id) throws NotABookException {
        Book book = getBook(id);
        return convertBookToInfo(book);
    }


    // Get all book information along with their id
    public Map<Integer, BookInformation> getAllBookInfo() {
        List<Book> bookList = getBooks();
        Map<Integer, BookInformation> map = new HashMap<>();
        bookList.forEach(book -> map.put(book.getId(), convertBookToInfo(book)));
        return map;
    }


    public BookInformation convertBookToInfo(Book book) {
        List<Author> authorList = authorItemRelationshipService.getAuthorsOfItem(book.getItem());
        List<String> authorNames = new ArrayList<>();
        for (Author author : authorList) {
            authorNames.add(author.getName());
        }
        BookInformation bookInformation = new BookInformation();
        bookInformation.setAuthors(authorNames);
        bookInformation.setSeries(book.getSeries());
        bookInformation.setYear(book.getYear());
        bookInformation.setPublisher(book.getPublisher());
        bookInformation.setTitle(book.getTitle());
        bookInformation.setGenre(book.getGenre().getName());
        bookInformation.setEdition(book.getEdition());
        bookInformation.setVolumeInfo(book.getVolumeInfo());
        return bookInformation;
    }

    /**
     * Saves book to disk and adds an item entry. Item id is sent back as a contract identifier.
     * TODO: use jwt as contract identifier
     *
     * @param file
     * @param user
     * @return
     * @throws IllegalFileFormatException
     */
    public Item startSaveContract(MultipartFile file, User user) throws IllegalFileFormatException, IOException, GenericDatabaseException {
        ItemType itemType;
        if ((itemType = itemTypeRepo.findByType("book").orElse(null)) == null)
            throw new GenericDatabaseException("No item type found in database: '" + "book" + "'");
        Item item = itemService.saveItem(file, itemType, user);
        return item;
    }

    //TODO: replace generic exception (see if try block is really needed)
    public Map<Integer, BookInformation> endSaveContract(BookInformation bookInfo, int itemId) throws Exception {
        try {
            //Get item
            Item item = itemService.getItem(itemId);
            if (bookRepo.findByItem(item).isPresent())
                throw new ExpiredContractException("The contract with id " + itemId + " has expired");
            //Add genre
            Genre genre = genreService.addGenre(bookInfo.getGenre());
            //Add authors-item relationship while adding authors
            for (String authorName : bookInfo.getAuthors())
                authorItemRelationshipService.addAuthorItemRelationship(item, authorService.addAuthor(authorName));
            //Finally, create the book
            Book book = new Book(item, bookInfo.getTitle(), bookInfo.getPublisher(), bookInfo.getYear(), bookInfo.getSeries(), bookInfo.getEdition(), genre, bookInfo.getVolumeInfo());
            //Save it on db
            bookRepo.save(book);
            Map<Integer, BookInformation> map = new HashMap<>();
            map.put(book.getId(), convertBookToInfo(book));
            //Return
            return map;
        } catch (IllegalGenreNameException | IllegalAuthorNameException | NotAnItemException e) {
            throw new Exception(e.getMessage());
        }
    }

    //----------GATHER METADATA

    /**
     * Wrapper for book metadata-pulling of disk saved item
     *
     * @param item
     * @return
     * @throws IOException
     */
    public BookInformation getBookMetadata(Item item) throws IOException {
        return convertPdfInfo(getPdfInfo(item));
    }

    /**
     * Gets PdfDocumentInformation format of given disk saved item
     *
     * @param item
     * @return
     * @throws IOException
     */
    private PdfDocumentInformation getPdfInfo(Item item) throws IOException {
        PdfDocument pdfItem = new PdfDocument();
        pdfItem.loadFromBytes(itemService.fetchItemFileBytes(item));
        return pdfItem.getDocumentInformation();
    }

    /**
     * Converts PdfDocumentInformation format to BookInformation
     *
     * @param pdfInfo
     * @return
     */
    private BookInformation convertPdfInfo(PdfDocumentInformation pdfInfo) {
        BookInformation bookInfo = new BookInformation();
        bookInfo.setTitle(pdfInfo.getTitle());
        //bookInfo.setYear();
        //bookInfo.setVolumeInfo();
        bookInfo.setAuthors(separateAuthors(pdfInfo.getAuthor()));
        //bookInfo.setEdition();
        //bookInfo.setEdition();
        //bookInfo.setPublisher();
        //bookInfo.setSeries();
        return bookInfo;
    }

    /**
     * Create a list of comma separated authors
     *
     * @param authors
     * @return
     */
    public List<String> separateAuthors(String authors) {
        //Separate strings
        List<String> authorList = Arrays.stream(authors.split(",", -1)).toList();
        //Strip front and back spaces
        authorList = authorList.stream().map(String::strip).toList();
        return authorList;
    }
    //----------DEPRECATED

    /**
     * Depracated. TODO delete
     * @param file
     * @param user
     * @return
     * @throws IllegalFileFormatException
     * @throws IOException
     */
   /* public Book oldAddBook(MultipartFile file, User user) throws IllegalFileFormatException, IOException {
        /*if (itemTypeRepo.findByType"book").isEmpty()) {
            ItemType itemType = new ItemType("book");
            itemTypeRepo.save(itemType);
        }
        Item item = itemService.saveItem(file, itemTypeRepo.findById(52).get(), user);
        //Get book title from metadata
        PdfDocument pdfItem = new PdfDocument();
        pdfItem.loadFromBytes(file.getBytes());
        PdfDocumentInformation pdfInfo = pdfItem.getDocumentInformation();
        //Scrape libgen
        //This is a mess
        BookInformation bookInformation = null;
        String title = pdfInfo.getTitle();
        if (title == null || title.isEmpty())
            title = removeOneDotExtension(file.getOriginalFilename());
        try {
            bookInformation = libGenApiService.fetchPossibleBookInfo(title, 1).get(0);
            System.out.println(title);
            Book book = new Book(item, bookInformation.getTitle(), bookInformation.getPublisher(), bookInformation.getYear(), bookInformation.getSeries(), bookInformation.getEdition(), null);
            bookRepo.save(book);
            return book;
        } catch (NoQueryResultsException e) {
            Book book = new Book(item, title, pdfInfo.getProducer(), null, null, null, null);
            System.out.println(title + "\n" + pdfInfo.getProducer());
            bookRepo.save(book);
            return book;
        }
        //Save the book
*/

    //-----------UTILITY


    /**
     * WIP. Build a JWT for contract identification
     *
     * @param issuer
     * @param subject
     * @return
     */
    public String buildJwt(String issuer, String subject) {
        String jws;
        try {
            Algorithm algorithm = Algorithm.HMAC512("JSKBUIWJTRB2345B232BIUGRBI2B34I");
            jws = JWT.create()
                    .withIssuer(issuer)
                    .withSubject(subject)
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            e.printStackTrace();
            return null;
        }
        return jws;
    }

    /**
     * Turn byte size into a readable format
     *
     * @param size number of bytes
     * @return
     */
    private String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Remove extension from file name. Experimental. TODO Test
     *
     * @param s
     * @return
     */
    public String removeOneDotExtension(String s) {
        int index = s.length() - 1;
        while (s.charAt(index) != '.') {
            index--;
        }
        return s.substring(0, index);
    }
}
