package com.pari.ipd_dashboard_backend.service;

import com.pari.ipd_dashboard_backend.Model.Match;
import com.pari.ipd_dashboard_backend.Model.MatchDTO;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

public class MatchDataProcessor implements ItemProcessor<MatchDTO, Match> {


    @Override
    public Match process(final MatchDTO MatchDTO) throws Exception {

        Match match = new Match();
        match.setId(Long.parseLong(MatchDTO.getId()));
        match.setCity(MatchDTO.getCity());
        match.setDate(LocalDate.parse(MatchDTO.getDate()));
        match.setPlayerOfMatch(MatchDTO.getPlayer_of_match());
        match.setVenue(MatchDTO.getVenue());

        // Set Team 1 and Team 2 depending on the innings order
        String firstInningsTeam, secondInningsTeam;

        if ("bat".equals(MatchDTO.getToss_decision())) {
            firstInningsTeam = MatchDTO.getToss_winner();
            secondInningsTeam = MatchDTO.getToss_winner().equals(MatchDTO.getTeam1())
                    ? MatchDTO.getTeam2() : MatchDTO.getTeam1();

        } else {
            secondInningsTeam = MatchDTO.getToss_winner();
            firstInningsTeam = MatchDTO.getToss_winner().equals(MatchDTO.getTeam1())
                    ? MatchDTO.getTeam2() : MatchDTO.getTeam1();
        }
        match.setTeam1(firstInningsTeam);
        match.setTeam2(secondInningsTeam);
        match.setTossWinner(MatchDTO.getToss_winner());
        match.setTossDecision(MatchDTO.getToss_decision());
        match.setMatchWinner(MatchDTO.getWinner());
        match.setResult(MatchDTO.getResult());
        match.setResultMargin(MatchDTO.getResult_margin());
        match.setUmpire1(MatchDTO.getUmpire1());
        match.setUmpire2(MatchDTO.getUmpire2());
        System.out.println(match);
        return match;
    }
}