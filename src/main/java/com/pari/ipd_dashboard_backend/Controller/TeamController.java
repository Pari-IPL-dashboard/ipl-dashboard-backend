package com.pari.ipd_dashboard_backend.Controller;

import com.pari.ipd_dashboard_backend.Model.Team;
import com.pari.ipd_dashboard_backend.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    TeamRepository teamRepository;

    @GetMapping("/")
    public ResponseEntity<Iterable<Team>> getAllTeams(){
        return new ResponseEntity<>(teamRepository.findAll(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{teamName}")
    public ResponseEntity<Team> getTeamByTeamName(@PathVariable("teamName") String teamName){
        return new ResponseEntity<>(teamRepository.findByTeamName(teamName), HttpStatusCode.valueOf(200));
    }

}
