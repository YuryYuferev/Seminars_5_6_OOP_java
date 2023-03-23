package notebook.repository;

import notebook.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface GBRepository<E, I> {
    List<User> findAll();
    User create(User user);
    Optional<User> findById(Long id);
    Optional<User> update(Long userId, User update);
    boolean delete(Long userId);
    List<String> readAll();
    void saveAll(List<String> data) throws IOException;
}