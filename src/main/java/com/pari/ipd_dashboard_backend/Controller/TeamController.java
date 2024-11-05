package com.pari.ipd_dashboard_backend.Controller;

import com.pari.ipd_dashboard_backend.Model.Match;
import com.pari.ipd_dashboard_backend.Model.Team;
import com.pari.ipd_dashboard_backend.repository.MatchRepository;
import com.pari.ipd_dashboard_backend.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MatchRepository matchRepository;

    @GetMapping("/")
    public ResponseEntity<Iterable<Team>> getAllTeams(){
        return new ResponseEntity<>(teamRepository.findAll(), HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{teamName}")
    public ResponseEntity<Team> getTeamByTeamName(@PathVariable("teamName") String teamName){
        Team team = teamRepository.findByTeamName(teamName);
        if(team == null){
            return new ResponseEntity<>(null, HttpStatusCode.valueOf(404));
        }
        team.setMatches(matchRepository.findLatestMatchesbyTeam(teamName,4));
        return new ResponseEntity<>(team, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{teamName}/matches")
    public List<Match> getMatchesForTeam(@PathVariable String teamName, @RequestParam int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year + 1, 1, 1);
        return matchRepository.getMatchesByTeamBetweenDates(
                teamName,
                startDate,
                endDate
        );
    }
}
