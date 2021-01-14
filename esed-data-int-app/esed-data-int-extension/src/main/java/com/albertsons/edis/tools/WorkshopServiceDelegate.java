package com.albertsons.edis.tools;

import com.albertsons.edis.Delegate;
import com.albertsons.edis.DelegateParameter;
import com.albertsons.edis.cms.ContentRepositoryService;
import com.albertsons.edis.tools.xmlbeans.Buffalo;
import com.albertsons.edis.tools.xmlbeans.XmlSchemaBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Component
@Delegate("Workshop")
public class WorkshopServiceDelegate {

    @Autowired
    ContentRepositoryService repositoryService;

    private File repositoryHome;
    private File cmmHome;
    private File projectHome;


    @PostConstruct
    public void init() {
        repositoryHome = new File(repositoryService.getRepositoryHome().getFile());
        cmmHome = new File(repositoryHome, "CMM/BOD");
        projectHome = new File(repositoryHome, "BusinessObjects");
    }

    @Delegate("CMM")
    public List<String> cmmList() {
        List<String> list = new ArrayList<>();
        for(File file: cmmHome.listFiles()) {
            if(file.isFile() && file.getName().endsWith(".xsd")) {
                list.add(file.getName());
            }
        }

        return list;
    }

    @Delegate("projects")
    public List<String> projectList() {
        List<String> list = new ArrayList<>();
        for(File file: projectHome.listFiles()) {
            if(file.isDirectory() && new File(file, "application.json").exists()) {
                list.add(file.getName());
            }
        }

        return list;
    }

    @Delegate("workflow")
    public String workflow(@DelegateParameter("project") String project) throws IOException {
        File projectDir = new File(projectHome, project);
        File workflowFile = new File(projectDir, "workflow.yaml");
        String contents = IOUtils.toString(new FileInputStream(workflowFile), Charset.defaultCharset());

        return contents;
    }

    @Delegate("process-workflow")
    public String processWorkflow(@DelegateParameter("project") String project, @DelegateParameter("renderer") String renderer) throws IOException {
        File projectDir = new File(projectHome, project);
        File workflowFile = new File(projectDir, "workflow.yaml");
        String yaml = IOUtils.toString(new FileInputStream(workflowFile), Charset.defaultCharset());

        return Buffalo.fromYaml(yaml, XmlSchemaBase.class).render(renderer);
    }

}
