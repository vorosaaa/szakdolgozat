package org.ati.core.service;

import org.ati.core.model.Skills;
import org.ati.core.repository.SkillsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SkillsService {
    @Autowired
    SkillsRepository skillsRepository;

    public void deleteFlow(Skills skill) {
        skillsRepository.delete(skill);
    }

    public List<Skills> listAllFlow() {
        return skillsRepository.findAll();
    }

    public Skills save(Skills skills) {
        return skillsRepository.save(skills);
    }

    public boolean existsById(Long id) {
        return skillsRepository.existsById(id);
    }

    public Skills getOne(Long id) {
        return skillsRepository.getOne(id);
    }
}
