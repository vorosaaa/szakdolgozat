package org.ati.core.repository;

import org.ati.core.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM TASK t WHERE LOWER(t.assignedUser.username) = LOWER(:username) ")
    List<Task> listTasksByUser(@Param("username") String username);
}
