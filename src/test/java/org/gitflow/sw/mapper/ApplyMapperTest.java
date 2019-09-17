package org.gitflow.sw.mapper;

import lombok.extern.slf4j.Slf4j;
import org.gitflow.sw.dto.ApplyContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApplyMapperTest {

    @Resource
    private ApplyPartMapper applyPartMapper;
    @Resource
    private ApplyFormMapper applyFormMapper;
    @Resource
    private ApplyContentMapper applyContentMapper;

    @Test
    public void applyPart_findAll_TEST() {
        log.info("# {}", applyPartMapper.findAll());
    }

    @Test
    public void applyPart_findById_TEST() {
        log.info("# {}", applyPartMapper.findById(1));
    }

    @Test
    public void applyForm_findById_TEST() {
        log.info("# {}", applyFormMapper.findById(1));
    }

    @Test
    public void applyForm_findByPartId_TEST() {
        log.info("# {}", applyFormMapper.findByPartId(1));
    }

    @Test
    public void applyContent_findAllByPartId_TEST() {
        log.info("# {}", applyContentMapper.findAllByPartId(1));
        log.info("## {}", applyContentMapper.findAllByPartId(2));


    }

    @Test
    public void applyContent_findById_TEST() {
        List<ApplyContent> applyContents = applyContentMapper.findAllByPartId(1);
        for (ApplyContent applyContent : applyContents) {
            System.out.println(applyContentMapper.findById(applyContent.getId()));
        }
    }

    @Test
    public void applyContent_findByUserId_TEST() {
        log.info("# {}", applyContentMapper.findByUserId(1));
    }

    @Test
    public void applyContent_insert_TEST() {
        ApplyContent applyContent = new ApplyContent();
        applyContent.setUserId(1010);
        applyContent.setPartId(2);
        applyContent.setContent1("test1");
        applyContent.setContent2("test2");
        applyContentMapper.insert(applyContent);
    }

    @Test
    public void applyContent_update_TEST() {
        ApplyContent applyContent = applyContentMapper.findByUserId(1010);
        applyContent.setContent1("update test1");
        applyContent.setContent2("update test2");
        applyContent.setContent3("update test3");
        applyContentMapper.update(applyContent);
    }

    @Test
    public void applyContent_delete_TEST() {
        applyContentMapper.deleteByUserId(1010);
    }

}
