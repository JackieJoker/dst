package dst.ass2.service.auth.impl;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.model.IRider;
import dst.ass1.jpa.model.impl.Rider;
import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.auth.ICachingAuthenticationService;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ManagedBean
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class CachingAuthenticationService implements ICachingAuthenticationService {
    @PersistenceContext
    private EntityManager em;
    @Inject
    private IDAOFactory daoFactory;
    // <Email, Password>
    // Password is stored as sha1 hash sums, NOT clear as plain text
    private ConcurrentHashMap<String, byte[]> emailPassword;
    // <Token, Email>
    private ConcurrentHashMap<String, String> tokenEmail;

    private static byte[] toSHA1(String password) {
        byte[] bytePassword = password.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md != null ? md.digest(bytePassword) : new byte[0];
    }

    @PostConstruct
    @Override
    public void loadData() {
        emailPassword = new ConcurrentHashMap<>();
        tokenEmail = new ConcurrentHashMap<>();

        IRiderDAO riderDAO = daoFactory.createRiderDAO();
        List<IRider> users = riderDAO.findAll();
        users.forEach(user -> emailPassword.put(user.getEmail(), user.getPassword()));
    }

    @Override
    @Lock(LockType.READ)
    public String authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        byte[] hashedPassword = toSHA1(password);
        if (!emailPassword.containsKey(email)) {
            IRiderDAO riderDAO = daoFactory.createRiderDAO();
            IRider user = riderDAO.findByEmail(email);
            if (user == null) throw new NoSuchUserException();
            emailPassword.put(email, user.getPassword());
        }
        // here the email is in cache
        if (Arrays.equals(emailPassword.get(email), hashedPassword)) {
            UUID token = UUID.randomUUID();
            tokenEmail.put(token.toString(), email);

            return token.toString();
        } else throw new AuthenticationException();
    }

    @Override
    @Transactional
    @Lock(LockType.WRITE)
    public void changePassword(String email, String newPassword) throws NoSuchUserException {
        byte[] hashedPassword = toSHA1(newPassword);
        IRiderDAO riderDAO = daoFactory.createRiderDAO();
        IRider userQuery = riderDAO.findByEmail(email);
        if (userQuery == null) throw new NoSuchUserException();
        Rider user = em.find(Rider.class, userQuery.getId());
        user.setPassword(hashedPassword);
        emailPassword.replace(email, hashedPassword);
    }

    @Override
    public String getUser(String token) {
        return tokenEmail.get(token);
    }

    @Override
    public boolean isValid(String token) {
        return tokenEmail.containsKey(token);
    }

    @Override
    public boolean invalidate(String token) {
        if (!tokenEmail.containsKey(token)) return false;
        tokenEmail.remove(token);
        return true;
    }

    @Override
    public void clearCache() {
        emailPassword.clear();
    }
}
