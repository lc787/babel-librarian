package com.babel.services;

import com.babel.entities.BookInformation;
import com.babel.exceptions.NoQueryResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibGenApiService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private final WebClient libGenApiClient;

    @Autowired
    public LibGenApiService(WebClient libGenApiClient) {
        this.libGenApiClient = libGenApiClient;
    }

    /**
     * A wrapper for the entire scrape process
     *
     * @param query
     * @param limit Limit the amount of query results to analyze
     * @return
     */
    public List<BookInformation> fetchPossibleBookInfo(String query, int limit) throws NoQueryResultsException {
        return getBooksInfoFromLibGenBookId(getMd5s(query, limit).stream().map(this::getLibGenBookId).collect(Collectors.toList()));
    }

    /**
     * Scrape MD5s of books from HTML page
     *
     * @param query Query to find books
     * @return A list of MD5 values
     */
    private List<String> getMd5s(String query, int limit) throws NoQueryResultsException {
        //Get html
        List<String> stringList = libGenApiClient.get()
                .uri("/search.php?req=" + query)
                .accept(MediaType.TEXT_HTML)
                .retrieve()
                .bodyToFlux(String.class).collectList().block(REQUEST_TIMEOUT);
        //Get md5s
        List<String> filteredMd5s = new ArrayList<>();
        stringList.stream().filter(string -> string.contains("?md5=")).forEach(string -> {
            String newString = string.split("md5=")[1].split("'")[0]; //Can't possibly throw
            if (newString != null)
                filteredMd5s.add(newString);
        });
        //Remove dupes
        List<String> cleanMd5s = filteredMd5s.stream().distinct().collect(Collectors.toList());
        //Limit
        List<String> stringerList = cleanMd5s.stream().limit(5).toList();
        if (stringerList.size() == 0)
            throw new NoQueryResultsException("No query results. Defaulting to file metadata");
        return stringerList;
    }

    /**
     * Scrape libgen book id from md5
     *
     * @param md5
     * @return id
     */
    private String getLibGenBookId(String md5) {
        List<String> stringList = libGenApiClient.get()
                .uri("/book/bibtex.php?md5=" + md5)
                .accept(MediaType.TEXT_HTML)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .block(REQUEST_TIMEOUT);
        return stringList.stream().filter(string -> string.contains("@book{book:")).toList().get(0).split("book:")[1].split(",")[0]; //Can't possibly throw
    }

    private List<BookInformation> getBooksInfoFromLibGenBookId(List<String> ids) {
        StringBuilder uri = new StringBuilder("ids=");
        ids.forEach(id -> uri.append(id + ','));
        uri.append("&fields=Title,Author,Publisher,Year,Edition,Series,VolumeInfo");//,Publisher,Year,Edition,Series,VolumeInfo");

        List<BookInformation> bookInfo = libGenApiClient.get()
                .uri("/json.php?" + uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(BookInformation.class)
                .collectList()
                .block(REQUEST_TIMEOUT);
        bookInfo.forEach(System.out::println);
        return bookInfo;
        //Arrays.stream(booksInfo).forEach(bookInformation -> System.out.println(bookInformation.toString()));
        //return Arrays.stream(booksInfo).toList();
    }


}
