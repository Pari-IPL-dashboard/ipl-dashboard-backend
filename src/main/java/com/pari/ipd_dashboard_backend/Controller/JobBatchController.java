package com.pari.ipd_dashboard_backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pari.ipd_dashboard_backend.Config.JobRunner;

@RestController
public class JobBatchController {

    @Autowired
    JobRunner jobRunner;
    
    @PostMapping("/job/trigger")
    public ResponseEntity<String> triggerBatchJob() throws Exception {
        jobRunner.run("triggerJob");
        return  new ResponseEntity<>("Job triggered successfully", HttpStatusCode.valueOf(204));
    }
}
