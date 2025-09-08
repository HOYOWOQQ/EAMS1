package com.eams.common.Configuration.Services;

import java.util.List;
import java.util.ArrayList;

public class TemplateInfo {
    private Integer id;
    private String templateKey;
    private String templateName;
    private String subject;
    private String content;
    private List<String> variables = new ArrayList<>();

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getTemplateKey() { return templateKey; }
    public void setTemplateKey(String templateKey) { this.templateKey = templateKey; }
    
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public List<String> getVariables() { return variables; }
    public void setVariables(List<String> variables) { this.variables = variables; }
}


