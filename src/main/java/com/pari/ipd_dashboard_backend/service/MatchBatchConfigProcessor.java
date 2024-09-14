package com.pari.ipd_dashboard_backend.service;

import javax.sql.DataSource;

import com.pari.ipd_dashboard_backend.Config.JobCompletionNotificationListener;
import com.pari.ipd_dashboard_backend.Model.Match;
import com.pari.ipd_dashboard_backend.Model.MatchDTO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableBatchProcessing
public class MatchBatchConfigProcessor {

    private final String[] FIELD_NAMES = new String[] { "id", "city", "date", "player_of_match", "venue",
            "neutral_venue", "team1", "team2", "toss_winner", "toss_decision", "winner", "result", "result_margin",
            "eliminator", "method", "umpire1", "umpire2" };


    @Autowired
    DataSource dataSource;

    @Autowired
    JobRepository jobRepository;

    @Bean
    public Job importMatchJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step1) {
        return new JobBuilder("importMatchJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<MatchDTO, Match>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer(dataSource))  // Inject your DataSource bean in the writer method.
                .build();
    }

    @Bean
    public FlatFileItemReader<MatchDTO> reader() {
        return new FlatFileItemReaderBuilder<MatchDTO>()
                .name("MatchItemReader")
                .resource(new ClassPathResource("match-data.csv"))
                .delimited()
                .names(FIELD_NAMES)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(MatchDTO.class);
                    }
                })
                .build();
    }

    @Bean
    public MatchDataProcessor processor() {
        return new MatchDataProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Match> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Match>()
                .itemSqlParameterSourceProvider(new ItemSqlParameterSourceProvider<Match>() {
                    @Override
                    public SqlParameterSource createSqlParameterSource(Match match) {
                        return new MapSqlParameterSource()
                                .addValue("id", match.getId())
                                .addValue("city", match.getCity())
                                .addValue("date", match.getDate())
                                .addValue("playerOfMatch", match.getPlayerOfMatch())
                                .addValue("venue", match.getVenue())
                                .addValue("team1", match.getTeam1())
                                .addValue("team2", match.getTeam2())
                                .addValue("tossWinner", match.getTossWinner())
                                .addValue("tossDecision", match.getTossDecision())
                                .addValue("matchWinner", match.getMatchWinner())
                                .addValue("result", match.getResult())
                                .addValue("resultMargin", match.getResultMargin())
                                .addValue("umpire1", match.getUmpire1())
                                .addValue("umpire2", match.getUmpire2());
                    }
                })
                .sql("INSERT INTO match (id, city, date, player_of_match, venue, team1, team2, toss_winner, toss_decision, match_winner, result, result_margin, umpire1, umpire2) "
                        + "VALUES (:id, :city, :date, :playerOfMatch, :venue, :team1, :team2, :tossWinner, :tossDecision, :matchWinner, :result, :resultMargin, :umpire1, :umpire2)")
                .dataSource(dataSource)
                .build();
    }


}