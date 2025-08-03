package com.construction.crm.repository;

import com.construction.crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByDepartment(String department);
    
    List<User> findByPosition(String position);
    
    List<User> findByEnabled(boolean enabled);
    
    @Query("SELECT u FROM User u WHERE u.lastLoginDate >= :since")
    List<User> findActiveUsersSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRole(@Param("roleName") String roleName);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.hireDate >= :startDate AND u.hireDate <= :endDate")
    Long countHiredBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT u.department, COUNT(u) FROM User u GROUP BY u.department")
    List<Object[]> countByDepartment();
}