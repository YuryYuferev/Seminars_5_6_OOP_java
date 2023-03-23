package notebook.repository.impl;

import notebook.logger.Log;
import notebook.model.User;
import notebook.repository.*;
import notebook.mapper.impl.UserMapper;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
public class UserRepository implements GBRepository<User, Long> {
    private final UserMapper mapper;
    private final String fileName;
    private static notebook.logger.Log Log;
    private static final Logger log = Log.log(UserRepository.class.getName());
    public UserRepository(UserMapper mapper, String fileName) {
        this.mapper = new UserMapper();
        this.fileName = fileName;
    }
    @Override
    public List<User> findAll() {
        List<String> lines = readAll();
        List<User> users = new ArrayList<>();
        for (String line : lines) {
            users.add(mapper.toOutput(line));
        }
        log.log(Level.INFO, "Çàïðîñ âñåãî ñïèñêà êîíòàêòîâ");
        return users;
    }
    @Override
    public User create(User user) {
        List<User> users = findAll();
        long max = 0L;
        for (User u : users) {
            long id = u.getId();
            if (max < id) {
                max = id;
            }
        }
        long next = max + 1;
        user.setId(next);
        log.log(Level.INFO, "Íàçíà÷åí èäåíòèôèêàòîð äëÿ íîâîãî êîíòàêòà: " + next);
        users.add(user);
        write(users);
        log.log(Level.INFO, "Íîâûé êîíòàêò óñïåøíî ñîçäàí");
        return user;
    }
    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }
    @Override
    public Optional<User> update(Long userId, User update) {
        List<User> users = findAll();
        User editUser = null;
        for (User u : users) {
            if (u.getId().equals(userId)) {
                editUser = u;
            }
        }
        if (editUser == null) {
            log.log(Level.WARNING, "Êîíòàêò íå íàéäåí. Îòñóòñòâóåò èäåíòèôèêàòîð: " + userId);
            throw new NullPointerException("User not found");
        }
        editUser.setFirstName(update.getFirstName());
        editUser.setLastName(update.getLastName());
        editUser.setPhone(update.getPhone());
        write(users);
        log.log(Level.INFO, "Èçìåíåíû äàííûå êîíòàêòà id: " + editUser.getId());
        log.log(Level.INFO, "Èìÿ êîíòàêòà èçìåíåíî íà: " + editUser.getFirstName());
        log.log(Level.INFO, "Ôàìèëèÿ êîíòàêòà èçìåíåíà íà: " + editUser.getLastName());
        log.log(Level.INFO, "Òåëåôîí êîíòàêòà èçìåíåí íà: " + editUser.getPhone());
        return Optional.of(update);
    }
    @Override
    public boolean delete(Long userId) {
        List<User> users = findAll();
        User deleteUser = null;
        for (User u : users) {
            if (u.getId().equals(userId)) {
                deleteUser = u;
            }
        }
        if (deleteUser == null) {
            log.log(Level.WARNING, "Êîíòàêò íå íàéäåí. Îòñóòñòâóåò id: " + userId);
            throw new NullPointerException("User not found");
        }
        users.remove(deleteUser);
        log.log(Level.INFO, "Óñïåøíîå óäàëåíèå êîíòàêòà id: " + deleteUser.getId());
        write(users);
        return true;
    }
    private void write(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(mapper.toInput(u));
        }
        saveAll(lines);
        log.log(Level.INFO, "Óñïåøíîå ñîõðàíåíèå");
    }

    @Override
    public List<String> readAll() {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            if (line != null) {
                lines.add(line);
            }
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    lines.add(line);
                }
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    @Override
    public void saveAll(List<String> data) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            for (String line : data) {
                writer.write(line);
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}