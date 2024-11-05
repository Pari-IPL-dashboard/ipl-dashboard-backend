package com.pari.ipd_dashboard_backend.repository;

import com.pari.ipd_dashboard_backend.Model.Team;
import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<Team, Long> {

    Iterable<Team> findAll();
    Team findByTeamName(String teamName);
}
