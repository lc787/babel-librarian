package com.babel.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.babel.entities.Book;
import com.babel.entities.BookInformation;
import com.babel.entities.Item;
import com.babel.entities.User;
import com.babel.exceptions.IllegalFileFormatException;
import com.babel.exceptions.NoQueryResultsException;
import com.babel.exceptions.NotABookException;
import com.babel.repositories.BookRepo;
import com.babel.repositories.ItemRepo;
import com.babel.repositories.ItemTypeRepo;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentInformation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class BookService {
    private final BookRepo bookRepo;
    private final ItemRepo itemRepo;
    private final ItemTypeRepo itemTypeRepo;
    private final ItemService itemService;
    private final LibGenApiService libGenApiService;

    public BookService(BookRepo bookRepo, ItemRepo itemRepo, ItemTypeRepo itemTypeRepo, ItemService itemService, LibGenApiService libGenApiService) {
        this.bookRepo = bookRepo;
        this.itemRepo = itemRepo;
        this.itemTypeRepo = itemTypeRepo;
        this.itemService = itemService;
        this.libGenApiService = libGenApiService;
    }

    public Book getBook(int id) throws NotABookException {
        Book book = bookRepo.findById(id).orElse(null);
        if (book == null) throw new NotABookException("This book doesnt exist");
        return book;
    }

    public List<Book> getBooks() {
        return bookRepo.findAll();
    }


    public Book addBook(MultipartFile file, User user) throws IllegalFileFormatException, IOException {
        /*if (itemTypeRepo.findByType"book").isEmpty()) {
            ItemType itemType = new ItemType("book");
            itemTypeRepo.save(itemType);
        }*/
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


    }


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
     * Turn number of bytes into a readable format
     *
     * @param size
     * @return
     */
    private String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public String removeOneDotExtension(String s) {
        int index = s.length() - 1;
        while (s.charAt(index) != '.') {
            index--;
            }
        return s.substring(0, index);
    }
}
