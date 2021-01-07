package org.ati.core.service;

import org.ati.core.model.Flow;
import org.ati.core.repository.FlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FlowService {

    @Autowired
    private FlowRepository flowRepository;

    public void deleteFlow(Flow flow) {
        flowRepository.delete(flow);
    }

    public List<Flow> listAllFlow() {
        return flowRepository.findAll();
    }

    public Flow save(Flow flow) {
        return flowRepository.save(flow);
    }

    public boolean existsById(Long id) {
        return flowRepository.existsById(id);
    }

    public Flow getOne(Long id) {
        return flowRepository.getOne(id);
    }
}
