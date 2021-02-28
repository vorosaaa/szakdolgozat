package org.ati.core.service;

import org.ati.core.model.Task;
import org.ati.core.model.VoteForUser;
import org.ati.core.repository.TaskRepository;
import org.ati.core.repository.VoteForUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private VoteForUserRepository voteForUserRepository;

    public List<Task> listTasks(String username) {
        return taskRepository.listTasksByUser(username);
    }

    public Task getOne(Long id) {
        return taskRepository.getOne(id);
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public VoteForUser save(VoteForUser vote) {
        return voteForUserRepository.save(vote);
    }

    public VoteForUser getVote(Long id) {
        return voteForUserRepository.getOne(id);
    }

    public VoteForUser getVoteByTaskAndUser(String taskId, String userId) {
        return voteForUserRepository.getVoteByUserAndTask(taskId, userId);
    }
}
