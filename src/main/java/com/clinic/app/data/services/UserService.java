package com.clinic.app.data.services;

import com.clinic.app.data.repositories.UserRepository;
import com.clinic.app.entities.User;
import org.mindrot.jbcrypt.BCrypt;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Service class for user management operations
 */
public class UserService {
    private static UserService instance;
    private UserRepository userRepository;
    private boolean databaseAvailable = true;
    private Map<String, User> mockUsers = new ConcurrentHashMap<>();

    private UserService() {
        try {
            this.userRepository = new UserRepository();
            // Test database connection
            userRepository.findAll();
        } catch (Exception e) {
            System.err.println("Database not available, using mock data: " + e.getMessage());
            this.databaseAvailable = false;
            // Create a mock admin user for testing
            createMockAdminUser();
        }
    }

    private void createMockAdminUser() {
        User admin = new User();
        admin.setId(1);
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@clinic.com");
        admin.setPasswordHash(BCrypt.hashpw("admin123", BCrypt.gensalt()));
        admin.setRole("ADMIN");
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        mockUsers.put(admin.getEmail(), admin);

        // Create a mock regular user for testing
        User user = new User();
        user.setId(2);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@clinic.com");
        user.setPasswordHash(BCrypt.hashpw("test123", BCrypt.gensalt()));
        user.setRole("USER");
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        mockUsers.put(user.getEmail(), user);
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Register a new user
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email
     * @param password Plain text password
     * @param role User role (USER, ADMIN, DOCTOR)
     * @return User object if successful, null otherwise
     */
    public User registerUser(String firstName, String lastName, String email, String password, String role) {
        if (!databaseAvailable) {
            // Mock implementation
            if (mockUsers.containsKey(email)) {
                System.err.println("Email already exists: " + email);
                return null;
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            User user = new User();
            user.setId(mockUsers.size() + 1);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPasswordHash(hashedPassword);
            user.setRole(role.toUpperCase());
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            mockUsers.put(email, user);
            System.out.println("User registered successfully with ID: " + user.getId() + " (mock)");
            return user;
        }

        // Database implementation
        if (emailExists(email)) {
            System.err.println("Email already exists: " + email);
            return null;
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);
        user.setRole(role.toUpperCase());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        try {
            User savedUser = userRepository.save(user);
            System.out.println("User registered successfully with ID: " + savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Authenticate user login
     * @param email User's email
     * @param password Plain text password
     * @return User object if authentication successful, empty Optional otherwise
     */
    public Optional<User> authenticateUser(String email, String password) {
        if (!databaseAvailable) {
            // Mock implementation
            User user = mockUsers.get(email);
            if (user != null && user.isActive()) {
                if (BCrypt.checkpw(password, user.getPasswordHash())) {
                    System.out.println("User authenticated successfully: " + email + " (mock)");
                    return Optional.of(user);
                } else {
                    System.out.println("Invalid password for email: " + email + " (mock)");
                }
            } else {
                System.out.println("No active user found with email: " + email + " (mock)");
            }
            return Optional.empty();
        }

        // Database implementation
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent() && userOpt.get().isActive()) {
                User user = userOpt.get();
                String hashedPassword = user.getPasswordHash();
                if (BCrypt.checkpw(password, hashedPassword)) {
                    updateLastLogin(user.getId());
                    System.out.println("User authenticated successfully: " + email);
                    return Optional.of(user);
                } else {
                    System.out.println("Invalid password for email: " + email);
                }
            } else {
                System.out.println("No active user found with email: " + email);
            }
        } catch (Exception e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Check if email already exists
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (!databaseAvailable) {
            // Mock implementation
            return mockUsers.containsKey(email);
        }

        // Database implementation
        try {
            Optional<User> user = userRepository.findByEmail(email);
            return user.isPresent();
        } catch (Exception e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update user's last login time
     * @param userId User ID
     */
    private void updateLastLogin(Integer userId) {
        if (!databaseAvailable) {
            // Mock implementation - no need to update in mock mode
            return;
        }

        // Database implementation
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        } catch (Exception e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }

    /**
     * Get user by ID
     * @param userId User ID
     * @return User object if found, empty Optional otherwise
     */
    public Optional<User> getUserById(Integer userId) {
        if (!databaseAvailable) {
            // Mock implementation
            for (User user : mockUsers.values()) {
                if (user.getId().equals(userId) && user.isActive()) {
                    return Optional.of(user);
                }
            }
            return Optional.empty();
        }

        // Database implementation
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent() && userOpt.get().isActive()) {
                return userOpt;
            }
        } catch (Exception e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Get user by email
     * @param email User email
     * @return User object if found, empty Optional otherwise
     */
    public Optional<User> getUserByEmail(String email) {
        if (!databaseAvailable) {
            // Mock implementation
            User user = mockUsers.get(email);
            if (user != null && user.isActive()) {
                return Optional.of(user);
            }
            return Optional.empty();
        }

        // Database implementation
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent() && userOpt.get().isActive()) {
                return userOpt;
            }
        } catch (Exception e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Update user password
     * @param userId User ID
     * @param newPassword New plain text password
     * @return true if successful, false otherwise
     */
    public boolean updatePassword(Integer userId, String newPassword) {
        if (!databaseAvailable) {
            // Mock implementation
            for (User user : mockUsers.values()) {
                if (user.getId().equals(userId)) {
                    String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                    user.setPasswordHash(hashedPassword);
                    user.setUpdatedAt(LocalDateTime.now());
                    System.out.println("Password updated successfully for user ID: " + userId + " (mock)");
                    return true;
                }
            }
            return false;
        }

        // Database implementation
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
                user.setPasswordHash(hashedPassword);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                System.out.println("Password updated successfully for user ID: " + userId);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deactivate user account
     * @param userId User ID
     * @return true if successful, false otherwise
     */
    public boolean deactivateUser(Integer userId) {
        if (!databaseAvailable) {
            // Mock implementation
            for (User user : mockUsers.values()) {
                if (user.getId().equals(userId)) {
                    user.setActive(false);
                    user.setUpdatedAt(LocalDateTime.now());
                    System.out.println("User deactivated successfully: " + userId + " (mock)");
                    return true;
                }
            }
            return false;
        }

        // Database implementation
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setActive(false);
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                System.out.println("User deactivated successfully: " + userId);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if database is available
     * @return true if database is available, false otherwise
     */
    public boolean isDatabaseAvailable() {
        return databaseAvailable;
    }
}