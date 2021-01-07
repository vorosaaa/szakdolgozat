package org.ati.core.service;

import org.ati.core.model.Group;
import org.ati.core.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GroupService {
    @Autowired
    GroupRepository groupRepository;

    public void deleteFlow(Group GROUP) {
        groupRepository.delete(GROUP);
    }

    public List<Group> listAllGroup() {
        return groupRepository.findAll();
    }

    public Group save(Group GROUP) {
        return groupRepository.save(GROUP);
    }

    public boolean existsById(Long id) {
        return groupRepository.existsById(id);
    }

    public Group getOne(Long id) {
        return groupRepository.getOne(id);
    }

}
