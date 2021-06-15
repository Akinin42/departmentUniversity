package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.GroupDto;
import org.university.service.GroupService;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping()
    public String getAll(Model model) {       
        model.addAttribute("groups", groupService.findAllGroups());
        return "group/all_groups";
    }
    
    @GetMapping("/new")
    public String createGroup(Model model) {       
        model.addAttribute("group", new GroupDto()); 
        return "group/new_group";
    }

    @PostMapping()
    public String add(@ModelAttribute("group") GroupDto group) {        
        groupService.addGroup(group);  
        return "redirect:/groups";
    }
}
