package org.ati.core.repository;

import org.ati.core.model.VoteForUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteForUserRepository extends JpaRepository<VoteForUser, Long> {

    @Query("SELECT V FROM ANSWER V WHERE V.taskId = ?#{#task} AND V.userId = ?#{#user}")
    VoteForUser getVoteByUserAndTask(@Param("task") String taskId, @Param("user") String userId);
}
