package com.pari.ipd_dashboard_backend.Config;

import com.pari.ipd_dashboard_backend.Model.Team;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    EntityManager em;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job started: " + jobExecution.getJobInstance().getJobName());
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Job failed: " + jobExecution.getJobInstance().getJobName());
        } else {
            log.info("Job completed successfully: " + jobExecution.getJobInstance().getJobName());
            createTeams();
        }
    }

    // Annotation Transactional is important since EM is used
    /*
      The parent method doesn’t need @Transactional per se, but calling a @Transactional method directly from another method in the same class bypasses the transaction proxy.
      Using @Transactional on the parent (i.e., afterJob) ensures a transaction context because it would then be processed directly by the Spring AOP proxy.
      However, if you only want transactions in createTeams, the proxy approach with ApplicationContext is a way to trigger it without modifying afterJob.

     */
    @Transactional
    private void createTeams(){
        Map<String, Team> teamData = new HashMap<>();

        // get team names from team1 column
        em.createQuery("select m.team1, count(*) from Match m group by m.team1", Object[].class)
                .getResultList()
                .stream()
                .map(e -> new Team((String) e[0], (long) e[1]))
                .forEach(team -> teamData.put(team.getTeamName(), team));
        // get team names from team2 column
        em.createQuery("select m.team2, count(*) from Match m group by m.team2", Object[].class)
                .getResultList()
                .forEach(e -> {
                    Team currentTeam  = teamData.get((String) e[0]);
                    if(currentTeam != null) {
                        currentTeam.setTotalMatches(currentTeam.getTotalMatches() + (long) e[1]);
                    }
                    // neglecting case of when a team is available only in column2 for time being
                });

        em.createQuery("select m.matchWinner, count(*) from Match m group by m.matchWinner", Object[].class)
                .getResultList()
                .forEach(e -> {
                    Team team = teamData.get((String) e[0]);
                    if (team != null) team.setTotalWins((long) e[1]);
                });

        teamData.values().
                forEach(team -> em.persist(team));
        teamData.values().
                forEach(team -> System.out.println(team));

    }
}
