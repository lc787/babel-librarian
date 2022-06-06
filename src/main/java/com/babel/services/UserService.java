package com.babel.services;

import com.babel.entities.User;
import com.babel.exceptions.IllegalPasswordException;
import com.babel.exceptions.IllegalUsernameException;
import com.babel.exceptions.NotAUserException;
import com.babel.exceptions.WrongPasswordException;
import com.babel.repositories.UserRepo;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    public static final int MIN_PASSWORD = 20;
    public static final int MAX_PASSWORD = 256;
    public static final int MIN_USERNAME = 3;
    public static final int MAX_USERNAME = 32;
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    //----GET
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public User getUser(int id) throws NotAUserException {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) throw new NotAUserException("No user with such id registered");
        return user;
    }

    public User getUser(String username) throws NotAUserException {
        try {
            checkUsername(username); // We want no malicious input
        } catch (IllegalUsernameException e) {
            throw new NotAUserException("No username registered");
        }
        User user = userRepo.findByUsername(username).orElse(null);
        if (user == null) throw new NotAUserException("No username registered");
        return user;
    }
    //-----UPDATE?


    //-----DELETE
    public void deleteUser(int id) {
        userRepo.deleteById(id);
    }

    //-----ADD USER
    public void addUser(String username, String password) throws IllegalPasswordException, IllegalUsernameException {
        checkUsername(username);
        if (userRepo.findByUsername(username).isPresent())
            throw new IllegalUsernameException("This username is already taken");
        checkPassword(password);

        String salt = generateSalt();
        System.out.println("Generated salt: [" + generateSalt() + "]");
        User user = new User(username, salt, encryptPassword(password, salt));
        user.setCreationDate(Date.valueOf(LocalDate.now()));
        userRepo.save(user);
    }

    //-----LOGIN
    public void validateCredentials(String username, String password) throws NotAUserException, WrongPasswordException {
        //Check username
        try {
            checkUsername(username);
        } catch (IllegalUsernameException e) {
            throw new NotAUserException("No such user registered");
        }
        //Check if user exists
        User user = getUser(username);
        //Check password
        try {
            checkPassword(password);
        } catch (IllegalPasswordException e) {
            throw new WrongPasswordException("Wrong password");
        }

        String trueHashedPassword = user.getPassword();
        System.out.println("True hash: [" + trueHashedPassword + "]");
        String loginHashedPassword = encryptPassword(password, user.getSalt());
        System.out.println("Login hash: [" + loginHashedPassword + "]");
        System.out.println("Fetched salt: [" + user.getSalt() + "]");
        if (!trueHashedPassword.equals(loginHashedPassword)) throw new WrongPasswordException("Wrong password");

        //Logged in!
    }

    /**
     * Hard coded password checking.
     * Min password length - 20
     * Max password length - 255
     * Must contain at least 2 numbers, 5 letters, 2 uppercase and 2 special
     *
     * @param password
     * @throws IllegalPasswordException
     */
    public void checkPassword(String password) throws IllegalPasswordException {
        if(password == null)
            throw new IllegalPasswordException("Empty password field");
        if (password.length() < 20)
            throw new IllegalPasswordException("Password is too short. Minimum allowed length is " + MIN_PASSWORD);
        if (password.length() > 256)
            throw new IllegalPasswordException("Password is too long. Maximum allowed length is " + MAX_PASSWORD);
        if (!password.matches("[A-z0-9!@#$%^&*]+"))
            throw new IllegalPasswordException("Password contains illegal characters. A password may only contain alphanumeric and special characters('!@#$%^&*')");
     /*   if (!password.matches("[A-z]{5}"))
            throw new IllegalPasswordException("Password needs to contain at least 5 letters");
        if (!password.matches("(.*[A-Z]){2}"))
            throw new IllegalPasswordException("Password needs to contain at least 2 uppercase letters");
        if (!password.matches("(.*[0-9]){2}"))
            throw new IllegalPasswordException("Password needs to contain at least 2 digits");
        if (!password.matches("(.*[!|@|#|$|%|^|&|*|_]){2}"))
            throw new IllegalPasswordException("Password needs to contain at least 2 special characters ('!@#$%^&*_')"); */

    }

    /**
     * Hard coded username checking.
     * Min username length - 4
     * Max username length - 31
     * Must contain at least 2 letters and may contain numbers and '_'
     *
     * @param username
     * @throws IllegalUsernameException
     */
    public void checkUsername(String username) throws IllegalUsernameException {
        if(username == null)
            throw new IllegalUsernameException("Empty username field");
        if (username.length() < 4)
            throw new IllegalUsernameException("Username is too short. Minimum allowed length is " + MIN_USERNAME);
        if (username.length() > 32)
            throw new IllegalUsernameException("Username is too long. Maximum allowed length is " + MAX_USERNAME);
        if (!username.matches("[A-z|0-9|_]+"))
            throw new IllegalUsernameException("Username contains illegal characters. A username may only contain alphanumeric characters and '_'");
      /*  if (!username.matches("(.*[A-z]){2}"))
            throw new IllegalUsernameException("Username needs to contain at least 2 letters"); */
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[64];
        random.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }

    private String encryptPassword(String password, String salt) {
        //Generate encrypted password
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(password.getBytes(StandardCharsets.UTF_8));
        md.update(salt.getBytes(StandardCharsets.UTF_8)); //safe?
        //Pepper
        md.update("3bg7829f2340d1ei3jgdu3r9fh89134d9dig2398204h72".getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        return Base64.encodeBase64String(digest);
    }
}
