package data.denarius.radarius.scheduler;

import data.denarius.radarius.entity.Criterion;
import data.denarius.radarius.entity.Region;
import data.denarius.radarius.repository.CriterionRepository;
import data.denarius.radarius.repository.RegionRepository;
import data.denarius.radarius.service.AlertLogService;
import data.denarius.radarius.service.CriterionService;
import data.denarius.radarius.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class LevelScheduler {
    @Autowired
    private AlertLogService alertLogService;
    @Autowired
    private RegionRepository regionService;
    @Autowired
    private CriterionRepository criterionService;

    @Scheduled(fixedRate = 2 * (60 * 1000))
    public void checkLevels() {
        System.out.println("Checking levels...");
        Short previousLevel = (short) (new Random().nextInt(5) + 1);
        Short newLevel = (short) (new Random().nextInt(5) + 1);
        Region region = regionService.findById(1).orElse(null);
        Criterion criterion = criterionService.findById(1).orElse(null);

        if (!newLevel.equals(previousLevel)) {
//            alertLogService.create(newLevel, criterion, region);
        }
    }
}
