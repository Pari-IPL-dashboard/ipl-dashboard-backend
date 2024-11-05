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
    public ResponseEntity<String> triggerBatchJob() {
        try {
            jobRunner.run("triggerJob");
        } catch (Exception e){
            return  new ResponseEntity<>("Job failed", HttpStatusCode.valueOf(500));
        }
        return  new ResponseEntity<>("Job completed successfully", HttpStatusCode.valueOf(200));
    }
}
