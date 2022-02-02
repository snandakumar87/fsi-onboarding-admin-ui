package org.acme;

public class CaseDetails {
    private String caseId;
    private String caseStatus;
    private String caseStartedAt;
    private String slaCompliance;
    private String processId;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getSlaCompliance() {
        return slaCompliance;
    }

    public void setSlaCompliance(String slaCompliance) {
        this.slaCompliance = slaCompliance;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCaseStatus() {
        return caseStatus;
    }

    public void setCaseStatus(String caseStatus) {
        this.caseStatus = caseStatus;
    }

    public String getCaseStartedAt() {
        return caseStartedAt;
    }

    public void setCaseStartedAt(String caseStartedAt) {
        this.caseStartedAt = caseStartedAt;
    }
}
