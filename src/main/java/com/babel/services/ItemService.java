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

    public ItemService(ItemRepo itemRepo, ItemTypeRepo itemTypeRepo) {
        this.itemRepo = itemRepo;
        this.itemTypeRepo = itemTypeRepo;
    }

    public Item getItem(int id) throws NotAnItemException {
        Item item = itemRepo.findById(id).orElse(null);
        if (item == null) throw new NotAnItemException("This item does not exist");
        return item;
    }

    public List<Item> getItems() {
        return itemRepo.findAll();
    }

    public Item saveItem(MultipartFile file, ItemType type, User user) throws IllegalFileFormatException, IOException {
        Item item = new Item(type, saveItemToDisk(file, type), user);
        itemRepo.save(item);
        return item;
    }

    public void deleteItem(int id) throws IOException, NotAnItemException {
        removeItemFromDisk(getItem(id));
        itemRepo.deleteById(id);
    }

    private String saveItemToDisk(MultipartFile file, ItemType itemType) throws IllegalFileFormatException, IOException {
        //Build the folder if it's not there already
        String fileFolderPath = BABEL_RESOURCE_FOLDER + File.separatorChar + itemType.getType();
        File fileFolder = new File(fileFolderPath);
        if (!Files.exists(Paths.get(fileFolderPath)))
            fileFolder.mkdir();
        //Check file format
        if (!Objects.equals(file.getContentType(), "application/pdf"))
            throw new IllegalFileFormatException("Book is not of accepted type: " + getPermittedFileFormats());
        //Duplicate check
        String filePath = fileFolderPath + File.separatorChar + file.getOriginalFilename();
        if (Files.exists(Paths.get(filePath)))
            filePath = filePath + "-" + fileFolder.list().length;

        //Save locally
        Files.write(Paths.get(filePath), file.getBytes());

        return filePath;
    }

    private void removeItemFromDisk(Item item) throws IOException {
        Files.delete(Paths.get(item.getFilePath()));
    }

    /**
     * Get available file formats in a nice... format
     *
     * @return
     */
    private String getPermittedFileFormats() {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(PERMITTED_FILE_TYPES).forEach(permittedFileType -> sb.append(permittedFileType + ", "));
        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }
}
