package org.ati.core.service;

import org.ati.core.model.Comment;
import org.ati.core.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }

    public List<Comment> listAllComment() {
        return commentRepository.findAll();
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public boolean existsById(Long id) {
        return commentRepository.existsById(id);
    }

    public Comment getOne(Long id) {
        return commentRepository.getOne(id);
    }
    
}
