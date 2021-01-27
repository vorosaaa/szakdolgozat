package org.ati.web.controller;

import org.ati.core.model.Group;
import org.ati.core.model.Task;
import org.ati.core.service.GroupService;
import org.ati.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class GroupController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/groups")
    public String groupView(Model model, @ModelAttribute("group") Group group, Principal principal) {
        List<Group> groupList = new ArrayList<>();
        for (Group temp : groupService.listAllGroup()) {
            if (temp.getMembers().contains(userService.findByUsername(principal.getName()))) {
                groupList.add(temp);
            }
        }

        model.addAttribute("users", userService.listAllUsers());
        model.addAttribute("user", userService.findByUsername(principal.getName()).getUsername());
        model.addAttribute("groups", groupList);

        return "groups";
    }

    @PostMapping("/saveGroup")
    public String addGroup(@ModelAttribute("group") Group group,
                           BindingResult result, Principal principal, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "redirect:/groups";
        }

        List<String> listOfIds = new ArrayList<>();
        if (request.getParameterValues("usersOfGroup") != null) {
            listOfIds.addAll(Arrays.asList(request.getParameterValues("usersOfGroup")));
        }
        listOfIds.add(String.valueOf(userService.findByUsername(principal.getName()).getId()));

        for (String id : listOfIds) {
            group.getMembers().add(userService.getOne(Long.parseLong(id)));
        }
        group.setAdmin(userService.findByUsername(principal.getName()));
        groupService.save(group);
        return "redirect:/groups";
    }

    @GetMapping("/groups/{id}")
    public String getGroup(Model model, @PathVariable Long id) {
        Group group = groupService.getOne(id);
        model.addAttribute("group", group);
        model.addAttribute("taskList", group.getTasks());
        model.addAttribute("taskObj", new Task());
        model.addAttribute("members", group.getMembers());

        return ("group");
    }

    @PostMapping("/deleteGroup/{id}")
    public String deleteGroup(@PathVariable Long id, Principal principal) {
        Group group = groupService.getOne(id);
        if (group.getAdmin() == userService.findByUsername(principal.getName())) {
            groupService.deleteGroup(groupService.getOne(id));
        }

        return "redirect:/groups";
    }
}
