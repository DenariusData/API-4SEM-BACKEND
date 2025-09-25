package data.denarius.radarius.service;

import data.denarius.radarius.entity.User;

public interface UserService {
    User findByEmail(String email);
}
