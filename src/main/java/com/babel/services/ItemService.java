package com.babel.services;

import com.babel.entities.Item;
import com.babel.entities.ItemType;
import com.babel.entities.User;
import com.babel.exceptions.IllegalFileFormatException;
import com.babel.exceptions.NotAnItemException;
import com.babel.repositories.ItemRepo;
import com.babel.repositories.ItemTypeRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ItemService {
    public static final String BABEL_RESOURCE_FOLDER = System.getProperty("user.dir");
    private static final String[] PERMITTED_FILE_TYPES = {"pdf"};
    private final ItemRepo itemRepo;
    private final ItemTypeRepo itemTypeRepo;
    private final AuthorItemRelationshipService authorItemRelationshipService;

    public ItemService(ItemRepo itemRepo, ItemTypeRepo itemTypeRepo, AuthorItemRelationshipService authorItemRelationshipService) {
        this.itemRepo = itemRepo;
        this.itemTypeRepo = itemTypeRepo;
        this.authorItemRelationshipService = authorItemRelationshipService;
    }

    public Item getItem(int id) throws NotAnItemException {
        Item item = itemRepo.findById(id).orElse(null);
        if (item == null) throw new NotAnItemException("This item does not exist");
        return item;
    }

    public List<Item> getItems() {
        return itemRepo.findAll();
    }

    /**
     * Saves item to db and disk given its file, type, and user (needed for ownership)
     *
     * @param file
     * @param type
     * @param user
     * @return
     * @throws IllegalFileFormatException
     * @throws IOException
     */
    public Item saveItem(MultipartFile file, ItemType type, User user) throws IllegalFileFormatException, IOException {
        Item item = new Item(type, saveItemFileToDisk(file, type), user);
        itemRepo.save(item);
        return item;
    }

    /**
     * Removes item from db and disk given its id
     *
     * @param item
     * @throws IOException
     * @throws NotAnItemException
     */
    public void deleteItem(Item item) throws IOException {
        removeItemFromDisk(item);
        authorItemRelationshipService.deleteAuthorItemRelationship(item);
        itemRepo.delete(item);
    }

    /**
     * Get item file bytes
     *
     * @param item
     * @return
     * @throws IOException
     */
    public byte[] fetchItemFileBytes(Item item) throws IOException {
        String absoluteItemPath = BABEL_RESOURCE_FOLDER + File.separatorChar + item.getType().getType() + File.separatorChar + item.getFilePath();
        return Files.readAllBytes(Path.of(absoluteItemPath));
    }

    /**
     * Get item file
     *
     * @param item
     * @return
     */
    public File fetchItemFile(Item item) {
        String absoluteItemPath = BABEL_RESOURCE_FOLDER + File.separatorChar + item.getType().getType() + File.separatorChar + item.getFilePath();
        return new File(absoluteItemPath);
    }

    /**
     * Save item to disk
     * TODO Fully test
     *
     * @param file
     * @param itemType
     * @return fileName
     * @throws IllegalFileFormatException
     * @throws IOException
     */
    private String saveItemFileToDisk(MultipartFile file, ItemType itemType) throws IllegalFileFormatException, IOException {
        //Fetch the itemType
        String itemTypeString = itemType.getType();
        //Build the folder if it's not there already
        String fileFolderPath = BABEL_RESOURCE_FOLDER + File.separatorChar + itemTypeString;
        File fileFolder = new File(fileFolderPath);
        if (!Files.exists(Paths.get(fileFolderPath)))
            fileFolder.mkdir();
        //Check file format
        if (!Objects.equals(file.getContentType(), "application/pdf"))
            throw new IllegalFileFormatException("Book is not of accepted type: " + getPermittedFileFormats());
        //Is name empty?
        StringBuilder localFileName = new StringBuilder(Objects.requireNonNull(file.getOriginalFilename()));
        if (localFileName.length() == 0)
            localFileName = new StringBuilder(itemTypeString);
        //Duplicate check
        String filePath = fileFolderPath + File.separatorChar + localFileName;
        while (Files.exists(Paths.get(filePath))) {
            localFileName.append("-").append(Objects.requireNonNull(fileFolder.list()).length);
            filePath = fileFolderPath + File.separatorChar + localFileName;
        }
        //Save locally
        Files.write(Paths.get(filePath), file.getBytes());
        return localFileName.toString();
    }

    /**
     * Remove item from disk
     * TODO: test
     *
     * @param item
     * @throws IOException
     */
    private void removeItemFromDisk(Item item) throws IOException {
        Files.delete(Paths.get(getAbsolutePathOfItem(item)));
    }

    /**
     * Get available file formats in a nice... format
     *
     * @return formatted string
     */
    private String getPermittedFileFormats() {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(PERMITTED_FILE_TYPES).forEach(permittedFileType -> sb.append(permittedFileType + ", "));
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    /**
     * Fetch the absolute path of (any) given item
     *
     * @param item
     * @return
     */
    private String getAbsolutePathOfItem(Item item) {
        return BABEL_RESOURCE_FOLDER + File.separatorChar + item.getType().getType() + File.separatorChar + item.getFilePath();

    }
}
