package com.babel.services;

import com.babel.entities.BookInformation;
import com.babel.exceptions.NoQueryResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibGenApiService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

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
        return getBooksInfoFromLibGenBookId(getMd5s(urlEncode(query), limit).stream().map(this::getLibGenBookId).collect(Collectors.toList()));
    }


    /**
     * Compatibility layer for libgen query system
     *
     * @param query
     * @return
     */
    public String urlEncode(String query) {
        System.out.println(URLEncoder.encode(query, StandardCharsets.UTF_8));
        return URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    /**
     * Scrape MD5s of books from HTML page
     *
     * @param query Query to find books
     * @return A list of MD5 values
     */
    public List<String> getMd5s(String query, int limit) throws NoQueryResultsException {
        System.out.println("Inside md5 method: " + query);
        String uri = "/search.php?req=" + query + "&open=0&res=25&view=simple&phrase=1&column=def";
        //Get html
        List<String> stringList = libGenApiClient.get()
                .uri(uri)
                .accept(MediaType.TEXT_HTML)
                .retrieve()
                .bodyToFlux(String.class).collectList().block(REQUEST_TIMEOUT);
        System.out.println("[HTMLBEGIN]" + stringList + "[HTMLEND]");
        //Get md5s
        List<String> filteredMd5s = new ArrayList<>();
        stringList.stream().filter(string -> string.contains("?md5=")).forEach(string -> {
            String newString = string.split("md5=")[1].split("'")[0]; //Can't possibly throw
            if (newString != null)
                filteredMd5s.add(newString);
        });
        //Remove dupes
        List<String> cleanMd5s = filteredMd5s.stream().distinct().toList();
        //Limit
        List<String> stringerList = cleanMd5s.stream().limit(limit).toList();
        if (stringerList.size() == 0)
            throw new NoQueryResultsException("No query results");
        return stringerList;
    }

    /**
     * Scrape libgen book id from md5
     *
     * @param md5
     * @return id
     */
    public String getLibGenBookId(String md5) {
        List<String> stringList = libGenApiClient.get()
                .uri("/book/bibtex.php?md5=" + md5)
                .accept(MediaType.TEXT_HTML)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .block(REQUEST_TIMEOUT);
        return stringList.stream().filter(string -> string.contains("@book{book:")).toList().get(0).split("book:")[1].split(",")[0]; //Can't possibly throw
    }

    public List<BookInformation> getBooksInfoFromLibGenBookId(List<String> ids) {
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
