package com.pari.ipd_dashboard_backend.repository;

import com.pari.ipd_dashboard_backend.Model.Match;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface MatchRepository extends CrudRepository<Match, Long> {

    List<Match> findByTeam1OrTeam2OrderByDateDesc(String team1, String team2, Pageable pageable);

    // instead of using a separate daoimpl class to set pageable default method is used.
    // This is to separate repo naming with developer friendly method name
    default List<Match> findLatestMatchesbyTeam(String teamName, int count) {
        return findByTeam1OrTeam2OrderByDateDesc(teamName, teamName, PageRequest.of(0, count));
    }

    @Query("select m from Match m where (m.team1 = :teamName or m.team2 = :teamName) and m.date between :dateStart and :dateEnd order by date desc")
    List<Match> getMatchesByTeamBetweenDates( @Param("teamName") String teamName,
                                              @Param("dateStart") LocalDate dateStart,
                                              @Param("dateEnd") LocalDate dateEnd);
}
